package org.geek.geeksearch.recommender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geek.geeksearch.configure.Configuration;
import org.geek.geeksearch.queryer.Response;
import org.geek.geeksearch.util.DBOperator;

public class CheckSpell {
	
//	static String[] keywords = { "信息检索", "数据检索", "贝叶斯", "分类器" };
	static Map<String, ArrayList<String>> gram_2_index = new HashMap<>();
	static Map<String, ArrayList<String>> gram_3_index = new HashMap<>();
	static Map<String, ArrayList<String>> start_2_index = new HashMap<>();
	static Map<String, ArrayList<String>> end_2_index = new HashMap<>();
	static Map<String, ArrayList<String>> start_3_index = new HashMap<>();
	static Map<String, ArrayList<String>> end_3_index = new HashMap<>();
	static NGramDistance ngram_dis = new NGramDistance();
	static float min = (float) 0.5;
	static float goalFreq = 0;
	static DBOperator dbOperator = new DBOperator(new Configuration());
	static Map<String,Integer> hot_words = create_ngram_index(); //检索历史，到一定size写入数据库
	
	/* 将查询词存入hot_words */
	public static void store_query(String query){
		if (!hot_words.containsKey(query))
			hot_words.put(query, 1);
		else
			hot_words.put(query, hot_words.get(query)+1);
		
		addGram(query);
	}
	
	/* 从数据库加载热词（keywords） */
	private static Map<String, Integer> loadHotWords() {
		Map<String, Integer> wordsMap = new HashMap<>();
		String sql = " SELECT * FROM PAGESINDEX ";//
		ResultSet rSet = dbOperator.executeQuery(sql);
		if (rSet == null) {
			System.err.println("load nothing from table PagesIndex!");
			return null;
		}
		String keywords;
		String[] words;
		try {
			while (rSet.next()) {
				keywords = rSet.getString("keywords");
				if (keywords == null || keywords.isEmpty()) {
					continue;
				}
				words = keywords.split("[、，。；？！,.;?! ]");
				for (String word : words) {
					if (word == null || word.isEmpty()) {
						continue;
					}
					word = word.trim();
					if (!wordsMap.containsKey(word))
						wordsMap.put(word, 1);
					else
						wordsMap.put(word, wordsMap.get(word)+1);
				}
//				System.out.println(id+" = "+term);
			}
		} catch (SQLException e) {
			System.err.println("error occurs while loading keywords");
			e.printStackTrace();
		}		
		return wordsMap;
	}
	
	/* 初始化hot_words, 只需一次初始化*/
	private static Map<String, Integer> create_ngram_index() {
		Map<String, Integer> wordsMap = loadHotWords();//从数据库加载

		//addGram
		Iterator<Map.Entry<String, Integer>> iter = wordsMap.entrySet().iterator();
//		System.out.println("hot_words:");
		while (iter.hasNext()) {
		    Map.Entry<String, Integer> entry = iter.next(); 
		    addGram(entry.getKey().toString());
//		    System.out.println(entry.getKey().toString()+"!!!!!!!!!");
		}
		return wordsMap;
	}

	private static void addGram(String text) {
		int len = text.length();
		int ng = 2;

		String end = null;
		for (int i = 0; i < len - ng + 1; i++) {
			String gram = text.substring(i, i + ng);
//			System.out.println(gram);
			if (!gram_2_index.containsKey(gram)) {
				gram_2_index.put(gram, new ArrayList<String>());
			}
			gram_2_index.get(gram).add(text);

			if (i == 0) {
				if (!start_2_index.containsKey(gram)) {
					start_2_index.put(gram, new ArrayList<String>());
				}
				start_2_index.get(gram).add(text);
			}
			end = gram;
		}
		if (end != null) { // may not be present if len==ng1
			if (!end_2_index.containsKey(end)) {
				end_2_index.put(end, new ArrayList<String>());
			}
			end_2_index.get(end).add(text);
		}
		ng = 3;
		for (int i = 0; i < len - ng + 1; i++) {
			String gram = text.substring(i, i + ng);
			if (!gram_3_index.containsKey(gram)) {
				gram_3_index.put(gram, new ArrayList<String>());
			}
			gram_3_index.get(gram).add(text);

			if (i == 0) {
				if (!start_3_index.containsKey(gram)) {
					start_3_index.put(gram, new ArrayList<String>());
				}
				start_3_index.get(gram).add(text);
			}
			end = gram;
		}
		if (end != null) { // may not be present if len==ng1
			if (!end_3_index.containsKey(end)) {
				end_3_index.put(end, new ArrayList<String>());
			}
			end_3_index.get(end).add(text);
		}
	}

	public static ArrayList<String> suggestSimilar(String word, int numSug) {

			final int freq;
			final int lengthWord = word.length();
			if(!hot_words.containsKey(word))
				freq=0;
			else
			    freq= hot_words.get(word);
			// if the word exists in the real index and we don't care for word
			// frequency, return the word itself
			if (freq > 0) {
//				return new String[] { word };
				return new ArrayList<String>();
			}
			BooleanQuery query = new BooleanQuery();
			String[] grams;
			String key;
			int ng = 2;
			key = "gram" + ng; 
			grams = formGrams(word, ng);
			if (grams.length != 0) {			
			
			query.start2 = grams[0];
			for (int i = 0; i < grams.length; i++)
				query.gram_2.add(grams[i]);
			query.end2 = grams[grams.length - 1];
			}
			ng = 3;
			grams = formGrams(word, ng); 
			if (grams.length != 0) {
			query.start3 = grams[0];
			for (int i = 0; i < grams.length; i++){
				query.gram_3.add(grams[i]);
//				System.out.println(grams[i]);
			}
			query.end3 = grams[grams.length - 1];
			}
//			query.for_print();
			System.out.println("hit:");
			SuggestWordQueue sugQueue = new SuggestWordQueue(numSug);

//			int stop = Math.min(hits.size(), maxHits);
			SuggestWord sugWord = new SuggestWord();
			Set<String> hits = search(query);
			Iterator iterator=hits.iterator();
		     while(iterator.hasNext()){
		    	 sugWord.string = (String) iterator.next();
		    	 
				
				// don't suggest a word for itself, that would be silly
				if (sugWord.string.equals(word)) {
					continue;
				}

				// edit distance
				sugWord.score = ngram_dis.getDistance(word, sugWord.string);
				System.out.println(sugWord.string+":"+sugWord.score);
				if (sugWord.score < min) {
					continue;
				}
				
				sugWord.freq = hot_words.get(sugWord.string); 
				if ((goalFreq > sugWord.freq)|| sugWord.freq < 1) {
						continue;
				}
				System.out.println(sugWord.string+":"+sugWord.score);
				 System.out.println("插入词："+sugWord.string);
				sugQueue.insertWithOverflow(sugWord);
				if (sugQueue.size() == numSug) {
					// if queue full, maintain the minScore score
					
					min = sugQueue.top().score;
					System.out.println("new_min:"+min);
				}
				sugWord = new SuggestWord();
			}

			// convert to array string
			ArrayList<String> list = new ArrayList<String>(sugQueue.size());
			System.out.println("推荐词：");
			for (int i = sugQueue.size() - 1; i >= 0; i--) {
				list.add(i, sugQueue.pop().string);
				System.out.println(list.get(i));
			}
			return list;
	}

	private static Set<String> search(BooleanQuery query) {
		// TODO Auto-generated method stub
		List<String> start_3 = new ArrayList<String>();
		List<String> start_2 = new ArrayList<String>();
		List<String> end_3 = new ArrayList<String>();
		List<String> end_2 = new ArrayList<String>();
		if(start_2_index.containsKey(query.start2))
			start_2 = gram_2_index.get(query.start2);
		if(start_3_index.containsKey(query.start3))
			start_3 = gram_3_index.get(query.start3);
		if(end_2_index.containsKey(query.end2))
			end_2 = gram_2_index.get(query.end2);
		if(end_3_index.containsKey(query.end3))
			end_3 = gram_3_index.get(query.end3);		

		Set<String> gram_2 = new HashSet<String>();
		Set<String> gram_3 = new HashSet<String>();
		for(int i = 0; i < query.gram_2.size();i++){
			if(gram_2_index.containsKey(query.gram_2.get(i)))
				gram_2.addAll(gram_2_index.get(query.gram_2.get(i)));
		}
		for(int i = 0; i < query.gram_3.size();i++){
			if(gram_3_index.containsKey(query.gram_3.get(i)))
				gram_3.addAll(gram_3_index.get(query.gram_3.get(i)));
		}
		Set<String> combine = new HashSet<String>();
		combine.addAll(gram_3);
		combine.addAll(gram_2);
		combine.addAll(start_2);
		combine.addAll(end_2);
		combine.addAll(start_3);
		combine.addAll(end_3);
		return combine;
	}

	private static String[] formGrams(String text, int ng) {
		int len = text.length();
		String[] res = new String[len - ng + 1];
		for (int i = 0; i < len - ng + 1; i++) {
			res[i] = text.substring(i, i + ng);
//			System.out.println(res[i]);
		}
		return res;
	}

	public static Map<String, Integer> getHot_words() {
		return hot_words;
	}

	public static void main(String[] args) {
//		create_ngram_index();
		Iterator iter = gram_2_index.entrySet().iterator();
/*		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			ArrayList<String> val = (ArrayList<String>) entry.getValue();
		}*/

	}
}
package org.geek.geeksearch.queryer;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.recommender.CheckSpell;


public class Response {


	private HashMap<String, ArrayList<String>> invertedIndexMap;
	private ArrayList<Result> results;	
//	public static HashMap<String,Integer> hot_words = new HashMap<String,Integer>();
	public static HashMap<String,Integer> hot_words = new HashMap<String,Integer>(){
		{put("姚明", 3);
		put("足球新闻", 4);
		put("篮球新闻", 5);}
	};
	boolean need_to_recommend = true;
	
	public Response()
	{
//		dictSeg = new Tokenizer();
	}
	public void query_store(String request)
	{
		if (!hot_words.containsKey(request))
			hot_words.put(request, 1);
		else
			hot_words.put(request, hot_words.get(request)+1);
	}
	public void hot_query_get_from_mysql()
	{
	}
	public String get_recommend_query(String requert){
		if(need_to_recommend){
			CheckSpell checkspell = new CheckSpell();
			checkspell.create_ngram_index();
			ArrayList<String> sug = checkspell.suggestSimilar(requert,3);			
			return new JSONArray().fromObject(sug).toString();
//			return sug.toString();
		}
		else
			return null;
	
	}
	public String getResponse(String request)
	{
		System.out.println("搜索词是： "+request);
		doQuery(request);
		//construct json and output it	
		JSONArray json_result = JSONArray.fromObject(results);
/*		JSONObject json = new JSONObject();
		JSONArray jsonMembers = new JSONArray();
		JSONObject member1 = new JSONObject();
		member1.put("loginname", "zhangfan");
		member1.put("password", "userpass");
		member1.put("email", "10371443@qq.com");
		member1.put("sign_date", "2007-06-12");
		jsonMembers.add(member1);

		JSONObject member2 = new JSONObject();
		member2.put("loginname", "zf");
		member2.put("password", "userpass");
		member2.put("email", "8223939@qq.com");
		member2.put("sign_date", "2008-07-16");
		jsonMembers.add(member2);
		json.put("users", jsonMembers);*/	

		return json_result.toString();
	}
	

	private void doQuery(String request) {
		

		results = new ArrayList<Result>();
		System.out.println("开始分词");
		System.out.println("分词结束 \n");
		for(int i = 0; i < 10; i++)
			results.add(new Result("姚明","姚明的篮球队","www.baidu.com","2013"));		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
	}

}

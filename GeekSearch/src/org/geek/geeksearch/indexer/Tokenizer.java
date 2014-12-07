package org.geek.geeksearch.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;
import org.geek.geeksearch.configure.Configuration;

/**
 * 分词器
 *
 */
public class Tokenizer {
	
	/* for index generating */
	public Tokenizer(Configuration config) {
		loadStopWordsLib(config.getValue("StopLibPath"));
		ToAnalysis.parse("");
	}
		
	/* 正文词条化
	 * 使用第三方分词工具ansj实现分词 
	 * 使用toAnalysis进行细粒度分词 ，用于构建索引
	 */
	public static List<String> doTokenise(String plainText) {
		// ToAnalysis
		System.out.println("--------------------"+plainText);
		List<Term> splitedTerms = ToAnalysis.parse(plainText);
		// 去除停用词
		splitedTerms = deleStopWords(splitedTerms);
		// 去除词性
		return cleanTerms(splitedTerms);
	}
	
	/*
	 * 使用NLP分词，粗粒度，用于对keywords分词，便于搜索词推荐
	 */
	public static List<String> doNLpTokenise(String query) {
		// 使用第三方分词工具ansj实现分词
		List<Term> splitedTerms = NlpAnalysis.parse(query);
		// 去除停用词
		splitedTerms = deleStopWords(splitedTerms);
		// 去除词性
		return cleanTerms(splitedTerms);
	}
	
	private static List<Term> deleStopWords(List<Term> splitedTerms) {
		// 过滤停用词（需要自定义停用词词典）
//		System.out.println(splitedTerms.toString());
		splitedTerms = FilterModifWord.modifResult(splitedTerms);
//		System.out.println(splitedTerms.toString());
		return splitedTerms;
	}
	
	private static void loadStopWordsLib(String path) {
		File file = new File(path);
		if (file.isDirectory() || !file.exists()) {
			System.err.println("can not find stopwords library: "+path);
			return;
		}
		try {
			String line = null;
			System.out.println("===================================");
			System.out.println("===================================");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				FilterModifWord.insertStopWord(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> cleanTerms(List<Term> splitedTerms) {
		List<String> parsedTerms = new ArrayList<>();
		String tmp = null;
		int idx = 0;
		for (Term term : splitedTerms) {
			tmp = term.toString();
			if (tmp != null && (idx = tmp.indexOf("/")) > 0) {
				parsedTerms.add(tmp.substring(0, idx));
			}
		}
		return parsedTerms;
	}
	
}

package org.geek.geeksearch.queryer;

import java.util.ArrayList;
import java.util.HashMap;

import org.geek.geeksearch.indexer.Tokenizer;


public class Response {


	private HashMap<String, ArrayList<String>> invertedIndexMap;
	private ArrayList<Result> results;

	private Tokenizer dictSeg;
	
	private HashMap<String,Integer> hot_words;
	public Response()
	{
//		dictSeg = new Tokenizer();
	}
	public void query_store(String request)
	{
		if (!hot_words.containsKey(request))
			hot_words.put(request, hot_words.get(request)+1);
		else
			hot_words.put(request, 0);
		
	}
	public void hot_query_get_from_mysql(String request)
	{
		
	}
	
	public ArrayList<Result> getResponse(String request)
	{
		System.out.println("搜索词是： "+request);
		doQuery(request);
		return results;
	}
	

	private void doQuery(String request) {
		

		results = new ArrayList<Result>();
//		ArrayList<String> keyWords = (ArrayList<String>) dictSeg.doQueryTokenise(request);
//		ArrayList<String> keyWords = null;
		System.out.println("开始分词");
//		for(String keyWord : keyWords)
//			System.out.println(keyWord);
		System.out.println("分词结束 \n");
		for(int i = 0; i < 10; i++)
			results.add(new Result("姚明","姚明的篮球队","www.baidu.com","2013"));		
//		for(String keyWord : keyWords){
//
//		}
					
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
	}

}

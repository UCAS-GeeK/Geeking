package org.geek.geeksearch.indexer;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

/**
 * 分词器
 *
 */
public class Tokenizer {
	
	/* 正文词条化
	 * 使用第三方分词工具ansj实现分词  
	 */
	public static List<String> doTextTokenise(String plainText) {
		// ToAnalysis ：精准分词
		List<Term> splitedTerms = ToAnalysis.parse(plainText);
		
		// 过滤停用词（尚未实现需要自定义停用词词典）
//		System.out.println(splitedTerms.toString());
//		splitedTerms = FilterModifWord.modifResult(splitedTerms);
//		System.out.println(splitedTerms.toString());
		// 去除词性
		return cleanTerms(splitedTerms);
	}
	
	public static List<String> doQueryTokenise(String query) {
		// 使用第三方分词工具ansj实现分词
		List<Term> splitedTerms = NlpAnalysis.parse(query);
		// 不过滤停用词
		// 去除词性
		return cleanTerms(splitedTerms);
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

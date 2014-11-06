package org.geek.geeksearch.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

/**
 * 分词器
 *
 */
public class Tokenizer {
	
	public Tokenizer() {
		String libPath = "E:\\eclipseWorkspace\\Geeking\\GeekSearch\\library\\stopLibrary.dic";
		loadStopWordsLib(libPath);
	}
	
	/* 正文词条化
	 * 使用第三方分词工具ansj实现分词  
	 */
	public List<String> doTextTokenise(String plainText) {
		// NlpAnalysis
		List<Term> splitedTerms = NlpAnalysis.parse(plainText);
		
		// 去除停用词
		splitedTerms = deleStopWords(splitedTerms);

		// 去除词性
		return cleanTerms(splitedTerms);
	}
	
	public List<String> doQueryTokenise(String query) {
		// 使用第三方分词工具ansj实现分词
		List<Term> splitedTerms = NlpAnalysis.parse(query);
		// 不过滤停用词
		// 去除词性
		return cleanTerms(splitedTerms);
	}
	
	private List<Term> deleStopWords(List<Term> splitedTerms) {
		// 过滤停用词（需要自定义停用词词典）
//		System.out.println(splitedTerms.toString());
		splitedTerms = FilterModifWord.modifResult(splitedTerms);
//		System.out.println(splitedTerms.toString());
		return splitedTerms;
	}
	
	private void loadStopWordsLib(String path) {
		File file = new File(path);
		if (file.isDirectory() || !file.exists()) {
			System.err.println("can not find stopwords library: "+path);
			return;
		}
		try {
			String line = null;
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
	
	public List<String> cleanTerms(List<Term> splitedTerms) {
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

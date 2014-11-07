package org.geek.geeksearch.queryer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geek.geeksearch.configure.Configuration;
import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.model.InvertedIndex;
import org.geek.geeksearch.model.PageInfo;
import org.geek.geeksearch.util.DBOperator;


public class QueryProcessor {
	private HashMap<String, Long> termIDsMap = new HashMap<>(); //词项-词项ID 映射表
	private Map<Long,InvertedIndex> invIdxMap = new HashMap<>(); //倒排索引表
	private HashMap<String,Integer> queryHistory = new HashMap<>(); //检索历史，到一定size写入数据库
	
	private final Configuration config;
	private final Tokenizer tokenizer;
	private final DBOperator dbOperator;
	
	public QueryProcessor() {
		this.config = new Configuration();
		this.dbOperator = new DBOperator(config);
		this.tokenizer = new Tokenizer();
		loadIndexes();
	}
	
	/* 检索入口 */
	public List<PageInfo> doQuery(String query) {
		// 分词 
		List<Long> queryIDs = parseQuery(query);
		if (queryIDs == null || queryIDs.isEmpty()) {
			System.out.println("nothing to search!");
			return null;
		}
		// 获取相关网页及信息
		List<PageInfo> resultPages = getResultPages(queryIDs);
		if (resultPages == null || resultPages.isEmpty()) {
			System.out.println("nothing retrived for query: "+ query);
			return null;
		}
		// 根据余弦相关度排序
		// 聚类(和排序有一定冲突)
		// snippet和快照在PageInfo.java中实现
		
		
		return resultPages;
	}
	
	/* 获取相关网页，并从数据库PagesIndex获取网页信息 */
	private List<PageInfo> getResultPages(List<Long> queryIDs) {
		List<PageInfo> resultPages = new ArrayList<>();

		List<Long> relevantDocs = getRelevantDocs(queryIDs);
		// for 
		return resultPages;
	}
	
	/* 获取各个词项的TopK篇文档，求并集 */
	private List<Long> getRelevantDocs(List<Long> queryIDs) {
		List<Long> relevantDocs = new ArrayList<>();
		//getTopK and merge
		return relevantDocs;
	}
	
	/* query解析 */
	private List<Long> parseQuery(String query) {
		// 分词
		List<String> queryTerms = tokenizer.doQueryTokenise(query);
		if (queryTerms == null || queryTerms.isEmpty()) {
			return null;
		}
		// 映射成ID
		List<Long> queryIDs = new ArrayList<>();
		for (String term : queryTerms) {
			if (term == null || term.isEmpty()) {
				continue;
			}
			//fetchTermID();
			//queryIDs.add()
		}
		return queryIDs;
	}
	
	/* 从数据库加载索引到内存 */
	private void loadIndexes() {
		loadInvertedIndex();
		loadTermsIndex();
	}
	
	/* 加载 InvertedIndex 表 */
	private void loadInvertedIndex() {
		//根据df和tf计算权重，排序topK
	}
	
	/* 加载 TermsIndex 表 */
	private void loadTermsIndex() {
		
	}
	
	
}

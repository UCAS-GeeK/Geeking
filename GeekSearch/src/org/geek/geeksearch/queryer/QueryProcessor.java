package org.geek.geeksearch.queryer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geek.geeksearch.configure.Configuration;
import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.model.InvertedIndex;
import org.geek.geeksearch.model.PageInfo;
import org.geek.geeksearch.model.TermStat;
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
		loadInvertedIndex();
		loadTermsIndex();
	}
	
	/* 检索入口 */
	public List<PageInfo> doQuery(String query) {
		// 分词 
		List<Long> queryIDs = parseQuery(query);
		if (queryIDs == null || queryIDs.isEmpty()) {
			System.out.println("nothing to search!");
			return null;
		}
		// 获取已排序的相关网页及信息
		List<PageInfo> resultPages = getResultPages(queryIDs);
		if (resultPages == null || resultPages.isEmpty()) {
			System.out.println("nothing retrived for query: "+ query);
			return null;
		}
		// 聚类: doCluster(resultPages);
		// snippet和快照在PageInfo.java中实现

		return resultPages;
	}
	
	/* 获取相关网页，并从数据库PagesIndex获取网页信息 */
	private List<PageInfo> getResultPages(List<Long> queryIDs) {
		List<PageInfo> resultPages = new ArrayList<>();
		
		List<TermStat> relevantDocs = getRelevantDocs(queryIDs);
		if (relevantDocs == null || relevantDocs.isEmpty()) {
			System.out.println("no pages retrived");
			return null;
		}
		//relevantDocs根据权重降序排列
		Collections.sort(relevantDocs, new Comparator<TermStat>() {
			public int compare(TermStat o1, TermStat o2) {
				return o2.getWeight() > o1.getWeight() ? 1 : -1;
			}
		});
		//从PagesIndex获取PageInfo
		PageInfo page;
		for (TermStat doc : relevantDocs) {
			page = new PageInfo(doc.getDocID());
			if (page.loadInfo(dbOperator)) {
				continue;
			}
			resultPages.add(page);
		}
		return resultPages;
	}
	
	/* 获取各个词项的TopK篇文档，计算相似度，求并集 */
	private List<TermStat> getRelevantDocs(List<Long> queryIDs) {
		List<TermStat> relevantDocs = new ArrayList<>();
		//for 从 invIdxMap 获取 每个词项的invertedIndex中的TopK个TermStat
			//relevantDocs = 和上个词项的TopK进行merge，并计算tf*idf,累加到TermStat.weight

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
			long id = fetchTermID(term);
			if (id < 0) {
				//跳过索引库中没有的词项
				continue;
			}
			queryIDs.add(id);
		}
		return queryIDs;
	}
	
	/* 从TermsIndex获取termID */
	private long fetchTermID(String term) {
		String sql = " SELECT * FROM TERMSINDEX WHERE term='"+term+"' ";
		ResultSet rSet = dbOperator.executeQuery(sql);
		long termID = -1;
		try {
			termID = rSet.getLong("TermID");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return termID;
	}

	/* 加载 InvertedIndex 表 */
	private void loadInvertedIndex() {
		String sql = " SELECT * FROM INVERTEDINDEX ";
		ResultSet rSet = dbOperator.executeQuery(sql);
		if (rSet == null) {
			System.err.println("load nothing from table InvertedIndex!");
			return;
		}		
		InvertedIndex invIdx;
		long termID = -1;
		String docIDs = "";
		try {
			while (rSet.next()) {
				termID = rSet.getLong("TermID");
				docIDs = rSet.getString("DocumentIDs");
				if (docIDs == null || docIDs.isEmpty() || termID < 0) {
					continue;
				}
				invIdx = new InvertedIndex(termID);
				invIdx.parseIndex(docIDs);
				invIdxMap.put(termID, invIdx);
			}
		} catch (SQLException e) {
			System.err.println("error occurs while loading termID: "+termID);
			e.printStackTrace();
		}
//		InvertedIndex.addAll2DB(invIdxMap, dbOperator); //just for test
	}

	/* 加载 TermsIndex 表 */
	private void loadTermsIndex() {
		String sql = " SELECT * FROM TERMSINDEX ";
		ResultSet rSet = dbOperator.executeQuery(sql);
		if (rSet == null) {
			System.err.println("load nothing from table TermsIndex!");
			return;
		}		
		String term = "";
		long id = -1;
		try {
			while (rSet.next()) {
				term = rSet.getString("Term");
				id = rSet.getLong("TermID");
				if (term == null || term.isEmpty() || id < 0) {
					continue;
				}
				termIDsMap.put(term, id);
				System.out.println(id+" = "+term);//
			}
		} catch (SQLException e) {
			System.err.println("error occurs while loading term: "+term);
			e.printStackTrace();
		}
	}
	
	/* just for test */
	public static void main(String[] args) {
		QueryProcessor queryProc = new QueryProcessor();
//		queryProc.doQuery("科比防守"); // 尚未完全实现
	}
	
}

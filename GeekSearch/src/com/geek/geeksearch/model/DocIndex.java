package com.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.geek.geeksearch.util.DBOperator;
import com.sun.org.apache.regexp.internal.recompile;

/**
 * 文档索引哈希表
 * 存储若干条文档索引记录
 *
 */
public class DocIndex {
//	// 用于从数据库读取时在内存维护多条索引，或用于写数据库时累积一定数目索引后写入
//	private HashMap<Long, ArrayList<Long>> docsIndex= new HashMap<>();

	public void addIndex(long docID, List<Long> docTermIDs, DBOperator dbOp) {
		if (docID < 0) {
			System.err.printf("bad docID: %s\n", docID);
			return;
		}
//		docsIndex.put(docID, docTermIDs);
		String docTerms = toString(docTermIDs);
		add2DB(dbOp, docID, docTerms);
	}
	
	public List<Long> toList() {
		List<Long> docTermIDs = new ArrayList<>();
		//
		return docTermIDs;
	}
	
	private String toString(List<Long> docTermIDs) {
		StringBuffer docTerms = new StringBuffer();
		for (Long termID : docTermIDs) {
			docTerms = docTerms.append("#").append(termID.toString());
		}
//		System.out.println(terms.toString());
		return docTerms.toString();	
	}

//	public ArrayList<String> getTermList(long docID) {
//		if (!docsIndex.containsKey(docID)) {
//			System.err.printf("bad docID: %s\n", docID);
//		}
//		return docsIndex.get(docID);
//	}
	
	/* 每有一条文档索引，就写一次数据库 */
	private void add2DB(DBOperator dbOp, long docID, String terms) {
		//
	}
	
//	/* 索引数目达到一定数目后一起写入数据库（后期实现） */
//	public void addAll2DB() {
//		//
//	}
	
}

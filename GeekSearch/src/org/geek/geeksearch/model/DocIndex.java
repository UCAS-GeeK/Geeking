package org.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geek.geeksearch.util.DBOperator;

import com.sun.org.apache.regexp.internal.recompile;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * 文档索引
 * 一次处理一条文档索引记录
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
	
	public static List<String> toList(String docs) {
		List<String> docTermIDs = new ArrayList<>();
		if (docs == null || docs.isEmpty()) {
			System.out.printf("invalid doc index record %s!", docs);
			return null;
		}
		String[] temp = docs.split("#");
		docTermIDs = Arrays.asList(temp);
		return docTermIDs;
	}
	
	private String toString(List<Long> docTermIDs) {
		StringBuffer docTerms = new StringBuffer();
		for (Long termID : docTermIDs) {
			docTerms = docTerms.append(termID.toString()).append("#");
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
		String sql = " INSERT INTO DocIndex values("+docID+",'"+terms+"') ";
		dbOp.executeUpdate(sql);
	}
	
//	/* 索引数目达到一定数目后一起写入数据库（后期实现） */
//	public void addAll2DB() {
//		//
//	}
	
}

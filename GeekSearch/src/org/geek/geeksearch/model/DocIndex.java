package org.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geek.geeksearch.util.DBOperator;

/**
 * 文档索引
 * 一次处理一条文档索引记录
 *
 */
public class DocIndex {
	
	public void addIndex(long docID, List<Long> docTermIDs, DBOperator dbOp) {
		if (docID < 0) {
			System.err.printf("bad docID: %s\n", docID);
			return;
		}
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
	
	/* 每有一条文档索引，就写一次数据库 */
	private void add2DB(DBOperator dbOp, long docID, String terms) {
		String sql = " INSERT INTO docsindex values("+docID+",'"+terms+"') ";
		dbOp.executeUpdate(sql);
	}
	
//	/* 索引数目达到一定数目后一起写入数据库（后期实现） */
//	public void addAll2DB() {
//		//
//	}
	
}

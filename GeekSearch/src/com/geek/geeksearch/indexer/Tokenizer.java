//package com.geek.geeksearch.indexer;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicLong;
//
//import org.ansj.domain.Term;
//import org.ansj.splitWord.analysis.IndexAnalysis;
//
//import com.geek.geeksearch.util.DBOperator;
//
///**
// * 分词器
// *
// */
//public class Tokenizer {
//	
//	private final DBOperator dbOp;
//	
//	public Tokenizer(DBOperator dbOp) {
//		this.dbOp = dbOp;
//	}
//	
//	/* 词条化 */
//	public List<String> doTokenise(String text, long docID) {
//		// 使用第三方分词工具ansj实现分词
//		List<Term> parsedTerms = IndexAnalysis.parse(text);
//		
//		//生成 词项ID-词项ID 映射表TermIdIndex
//		List<Long> termIDs = createTermIdIndex(parsedTerms);
//		
//		
//	}
//	
//
//	
//	private void addTermId2DB() {
////		termID.incrementAndGet();
////		DB: TermIdIndex
//		
//	}
//}

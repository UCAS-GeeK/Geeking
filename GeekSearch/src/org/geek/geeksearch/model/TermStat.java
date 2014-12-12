package org.geek.geeksearch.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 词项统计
 * 某词项在某文档中的统计信息
 *
 */
public class TermStat {
	private final long docID;
	private long termFreq = 0; // term frequency
	private Set<Long> posSet = null;//new HashSet<>(); // position set
	private long tfIdf = 0; //该文档和某个词项的tf-idf值
	/* 点乘结果:weight = Σ{检索词项权重(1)*该文档权重(tf-idf)}+标题中搜索词出现次数*10
	 * 在本类中，只和docID有关，是产生query最终结果时该文档和query的相似度
	 */
	private long weight = 0; 
	
	//计算tf-idf
	public long calcTfIdf(long idf) {
		/**********使用不带log的tf-idf**********/
		tfIdf = (long)(1+termFreq)*idf;
//		tfIdf = termFreq*idf;
		return tfIdf;
	}
	
	public void setTfIdf(long tfIdf, long df, long totalDocs) {
		/*由于构建索引时公式用的是不带log的，在此补偿
		 * 参数tfidf=totalDocs/df * (tf+1)反解
		*/
		this.tfIdf = (long)((Math.log((tfIdf*1.000/totalDocs)*df-1)+1)*Math.log(totalDocs/df));
		String aString="";
		aString="";
	}
	
	public long getTfIdf() {
		return tfIdf;
	}
	
	public long getWeight() {
		return weight;
	}

	public void addWeight(long tfIdf) {
		this.weight += tfIdf;
	}

	public TermStat(long docID) {
		this.docID = docID;
	}
	
	public long getDocID() {
		return docID;
	}
	
	public long getTF() {
		return termFreq;
	}
	
	public void setTF(long tf) {
		this.termFreq = tf;
	}
	
	// ++TF
	public void IncrementTF() {
		++termFreq;
	}
	
	public Set<Long> getPosSet() {
		return posSet;
	}
	
	public void setPosSet(Set<Long> posSet) {
		this.posSet = posSet;
	}
	
	public void add2PosSet(long pos) {
		posSet.add(pos);
	}
}

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
	private Set<Long> posSet = new HashSet<>(); // position set
	private double tfIdf = -1; //该文档和某个词项的tf-idf值
	private double weight = -1; // 点乘结果:weight = Σ{词项权重(1)*该文档权重(tf-idf)}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
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
	
	public void setTF(long termFreq) {
		this.termFreq = termFreq;
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

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

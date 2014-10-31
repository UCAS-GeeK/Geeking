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
	private long termFreq = -1; // term frequency
	private Set<Long> posSet = new HashSet<>(); // position set
	
	public TermStat(long docID) {
		this.docID = docID;
	}
	
	public long getDocID() {
		return docID;
	}
	
	public long getTermFreq() {
		return termFreq;
	}
	
	public void setTermFreq(long termFreq) {
		this.termFreq = termFreq;
	}
	
	public Set<Long> getPosSet() {
		return posSet;
	}
	
	public void setPosSet(Set<Long> posSet) {
		this.posSet = posSet;
	}
}

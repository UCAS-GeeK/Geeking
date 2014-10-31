package org.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 某词项的倒排索引
 * 即倒排索引表的一条记录
 *
 */
public class InvertedIndex {
	private final long termID;
	private long docFreq = -1; //document frequence
	private List<TermStat> stats = new ArrayList<>();// 该词项在各个文档中的统计信息
	
	public long getDocFreq() {
		return docFreq;
	}

	public void setDocFreq(long docFreq) {
		this.docFreq = docFreq;
	}

	public List<TermStat> getStats() {
		return stats;
	}

	public void setStats(List<TermStat> stats) {
		this.stats = stats;
	}

	public long getTermID() {
		return termID;
	}

	public InvertedIndex(long termID, long docID) {
		this.termID = termID;
		//stats.add(new TermStat(docID));
	}
	
	
	
	
	
}

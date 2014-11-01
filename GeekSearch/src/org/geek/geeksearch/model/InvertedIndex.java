package org.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.tribes.group.interceptors.TwoPhaseCommitInterceptor.MapEntry;
import org.geek.geeksearch.util.DBOperator;


/**
 * 某词项的倒排索引
 * 即倒排索引表的一条记录
 *
 */
public class InvertedIndex {
	private final long termID;
	private long docFreq = 0; //document frequence
	private Map<Long, TermStat> statsMap = new HashMap<>();// 该词项在各个文档中的统计信息
	
	public InvertedIndex(long termID) {
		this.termID = termID;
	}
	
	public long getDF() {
		return docFreq;
	}

	public void setDF(long docFreq) {
		this.docFreq = docFreq;
	}

	public Map<Long, TermStat> getStatsMap() {
		return statsMap;
	}

	public void setStatsMap(Map<Long, TermStat> statsMap) {
		this.statsMap = statsMap;
	}
	
	public long getTermID() {
		return termID;
	}
	
	public static void addAll2DB(Map<Long,InvertedIndex> termIDsMap, DBOperator dbOp) {
		Iterator<Entry<Long, InvertedIndex>> iter = termIDsMap.entrySet().iterator();
		long tID = -1, dF = -1, dID = -1, tF = -1;
		String positions = "", docIDs = "";
		while (iter.hasNext()) { // 遍历倒排索引
			Map.Entry<Long, InvertedIndex> entry = iter.next();
			tID = entry.getKey(); // 获取词项ID
			dF = entry.getValue().getDF(); // 获取dF
			
			/* 
			 * 一个 termID 对应的一条 documentIDs 在数据库中的存储格式:
			 * docFreq|docID1:TF:[pos1, pos2...]#docID2:TF:[pos1, pos2...]#...
			 */
			Iterator<Entry<Long, TermStat>> iter2 = entry.getValue().getStatsMap().entrySet().iterator();
			while (iter2.hasNext()) { // 遍历文档ID集
				Map.Entry<Long, TermStat> entry2 = iter2.next();
				dID = entry2.getKey(); // 获取文档ID
				tF = entry2.getValue().getTF(); // 获取tF
				positions = entry2.getValue().getPosSet().toString();
				docIDs += dID+":"+tF+":"+positions+"#";
			}
			String sql = " INSERT INTO InvertedIndex values("+tID+",'"
					+dF+"|"+docIDs+"') ";
			dbOp.executeUpdate(sql);
			docIDs = "";
		}
		
	}
	
	
}

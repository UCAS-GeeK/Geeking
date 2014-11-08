package org.geek.geeksearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.geek.geeksearch.util.DBOperator;


/**
 * 某词项的倒排索引
 * 即倒排索引表的一条记录
 *
 */
public class InvertedIndex {
	private final long termID;
	private long docFreq = 0; //document frequency
	private Map<Long, TermStat> statsMap = new TreeMap<Long, TermStat>();//该词项在各个文档中的统计信息
	private List<Map.Entry<Long, TermStat>> sortedStatsMap = null;//降序排列的statsMap
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
			dF = entry.getValue().getStatsMap().size(); // 获取dF
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
					+dF+"|"+docIDs+"') ";// testInvertedIndex for test
			dbOp.executeUpdate(sql);
			docIDs = "";
		}
	}
	
	/* 解析从数据库中读取的一条索引 */
	public void parseIndex(String docIDs) {
		//docIDs = docFreq|docID1:TF:[pos1, pos2...]#docID2:TF:[pos1, pos2...]#...
		int idx = docIDs.indexOf("|");
		if (idx < 0) {
			System.err.println("no documentIDs of termID: "+termID);
			return;
		}
		docFreq = Long.parseLong(docIDs.substring(0, idx));
		String[] docs = docIDs.substring(idx+1).split("#");
		if (docs == null || docs.length == 0) {
			System.err.println("no docIDs of termID: "+termID);
			return;
		}
		TermStat tStat;
		for (String doc : Arrays.asList(docs)) {
			//doc = docID1:TF:[pos1, pos2...]
			String[] stat = doc.split(":");
			if (stat.length != 3) {
				continue;
			}
			tStat = new TermStat(Long.parseLong(stat[0]));
			tStat.setTF(Long.parseLong(stat[1]));
			//[pos1, pos2...]
			List<String> posList = Arrays.asList(stat[2].substring(1, stat[2].length()-1).split(","));
			if (posList == null || posList.isEmpty()) {
				System.err.println("no position of termID: "+termID+"->Doc: "+tStat.getDocID());
				continue;
			}
			for (String pos : posList) {
				if (pos.isEmpty()) {
					continue;
				}
				tStat.add2PosSet(Long.parseLong(pos.trim()));
			}
			statsMap.put(tStat.getDocID(), tStat);
		}
		// 根据tf值对statsMap降序排序，用于TopK处理
		sortedStatsMap = new ArrayList<Map.Entry<Long,TermStat>>(statsMap.entrySet());
		Collections.sort(sortedStatsMap, new Comparator<Map.Entry<Long, TermStat>>() {
			public int compare(Entry<Long, TermStat> o1, Entry<Long, TermStat> o2) {
				return o2.getValue().getTF() > o1.getValue().getTF() ?
						1 : -1;
			}
		});
		/* just for order verify */
//		System.out.println("\ntermID="+termID);
//		for (Map.Entry<Long, TermStat> map : sortedStatsMap) {
//			System.out.print(map.getValue().getTF()+", ");
//		}
		
	}
	
}

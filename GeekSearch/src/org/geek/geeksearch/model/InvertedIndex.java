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
	private Map<Long, TermStat> topKStatsMap = new TreeMap<>();//降序排列的statsMap
	
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
	
	public static void addAll2DB(Map<Long,InvertedIndex> invIdxMap, DBOperator dbOp, long totalDocCnt) {
		Iterator<Entry<Long, InvertedIndex>> iter = invIdxMap.entrySet().iterator();
		long tID = -1, dF = -1, dID = -1, idf = 0; //词项的idf
		String positions = "", docIDs = "";
		while (iter.hasNext()) { // 遍历倒排索引
			Map.Entry<Long, InvertedIndex> entry = iter.next();
			tID = entry.getKey(); // 获取词项ID
			dF = entry.getValue().getStatsMap().size(); // 获取dF
			if (dF == 0) {
				/*do nothing*/
			} else {
				//计算 idf，鉴于数据集较小，使用2为底
				/**********测试阶段使用不带log的tf-idf**********/
				idf = (long)(totalDocCnt/dF);
//				idf = (long)Math.log(totalDocCnt/dF);
			}
			
			/* 
			 * 一个 termID 对应的一条 documentIDs 在数据库中的存储格式:
			 * docFreq|docID1:tfIdf:[pos1, pos2...]#docID2:TF:[pos1, pos2...]#...
			 */
			Iterator<Entry<Long, TermStat>> iter2 = entry.getValue().getStatsMap().entrySet().iterator();
			while (iter2.hasNext()) { // 遍历文档ID集
				Map.Entry<Long, TermStat> entry2 = iter2.next();
				dID = entry2.getKey(); // 获取文档ID
				//计算tf-idf
				long tfIdf = entry2.getValue().calcTfIdf(idf);
				positions = entry2.getValue().getPosSet().toString();
				docIDs += dID+":"+tfIdf+":"+positions+"#";
			}
			String sql = " INSERT INTO InvertedIndex values("+tID+",'"	
					+dF+"|"+docIDs+"') ";// testInvertedIndex for test
			dbOp.executeUpdate(sql);
			docIDs = "";
		}
	}
	
	/* 解析从数据库中读取的一条索引 */
	public void parseIndex(String docIDs, int topK) {
		//docIDs = docFreq|docID1:tfidf:[pos1, pos2...]#docID2:tfidf:[pos1, pos2...]#...
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
			//doc = docID1:tfIdf:[pos1, pos2...]
			String[] stat = doc.split(":");
			if (stat.length != 3) {
				continue;
			}
			tStat = new TermStat(Long.parseLong(stat[0]));
			/**********测试阶段使用不带log的tf-idf*********/
			tStat.setTfIdf(Long.parseLong(stat[1]));
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
		
		// 根据tf-idf值对statsMap降序排序，用于TopK处理
		List<Map.Entry<Long, TermStat>> sortedStatsMap = 
				new ArrayList<Map.Entry<Long,TermStat>>(statsMap.entrySet());
		Collections.sort(sortedStatsMap, new Comparator<Map.Entry<Long, TermStat>>() {
			public int compare(Entry<Long, TermStat> o1, Entry<Long, TermStat> o2) {
				return o2.getValue().getTfIdf() > o1.getValue().getTfIdf() ?
						1 : -1;
			}
		});
		
		//截取topK,如果不够topK,不动
		if (sortedStatsMap.size() > topK) {
			sortedStatsMap = sortedStatsMap.subList(0, topK);
		}
//		System.out.println("\ntermID="+termID); //
		for (Map.Entry<Long, TermStat> entry : sortedStatsMap) {
			topKStatsMap.put(entry.getKey(), entry.getValue());
//			System.out.println(entry.getKey()); //
		}
		
	}
	
	public Map<Long, TermStat> getTopKDocs() {
		return topKStatsMap;
	}
	
}

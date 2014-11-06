package org.geek.geeksearch.indexer;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.geek.geeksearch.model.DocIndex;
import org.geek.geeksearch.model.InvertedIndex;
import org.geek.geeksearch.model.PageInfo;
import org.geek.geeksearch.model.TermStat;
import org.geek.geeksearch.util.DBOperator;
import org.geek.geeksearch.util.HtmlParser;

/**
 * 0. 读取全部html网页：
 * 1. 抽取关键信息， 生成PageInfo存入PageIndex数据库
 * 2. 过滤html标签获取正文，进行正文词条化
 * 3. 生成文档索引DocIndex，和词项ID-词项映射表TermsIndex，将两者写入数据库
 * 4. 根据文档索引生成倒排索引InvertedIndex，写入数据库
 *
 */
public class IndexGenerator {
	private AtomicLong docID = new AtomicLong(-1); //文档ID long 可能不够，考虑用BigNumber
	private AtomicLong termID = new AtomicLong(-1); //词项ID
	HashMap<String, Long> termIDsMap = new HashMap<>(); //词项-词项ID 映射表
	Map<Long,InvertedIndex> invIdxMap = new HashMap<>(); //倒排索引表 
	
	private Tokenizer tokenizer = new Tokenizer();
	private final DBOperator dbOperator;
	private final String rawPagesDir; //configure.properties
	
	public IndexGenerator(String rawPagesDir) {
		this.rawPagesDir = rawPagesDir;
		this.dbOperator = new DBOperator();//debug
		dbOperator.cleanAllTables();//重建索引，清空所有table		
	}
	
	public static void main(String[] args) {
		IndexGenerator generator = new IndexGenerator("E:\\eclipseWorkspace\\Geeking\\RawPages4Test");
		generator.createIndexes();
	}
	
	public void createIndexes() {
		String[] typeArr = getTypes();
		for (String type : typeArr) {
			String[] htmlArr = getHTMLs(type);
			for (String html : htmlArr) {
				createIndexes(type, html);
			}
		}
		//建立 词项ID-词项 索引表
		createTermIdIndex();		
		// 建立倒排索引
		createInvertedIndex();		
	}

	/* 生成各种索引 */
	public void createIndexes(String type, String html) {
		String path = rawPagesDir+"\\"+type+"\\"+html;
		if (!HtmlParser.checkPath(path)) {
			return;
		}
		String htmlStr = HtmlParser.readHtmlFile(path);
		if (htmlStr == null || htmlStr.isEmpty()) {
			return;
		}
		//建立网页信息索引
		createPageIndex(htmlStr, type, getURL(html));		
		//过滤标签获取正文
		String plainText = HtmlParser.getPlainText(htmlStr, type);
//		System.out.println("docID = "+docID);
//		System.out.println(plainText);
		if (plainText == null || plainText.isEmpty()) {
			return;
		}
		// 使用第三方分词工具ansj实现分词
		List<String> parsedTerms = tokenizer.doTextTokenise(plainText);
		if (parsedTerms == null || parsedTerms.isEmpty()) {
			return;
		}
		//建立文档索引
		createDocIndex(parsedTerms);
	}
	
	public void createInvertedIndex() {
		//因为建倒排索引需要较大内存，因此先释放不再需要的数据结构
		termIDsMap.clear();
		//读取数据库docIndex整张表（默认内存够用）
		String sql = " SELECT * FROM DocsIndex "; // 要执行的SQL语句
		ResultSet res = dbOperator.executeQuery(sql);
		//遍历每条记录，遍历记录的每个词项ID
		try {
			while (res.next()) {
				long dID = res.getLong(1);
				String terms = res.getString(2);
				List<String> docTermIDs = DocIndex.toList(terms);
				if (docTermIDs == null) {
					continue;
				}				
				int pos = 0;
				InvertedIndex invIdx;
				TermStat stat;
				for (String termIDStr : docTermIDs) {
					if (termIDStr == null || termIDStr.isEmpty()) {
						continue;
					}
					long tID = Long.valueOf(termIDStr);
					if (!invIdxMap.containsKey(tID)) {
						invIdx = new InvertedIndex(tID);
						stat = new TermStat(dID);
						invIdx.getStatsMap().put(dID, stat);
						invIdxMap.put(tID, invIdx);
					} else {
						stat = invIdxMap.get(tID).getStatsMap().containsKey(dID) ? 
								invIdxMap.get(tID).getStatsMap().get(dID) : new TermStat(dID);
						invIdxMap.get(tID).getStatsMap().put(dID, stat);
					} 
					stat.IncrementTF(); //++TF
					stat.add2PosSet(pos++); //add pos
//					String string =  invIdxMap.get(tID).getStatsMap().get(dID).getPosSet().toString();
//					string = "tID="+tID+"->DocID="+dID+":TF="+invIdxMap.get(tID).getStatsMap().get(dID).getTF()
//							+";POS="+string;
//					System.out.println(string);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		InvertedIndex.addAll2DB(invIdxMap, dbOperator);
	}
	
	/* 建立 词项ID-词项 索引表 */
	public void createTermIdIndex() {
		//将termIDsMap写入数据库
		Iterator<Entry<String, Long>> iter = termIDsMap.entrySet().iterator(); 
		while (iter.hasNext()) {
			Map.Entry<String, Long> entry = iter.next();
			String terms = entry.getKey();
			long tID = entry.getValue();
			String sql = " INSERT INTO TermsIndex values("+tID+",'"+terms+"') "; 
			dbOperator.executeUpdate(sql);
		}
	}
	
	/* 建立文档索引 */
	public void createDocIndex(List<String> parsedTerms) {
		// 将词项转化成ID	
		List<Long> docTermIDs = transTerm2ID(parsedTerms);
//		String tran = parsedTerms.toString()+"\n"+docTermIDs.toString();
//		System.out.println(tran);
		DocIndex docIndex = new DocIndex();
		docIndex.addIndex(docID.get(), docTermIDs, dbOperator);
	}
	
	public List<Long> transTerm2ID(List<String> parsedTerms) {
		List<Long> docTermIDs = new ArrayList<>();
		for (String term : parsedTerms) {
			if (term == null || term.isEmpty()) {
				continue;
			}
			if (termIDsMap.containsKey(term)) {
				docTermIDs.add(termIDsMap.get(term)); //获取词项ID加入docTermsList
			} else {
				termIDsMap.put(term, termID.incrementAndGet());
				docTermIDs.add(termID.get());
			}		
		}
		return docTermIDs;
	}
	
	/* 建立网页信息索引 */
	public void createPageIndex(String htmlStr, String type, String url) {
		String title = HtmlParser.getTitle(htmlStr);
		String pubTime = HtmlParser.getPubTime(htmlStr, type);
		String[] kwAndDesc = HtmlParser.getKeyWordAndDesc(htmlStr);
		if (url.isEmpty() || type.isEmpty() || kwAndDesc.length != 2) {
			String err = "type="+type+";url="+url+";kwAndDesc="+kwAndDesc.toString();
			System.err.printf("bad page info: %s\n", err);
			return;
		}
		
		PageInfo pageInfo = new PageInfo(docID.incrementAndGet(), url, type, 
				title, pubTime, kwAndDesc[0], kwAndDesc[1]);	
		pageInfo.add2DB(dbOperator);
//		System.out.print("title="+title+";  kw="+kwAndDesc[0]+";  pubTime="
//				+pubTime+"\ndescrip="+kwAndDesc[1]+"\n");
	}
	
	public String getURL(String fileName) {
		int idx = fileName.lastIndexOf(".");
		if (idx <= 0) {
			System.err.printf("wrong type of file name!", fileName);
			return "";
		}
		return fileName.substring(0, idx); 
	}
	
	/* 从网页库目录获取类型目录 的列表*/
	public String[] getTypes() {
		File rootDir = new File(rawPagesDir);
		if (!rootDir.exists() || !rootDir.isDirectory()) {   
			System.err.printf("unexisting path of rawPages: %s\n", rawPagesDir);   
		    return null;
		}
		return rootDir.list();
	}
	
	/* 从 类型目录获取html文件列表*/
	public String[] getHTMLs(String type) {
		File typeDir = new File(rawPagesDir+"\\"+type);
		if (!typeDir.exists() || !typeDir.isDirectory()) {   
			System.err.printf("unexisting path of type: %s\n", typeDir.toString());   
		    return null;
		}
		return typeDir.list();
	}

}
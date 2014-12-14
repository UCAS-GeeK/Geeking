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

import org.geek.geeksearch.configure.Configuration;
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
	private final String rawPagesDir;
	private AtomicLong docID = new AtomicLong(-1); //文档ID long 可能不够，考虑用BigNumber
	private AtomicLong termID = new AtomicLong(-1); //词项ID
	private HashMap<String, Long> termIDsMap = new HashMap<>(); //词项-词项ID 映射表
	private Map<Long,InvertedIndex> invIdxMap = new HashMap<>(); //倒排索引表 
	
	private final Configuration config;
	private final DBOperator dbOperator;
	
	public IndexGenerator() {
		this.config = new Configuration("configure.properties");
		this.rawPagesDir = config.getValue("RawPagesPath");
		this.dbOperator = new DBOperator(config);
		//new Tokenizer(config);//release mem
		dbOperator.cleanAllTables();//!!!重建索引，清空所有table!!!		
	}
	
	/* 构建索引入口 */
	public void createIndexes() {
//		String[] typeArr = getTypes();
//		long start = System.currentTimeMillis();
//		for (String type : typeArr) {
//			String[] htmlArr = getHTMLs(type);
//			for (String html : htmlArr) {
//				createIndexes(type, html);
//			}
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("=== 网页预处理(文本抽取过滤+正向索引表+网页信息表)建立完成，用时: "+(end-start)/1000+"秒 ===");
//		//建立 词项ID-词项 索引表 
//		createTermIdIndex();
//		start = System.currentTimeMillis();
//		
//		System.out.println("=== 词项索引表建立完成，用时："+(start-end)/1000+"秒 ===");
		long start = System.currentTimeMillis();
		// 建立倒排索引
		createInvertedIndex();
		System.out.println("=== 倒排索引表建立完成，用时："+(System.currentTimeMillis()-start)/1000+"秒 ===");
	}

	/* 生成各种索引 */
	public void createIndexes(String type, String html) {		
//		long start = System.currentTimeMillis();
		String path = rawPagesDir+type+"/"+html; //under linux
//		System.out.println("------------ 正在处理网页："+ path +" ------------");
		if (!HtmlParser.checkPath(path)) {
			return;
		}
		String htmlStr = HtmlParser.readHtmlFile(path);
		if (htmlStr == null || htmlStr.isEmpty()) {
			return;
		}
//		long end = System.currentTimeMillis();
//		System.out.println("==== 读取网页，用时："+ (end-start) +"毫秒 ====");
		
		//建立网页信息索引
		String titleDesc = createPageIndex(htmlStr, type, html);
		if (titleDesc == null || titleDesc.isEmpty()) {
			return;
		}
		
//		start = System.currentTimeMillis();
//		System.out.println("==== 建立网页信息索引，用时："+ (start-end) +"毫秒 ====");
		
		//过滤标签获取正文
		String plainText = HtmlParser.getPlainText(htmlStr, type);
		if (plainText == null || plainText.isEmpty()) {
			return;
		}
		
//		end = System.currentTimeMillis();
//		System.out.println("==== 获取正文，用时："+ (end - start) +"毫秒 ====");
		
		// 使用第三方分词工具ansj实现分词
		List<String> parsedTerms = Tokenizer.doTokenise(titleDesc+plainText);
		if (parsedTerms == null || parsedTerms.isEmpty()) {
			return;
		}
		
//		start = System.currentTimeMillis();
//		System.out.println("==== 分词，用时："+ (start-end) +"毫秒 ====");
		
		//建立文档索引
		createDocIndex(parsedTerms);
		
//		end = System.currentTimeMillis();
//		System.out.println("==== 将正文转化为IDs，用时："+ (end - start) +"毫秒 ====");
	}
	
	public void createInvertedIndex() {
		//因为建倒排索引需要较大内存，因此先释放不再需要的数据结构
		termIDsMap.clear();
		
		//读取数据库docIndex整张表（默认内存够用）
		
		// 要执行的SQL语句,若运行中断于docID=n，可加入where docID > n
		String sql = " SELECT * FROM docsindex "; 
		ResultSet res = dbOperator.executeQuery(sql);
		long totalDocCnt = 0;//对总文档数计数
		
		sql=null;//release mem
		
		//遍历每条记录
		try {
			while (res.next()) {
				long dID = res.getLong(1);
				String terms = res.getString(2);
				List<String> docTermIDs = DocIndex.toList(terms);
				terms=null;//release mem
				if (docTermIDs == null) {
					continue;
				}
				int pos = 0;
				InvertedIndex invIdx;
				TermStat stat;
				String termIDStr;
				long tID;
				//遍历记录的每个词项ID
				for (int i=0; i<docTermIDs.size();i++) {
					termIDStr = docTermIDs.get(i);
					if (termIDStr == null || termIDStr.isEmpty()) {
						continue;
					}
					
					try {
						tID = Long.valueOf(termIDStr.trim());
						termIDStr=null;//release mem
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					
					if (!invIdxMap.containsKey(tID)) {
						invIdx = new InvertedIndex(tID);
						stat = new TermStat(dID);
						invIdx.getStatsMap().put(dID, stat);
						invIdxMap.put(tID, invIdx);
						invIdx=null;//release mem
					} else {
						stat = invIdxMap.get(tID).getStatsMap().containsKey(dID) ? 
								invIdxMap.get(tID).getStatsMap().get(dID) : new TermStat(dID);
						invIdxMap.get(tID).getStatsMap().put(dID, stat);
					}
					stat.IncrementTF(); //++TF
					stat=null;//release mem
					//stat.add2PosSet(pos++); //add pos
//					String string =  invIdxMap.get(tID).getStatsMap().get(dID).getPosSet().toString();
//					string = "tID="+tID+"->DocID="+dID+":TF="+invIdxMap.get(tID).getStatsMap().get(dID).getTF()
//							+";POS="+string;
//					System.out.println(string);
				}
				++totalDocCnt;
				if (dID % 200 == 0) {
					System.out.println("----正在处理文档索引："+dID);
				}
				if (dID > 100000) {
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("total doc cnt:"+ totalDocCnt);
		}
		res = null;//release mem
		System.out.println("--------完成正向索引遍历，开始写入数据库--------");
		InvertedIndex.addAll2DB(invIdxMap, dbOperator, totalDocCnt);
	}
	
	/* 建立 词项ID-词项 索引表 */
	public void createTermIdIndex() {
		//将termIDsMap写入数据库
		Iterator<Entry<String, Long>> iter = termIDsMap.entrySet().iterator(); 
		while (iter.hasNext()) {
			Map.Entry<String, Long> entry = iter.next();
			String terms = entry.getKey();
			long tID = entry.getValue();
			String sql = " INSERT INTO termsindex values("+tID+",'"+terms+"') "; 
			dbOperator.executeUpdate(sql);
		}
	}
	
	/* 建立文档索引 */
	public void createDocIndex(List<String> parsedTerms) {
		// 将词项转化成ID	
		List<Long> docTermIDs = transTerm2ID(parsedTerms);
//		String tran = parsedTerms.toString()+"\n"+docTermIDs.toString();
		if (docID.get()%200 == 0) {
			System.out.println("----正在建立正向索引，docID："+docID.get());
		}
		DocIndex.addIndex(docID.get(), docTermIDs, dbOperator);
	}
	
	public List<Long> transTerm2ID(List<String> parsedTerms) {
		List<Long> docTermIDs = new ArrayList<>();
		String term;
		for (int i=0;i<parsedTerms.size();i++) {
			term = parsedTerms.get(i);
			if (term == null) {
				continue;
			}
			term = term.trim();
			if (term.isEmpty()) {
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
	public String createPageIndex(String htmlStr, String type, String url) {
		String title = HtmlParser.getTitle(htmlStr);
		if (title == null || title.isEmpty())
			return "";
		
		String pubTime = HtmlParser.getPubtimeReg(htmlStr);
		String[] kwAndDesc = HtmlParser.getKeyWordAndDesc(htmlStr);
		if (kwAndDesc == null || kwAndDesc.length != 2) {
//			String err = "type="+type+";url="+url+";kwAndDesc="+kwAndDesc.toString();
//			System.err.println("bad page info");
			return "";
		}
		
		PageInfo pageInfo = new PageInfo(docID.incrementAndGet(), url, type, 
				title, pubTime, kwAndDesc[0], kwAndDesc[1]);	
		pageInfo.add2DB(dbOperator);
//		System.out.print("title="+title+";  kw="+kwAndDesc[0]+";  pubTime="
//				+pubTime+"\ndescrip="+kwAndDesc[1]+"\n");
		return title+kwAndDesc[1];//返回 标题+描述
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
		File typeDir = new File(rawPagesDir+type);
		if (!typeDir.exists() || !typeDir.isDirectory()) {   
			System.err.printf("unexisting path of type: %s\n", typeDir.toString());   
		    return null;
		}
		return typeDir.list();
	}

	public static void main(String[] args) {
		IndexGenerator generator = new IndexGenerator();
		generator.createIndexes();
	}

}
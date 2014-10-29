package com.geek.geeksearch.indexer;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import com.geek.geeksearch.model.PageInfo;
import com.geek.geeksearch.util.DBOperator;
import com.geek.geeksearch.util.HtmlParser;

/**
 * 0. 读取全部html网页：
 * 1. 抽取关键信息， 生成PageInfo存入PageIndex数据库
 * 2. 过滤html标签获取正文，进行正文词条化
 * 3. 生成文档索引DocIndex，和词项ID-词项映射表TermsMap，将两者写入数据库
 * 4. 根据文档索引生成倒排索引InvertedIndex，写入数据库
 *
 */
public class IndexGenerator {
	private AtomicLong docID = new AtomicLong(-1); // 文档ID
	private AtomicLong termID = new AtomicLong(-1); // 词项ID
	private DBOperator dbOperator = new DBOperator();
	private String rawPagesDir = null; //configure.properties
	
	public IndexGenerator(String rawPagesDir) {
		this.rawPagesDir = rawPagesDir;
	}
	
	public static void main(String[] args) {
		IndexGenerator generator = new IndexGenerator("RawPages");
		generator.createIndexes();
	}
	
	public void createIndexes() {
		String[] typeArr = getTypes();
		for (String type : typeArr) {
			String[] htmlArr = getHTMLs(type);
			for (String html : htmlArr) {
//				System.out.println(type+"/"+html);
				createIndexes(type, html);
			}
		}
	}
	
	/* 生成各种索引 */
	public void createIndexes(String type, String html) {
		String path = rawPagesDir+"\\"+type+"\\"+html;
		//if (checkHtml()) return;
		String htmlStr = HtmlParser.readHtmlFile(path);

		//建立网页信息索引
		createPageIndex(htmlStr, type, getURL(html));
		
		//过滤标签获取正文
		String plainText = HtmlParser.getPlainText(htmlStr, type);
		
		//建立文档索引
		createDocIndex(plainText);
	}
	
	/* 建立文档索引 */
	public void createDocIndex(String text) {
		//分词，得到一篇文档的词项列表，写入数据库
	}
	
	/* 建立网页信息索引 */
	public void createPageIndex(String htmlStr, String type, String url) {
		String title = HtmlParser.getTitle(htmlStr);
		String[] kwAndDesc = HtmlParser.getKeyWordAndDesc(htmlStr);
		if (url.isEmpty() || type.isEmpty() || kwAndDesc.length != 2) {
			String err = "type="+type+";url="+url+";kwAndDesc="+kwAndDesc.toString();
			System.err.printf("bad page info: %s\n", err);
			return;
		}
		PageInfo pageInfo = new PageInfo(docID.incrementAndGet(), url, type, title, kwAndDesc[0], kwAndDesc[1]);	
		pageInfo.add2DB(dbOperator);
		System.out.print("title: "+title+";  kw: "+kwAndDesc[0]
				+"\ndescrip: "+kwAndDesc[1]+"\n");
	}
	
	public String getURL(String fileName) {
		int idx = fileName.indexOf(".");
		if (idx <= 0) {
			System.err.printf("wrong type of file name!", fileName);
			return "";
		}
		return fileName.substring(0, idx); 
	}
	
	/* 从网页库目录获取类型目录 列表*/
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

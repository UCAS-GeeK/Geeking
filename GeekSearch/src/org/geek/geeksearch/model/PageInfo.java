package org.geek.geeksearch.model;


import org.geek.geeksearch.util.DBOperator;

/**
 * 网页信息记录
 *
 */
public class PageInfo {
	private final long docID;
	private final String url;
	private String title = "";
	private String description = "";
	private String pubTime = ""; //
	private String keyWords = "";
	private String type = ""; // 考虑枚举常量enum
	
	public PageInfo(long docID, String url, String type, String title, String pubTime, String keywords, String descrip) {
		this.docID = docID;
		this.url = url;
		this.type = type;
		this.title = title;
		this.pubTime = pubTime;
		this.keyWords = keywords;
		this.description = descrip;
	}
	
	public void add2DB(DBOperator dbOp) {
		String sql = " INSERT INTO PagesIndex values("+docID+",'"+url+"','"+title
				+"','"+description+"','"+pubTime+"','"+type+"','"+keyWords+"') ";
		dbOp.executeUpdate(sql);
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPubTime() {
		return pubTime;
	}
	
	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}
	
	public String[] getKeyWords() {
		return keyWords.split(",");
	}
	
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

}

package org.geek.geeksearch.model;

import java.util.ArrayList;

import org.geek.geeksearch.util.DBOperator;

/**
 * 网页信息记录
 *
 */
public class PageInfo {
	private String url = "";
	private String title = "";
	private String description = "";
	private String date = ""; //
	private String keyWords = "";
	private String type = ""; // 考虑枚举常量enum
	
	public PageInfo(long docID, String url, String type, String title, String keywords, String descrip) {
		this.url = url;
		this.type = type;
		this.title = title;
		this.keyWords = keywords;
		this.description = descrip;
	}
	
	public void add2DB(DBOperator dbOp) {
		//
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
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

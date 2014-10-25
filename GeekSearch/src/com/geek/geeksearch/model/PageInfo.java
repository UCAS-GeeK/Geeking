package com.geek.geeksearch.model;

import java.util.ArrayList;

/**
 * 网页信息记录
 *
 */
public class PageInfo {
	private String url = null;
	private String tittle = null;
	private String digest = null;
	private String date = null; //
	private ArrayList<String> keyWords = new ArrayList<String>();
	private String category = null; //枚举常量
	private String offset = null;
	
	public PageInfo(String url, String category) {
		this.url = url;
		this.category = category;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTittle() {
		return tittle;
	}
	
	public void setTittle(String tittle) {
		this.tittle = tittle;
	}
	
	public String getSubstract() {
		return digest;
	}
	
	public void setSubstract(String digest) {
		this.digest = digest;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public ArrayList<String> getKeyWords() {
		return keyWords;
	}
	
	public void setKeyWords(ArrayList<String> keyWords) {
		this.keyWords = keyWords;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getOffset() {
		return offset;
	}
	
	public void setOffset(String offset) {
		this.offset = offset;
	}

}

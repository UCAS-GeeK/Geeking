package org.geek.geeksearch.model;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geek.geeksearch.util.DBOperator;

/**
 * 网页信息记录
 *
 */
public class PageInfo implements Cloneable{
	private final long docID;
	private String url;
	private String title = "";
	private String description = "";
	private String pubTime = ""; //网页发布时间
	private String keyWords = "";
	private String type = ""; // 考虑枚举常量enum
	private Map<String, List<Integer>> titleHlightPos = new HashMap<String, List<Integer>>();// 标题高亮位置
	private Map<String, List<Integer>> descHlightPos = new HashMap<String, List<Integer>>();// 摘要高亮位置
	
	public PageInfo(long docID, String url, String type, String title, String pubTime, String keywords, String descrip) {
		this.docID = docID;
		this.url = url;
		this.type = type;
		this.title = title;
		this.pubTime = pubTime;
		this.keyWords = keywords;
		this.description = descrip;
	}
	
	/* for query process */
	public PageInfo(long docID) {
		this.docID = docID;
	}
	
	public boolean loadInfo(DBOperator dbOperator) {
		String sql = " SELECT * FROM PAGESINDEX WHERE DocID='"+docID+"' ";
		ResultSet rSet = dbOperator.executeQuery(sql);
		if (rSet == null) {
			return false;
		}
		try {
			while (rSet.next()) {
				url = rSet.getString("Url");
				title = rSet.getString("Title");
				description = rSet.getString("Description");
				title = rSet.getString("Title");
				pubTime = rSet.getString("Date");
				type = rSet.getString("Type");
				keyWords = rSet.getString("keywords");				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void add2DB(DBOperator dbOp) {
		String sql = " INSERT INTO PagesIndex values("+docID+",'"+url+"','"+title
				+"','"+description+"','"+pubTime+"','"+type+"','"+keyWords+"') ";
		dbOp.executeUpdate(sql);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Object object = super.clone();
		return object;
	}
	
	/* 计算 title/description 高亮位置 */
	public void highlight(List<String> queryTerms) {
		//highlight title
		int pos = -1;
		for (String term : queryTerms) {
			String tmp = title;
			List<Integer> posList = new ArrayList<>();
			int realPos = 0;
			while(true) {
				pos = tmp.indexOf(term);
				if (pos < 0) {
					titleHlightPos.put(term, posList);
					break;
				}
				realPos = pos+realPos;
				posList.add(realPos);
				tmp = tmp.substring(pos+term.length());
			}
		}
		//highlight description
		for (String term : queryTerms) {
			String tmp = description;
			List<Integer> posList = new ArrayList<>();
			
			while(true) {//
				pos = tmp.indexOf(term);
				if (pos < 0) {
					descHlightPos.put(term, posList);
					break;
				}
				posList.add(pos);
				tmp = tmp.substring(pos+term.length());
			}
		}
	}
	
	public String getUrl() {
		return parseUrl();
	}
	
	private String parseUrl(){
		return url.replace("$", ":").replace("#", "/");
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
	
	
	public Map<String, List<Integer>> getTitleHlightPos() {
		return titleHlightPos;
	}

	public void setTitleHlightPos(Map<String, List<Integer>> titleHlightPos) {
		this.titleHlightPos = titleHlightPos;
	}

	public Map<String, List<Integer>> getDescHlightPos() {
		return descHlightPos;
	}

	public void setDescHlightPos(Map<String, List<Integer>> descHlightPos) {
		this.descHlightPos = descHlightPos;
	}

	//just for test
	public static void main(String[] args) {
		List<String> queryTerms = new ArrayList<String>();
		queryTerms.add("科比");
		queryTerms.add("2");
		PageInfo page = new PageInfo(9);
		page.setTitle("科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA"
				+ "科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA"
				+ "科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA");
		page.setDescription("科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA"
				+ "科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA"
				+ "科比32000分!救命2+1太美 这28分比44分更强_篮球-NBA");
		page.highlight(queryTerms);
		//verify
		for (Map.Entry<String, List<Integer>> entry : page.getTitleHlightPos().entrySet()) {
			for (Integer pos : entry.getValue()) {
				System.out.println(entry.getKey()+" : "+pos);
			}
		}
	}

}

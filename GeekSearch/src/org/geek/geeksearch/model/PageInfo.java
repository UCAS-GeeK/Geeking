package org.geek.geeksearch.model;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.util.DBOperator;

/**
 * 网页信息记录
 *
 */
public class PageInfo implements Cloneable{
	private final long docID;
	private String url;
	private String turl;
	private String title = "";
	private String description = "";
	private String pubTime = ""; //网页发布时间
	private String keyWords;
	private String type = ""; // 考虑枚举常量enum
	private String source = "";//将网页来源
	
	private int descriLength = 50; //限制description长度

	private Map<String, List<Integer>> titleHlightPos = new HashMap<String, List<Integer>>();// 标题高亮位置
	private Map<String, List<Integer>> descHlightPos = new HashMap<String, List<Integer>>();// 摘要高亮位置
	public String getTurl(){
		return url;
	}
	/* for IndexGenerator */
	public PageInfo(long docID, String url, String type, String title, String pubTime, String keywords, String descrip) {
		this.docID = docID;
		this.url = url;
		this.type = type;
		this.title = title;
		this.pubTime = pubTime;
		this.keyWords = Tokenizer.doTokenise(keywords).toString();
		this.description = descrip;
	}
	
	/* for query process */
	public PageInfo(long docID) {
		this.docID = docID;
	}
	
	public boolean loadInfo(DBOperator dbOperator) {
		String sql = " SELECT * FROM pagesindex WHERE DocID='"+docID+"' ";
		ResultSet rSet = dbOperator.executeQuery(sql);
		if (rSet == null) {
			return false;
		}
		try {
			while (rSet.next()) {
				url = rSet.getString("Url");
				title = rSet.getString("Title");
				String desc = rSet.getString("Description");
				description = (desc.length() < descriLength ? desc :
					desc.substring(0, 50)) +"...";
				title = rSet.getString("Title");
				type = rSet.getString("Type");
				keyWords = rSet.getString("keywords");
				source = typeToSource();
				pubTime = rSet.getString("Date");
				if (pubTime.equals("null") || pubTime.isEmpty()) {
					pubTime = "\\(╯-╰)/";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private String typeToSource() {
		if (type.equals("qq")) {
			return "腾讯体育";
		} else if (type.equals("163")){
			return "网易体育";
		} else if (type.equals("msn")){
			return "MSN体育";
		} else if (type.equals("sohu")){
			return "搜狐体育";
		} else {
			return "~自己看~";
		}
	}
	
	public void add2DB(DBOperator dbOp) {
		String sql = " INSERT INTO pagesindex values("+docID+",'"+url+"','"+title
				+"','"+description+"','"+pubTime+"','"+type+"','"+keyWords+"') ";
		dbOp.executeUpdate(sql);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Object object = super.clone();
		return object;
	}
	
	/* 计算title和description中搜索词出现的此处，返回权重。1次+10*/
	public long countInTitleDesc(String term) {
		String text = title + description;
		long weight = 0;
		int start = 0;
		
		int idx = text.indexOf(term, start);
		while (idx >= 0) {
			weight += 10;
			start = idx + term.length();
			idx = start < text.length() ? text.indexOf(term, start) : -1;
		}
		
		return weight;
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
		return parseKeyWords(keyWords);
	}
	
	public static String[] parseKeyWords(String keywords) {
		if (keywords == null || keywords.isEmpty()) {
			return null;
		}
		return keywords.replaceAll("[\\[\\]]", "").split(",");
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
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

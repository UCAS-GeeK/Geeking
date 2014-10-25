package com.geek.geeksearch.model;

import java.util.HashMap;

/**
 * 网页信息表 - 维护多条网页信息记录
 *
 */
public class PagesTable {
	private HashMap<Integer, PageInfo> pagesTable =  new HashMap<>();
	
	public void store2DB() {
		
	}
	
	/* load all pages info from DB to memory */
	public void load2Memory() {
		
	}
	
	public void getFromDB(Integer docID) {
		
	}
	
	public PageInfo get(Integer docID) {
		//check null error
		return pagesTable.get(docID);
	}
	
	public void put(Integer docID, PageInfo page) {
		//check null error
		pagesTable.put(docID, page);
	}

}

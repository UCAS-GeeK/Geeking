package org.geek.geeksearch.util;


public class testDBOperator {

	public static void main(String[] args){

		DBOperator db = new DBOperator() ;
		String sql = "INSERT INTO TermsIndex values(11,'极客')";
		db.executeUpdate(sql);
	
	}
}


	

	
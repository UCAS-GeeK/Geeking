package org.geek.geeksearch.util;


public class testDBOperator {

	public static void main(String[] args){
		//int a = 1;
		DBOperator db = new DBOperator() ;
		String sql = "INSERT INTO TermsIndex values(8000,'极客')";
		db.executeUpdate(sql);
		//a = 2;
	}
}


	

	
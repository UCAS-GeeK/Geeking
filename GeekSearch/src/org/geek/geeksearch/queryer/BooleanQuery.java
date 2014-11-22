package org.geek.geeksearch.queryer;
import java.io.IOException;
import java.util.*;


public class BooleanQuery {
	String start2 = "";
	String end2 = "";
	String start3 = "";
	String end3 = "";
	ArrayList<String> gram_2 = new ArrayList<String>();
	ArrayList<String> gram_3 = new ArrayList<String>();
	public void for_print(){
		System.out.println("start2:"+start2);
		System.out.println("start3:"+start3);
		System.out.println("end2:"+end2);
		System.out.println("end3:"+end2);
		for(int i = 0; i < gram_2.size(); i++)
			System.out.println("gram_2:"+gram_2.get(i));
		for(int i = 0; i < gram_3.size(); i++)
			System.out.println("gram_3:"+gram_3.get(i));	
	}
  
}

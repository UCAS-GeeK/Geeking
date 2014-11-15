 
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%> 
<% 
System.out.println("进入");
String []words = {"amani","abc","apple","abstract","an","bike","byebye", 
"beat","be","bing","come","cup","class","calendar","china"}; 
if(request.getParameter("search-text") != null) { 
String key = request.getParameter("search-text"); 
if(key.length() != 0){ 
String json="["; 
for(int i = 0; i < words.length; i++) { 
if(words[i].startsWith(key)){ 
json += "\""+ words[i] + "\"" + ","; 
} 
} 
json = json.substring(0,json.length()-1>0?json.length()-1:1); 
	json += "]"; 
System.out.println("json:" + json); 
out.println(json); 
} 
} 
%> 

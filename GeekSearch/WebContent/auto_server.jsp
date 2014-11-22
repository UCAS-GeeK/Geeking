
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<jsp:directive.page import="org.geek.geeksearch.queryer.Response" />
<jsp:directive.page import="org.geek.geeksearch.queryer.Result" />
<%
	System.out.println("进入");
	HashMap<String,Integer> hot_words = Response.hot_words;
	if (request.getParameter("search-text") != null) {
		String key = request.getParameter("search-text");
		if (key.length() != 0) {
			String json = "[";
			Iterator iter = hot_words.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String hot_word = (String) entry.getKey();
				System.out.println(hot_word);
				int freq = (Integer)entry.getValue();
				if (hot_word.contains(key)){
					json += "\"" + hot_word + "\"" + ",";
				}
			}
			json = json.substring(0,
					json.length() - 1 > 0 ? json.length() - 1 : 1);
			json += "]";
			System.out.println("json:" + json);
			out.println(json);
		}
	}
%>

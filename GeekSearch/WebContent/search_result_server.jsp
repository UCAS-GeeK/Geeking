<%@ page language="java" import="java.util.*"  pageEncoding="utf-8"%>
<jsp:directive.page import="org.geek.geeksearch.queryer.Response" />

<jsp:directive.page import="net.sf.json.JSONArray" />
<jsp:directive.page import="net.sf.json.JSONObject" />
<jsp:directive.page import="java.net.URLDecoder" />

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	String key = "";
	Response resp;
	JSONArray results = new JSONArray();
	JSONArray return_results = new JSONArray();
	JSONArray recommend_words = new JSONArray();
	if (request.getParameter("search-text") != null) {
		
		return_results.clear();//清空return_results
		String new_key = URLDecoder.decode(request.getParameter("search-text"), "UTF-8");
		System.out.println("query: "+new_key);
		int index=Integer.parseInt(request.getParameter("pageIndex"));
		int size=Integer.parseInt(request.getParameter("pageSize"));
		System.out.println("index:"+index+" size"+size);
		//这里修改了return_results.add内容，添加的是每个JSONArray
		if (new_key.equals(key)){
			
			for (int i = 0; i < size; i++){
				return_results.add(i,results.getJSONArray(i+size*index));
			}

				
		}else{
			key = new_key;
			resp = new Response();
			results = JSONArray.fromObject(resp.getResponse(key));// 得到新闻信息
			recommend_words = JSONArray.fromObject(resp.get_recommend_query(key));
			
			//这里修改了return_results.add内容，添加的是每个JSONArray
			for (int i = 0; i < size; i++){
				return_results.add(i,results.getJSONArray(i+size*index));
			}

		}
		
		
		JSONObject final_results = new JSONObject();
		//return_results = null;//调试search.jsp对无搜到新闻的反应
		final_results.put("results",return_results);
		
		final_results.put("recommend_words",recommend_words);
		
		System.out.println(final_results.toString());
		//final_results = null;//调试search.jsp对无搜到新闻的反应
		out.println(final_results.toString());
		
	}
%>
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
	JSONArray pagecnt_total = new JSONArray();//添加的返回结果总数目,若用JSONObject,seach.jsp读取失败 
	if (request.getParameter("search-text") != null) {
		
		return_results.clear();//清空return_results
		String new_key = URLDecoder.decode(request.getParameter("search-text"), "UTF-8");
		System.out.println("query: "+new_key);
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		System.out.println("pageIndex:"+pageIndex+" pageSize"+pageSize);
		//这里修改了return_results.add内容，添加的是每个JSONArray,并判断页面请求有没有超出范围
		
		if (new_key.equals(key)){
			if((pageIndex+1)*pageSize<results.size())
				for (int i = (pageIndex)*pageSize; i < (pageIndex+1)*pageSize; i++){
					return_results.add(i,results.getJSONArray(i));
				}
			else//最后一页如果超出范围
			{
				for (int i = (pageIndex)*pageSize; i < results.size(); i++){
					return_results.add(i,results.getJSONArray(i));
				}
			}				
		}else{
			key = new_key;
			resp = new Response();
			recommend_words = JSONArray.fromObject(resp.get_recommend_query(key));
			if(resp.getResponse(key)!=null){
				results = JSONArray.fromObject(resp.getResponse(key));// 得到新闻信息
				pagecnt_total = JSONArray.fromObject(resp.getResultCnt());
				//这里修改了return_results.add内容，添加的是每个JSONArray resp.getResultCnt(),并判断页面请求有没有超出范围
				if((pageIndex+1)*pageSize<results.size())
					for (int i = (pageIndex)*pageSize; i < (pageIndex+1)*pageSize; i++){
						return_results.add(i,results.getJSONArray(i));
					}
				else//最后一页如果超出范围
				{
					for (int i = (pageIndex)*pageSize; i < results.size(); i++){
						return_results.add(i,results.getJSONArray(i));
					}
				}		
			}
			else
			{
				return_results = null;
			}
		}
		
		
		JSONObject final_results = new JSONObject();
		//return_results = null;//调试search.jsp对无搜到新闻的反应
		final_results.put("results",return_results);		
		final_results.put("recommend_words",recommend_words);
		final_results.put("pagecnt_total",pagecnt_total);
		final_results.put("resultscnt",results.size());
		System.out.println(final_results.toString());
		out.println(final_results.toString());
		
	}
%>
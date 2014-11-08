<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<jsp:directive.page import="org.geek.geeksearch.queryer.Response" />
<jsp:directive.page import="org.geek.geeksearch.queryer.Result" />

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Search Result</title>
    
 <style type="text/css"> 
 #search{ 
 text-align: center; 
position:relative; 
	width:78px;
	height:28px;
	font:14px "宋体"
} 
.autocomplete{ 
border: 1px solid #9ACCFB; 
background-color: white; 
text-align: left; 
} 
.autocomplete li{ 
list-style-type: none; 
} 
.clickable { 
cursor: default; 
} 
.highlight { 
background-color: #9ACCFB; 
} 
	#textArea{
	width:300px;
	height:30px;
	font:14px "宋体"
	}
	</style>
	<script type="text/javascript" src="jquery.js"></script>
	<script type="text/javascript" src="auto_complete.js"></script>

  </head>
  
  <body>
    
    <%	
		String keyword = new String(request.getParameter("search-text").getBytes("ISO-8859-1"),"GB2312"); 	
	%>
	
	
    <form id="search" action="search.jsp" method="get">	
		<input name="search-text" type="text" maxlength="100" id="search-text" value=<%=keyword%>>
		<input type="submit" value="搜索一下" id = "submit">
	</form>
	

	
	<%  
		Response resp = new Response();
		ArrayList<Result> results = resp.getResponse(keyword);
		
		System.out.println("返回结果如下");
		for(Result result : results)
		{
	%>	
			<h2><a href=<%=result.getUrl()%>><%=result.getTitle()%></a></h2>
			<p><%=result.getContent()%><p>
			<p><%=result.getUrl()%> &nbsp;&nbsp;&nbsp; <%=result.getDate()%><p>
	<%  		
		}
	%>
    	
  </body>
</html>

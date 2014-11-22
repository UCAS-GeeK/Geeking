<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"
	+request.getServerName()+":"
	+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">    
    <title>dySE</title>
 <style type="text/css"> 
 	#submit{
	width:78px;
	height:28px;
	font:14px "宋体"
	}
	
	#search-text{
	width:300px;
	height:30px;
	font:14px "宋体"
	}
 #search{ 
 	text-align: center; 
	position:relative; 
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
	</style>
	<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript" src="auto_complete.js"></script> 
  </head>
  <body>
	<p align="center"><img src="dySE-logo.jpg" /></p>
	<form id="search" action="search.jsp" method="get" enctype="application/x-www-form-urlencoded">	
		<input align="center" type="text" maxlength="100" id="search-text" name="search-text"/>
		<input type="submit" value="搜索一下"  id ="submit">		
	</form>
  </body>

</html>

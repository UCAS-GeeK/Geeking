<%@ page language="java" import="java.util.*"  pageEncoding="utf-8"%>
<jsp:directive.page import="org.geek.geeksearch.queryer.Response" />


<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>Search Result</title>

<style type="text/css">
#search {
	text-align: center;
	position: relative;
	width: 78px;
	height: 28px;
	font: 14px "宋体"
}

.autocomplete {
	border: 1px solid #9ACCFB;
	background-color: white;
	text-align: left;
}

.autocomplete li {
	list-style-type: none;
}

.clickable {
	cursor: default;
}

.highlight {
	background-color: #9ACCFB;
}

#textArea {
	width: 300px;
	height: 30px;
	font: 14px "宋体"
}
</style>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript" src="auto_complete.js"></script>
<script type="text/javascript" src="jquery.min.js"></script>
<script type="text/javascript" src="jquery.pagination.js"></script>
<script type="text/javascript">
$(function(){
	var pageSize = 3;
	var pageIndex = 0;
//	InitTable(0)
	//此demo通过Ajax加载分页元素		
		var num_entries = 10;
		// 创建分页
		$("#Pagination").pagination(num_entries, {
			num_edge_entries: 1, //边缘页数
			num_display_entries: 4, //主体页数
			callback: pageselectCallback,
			items_per_page: 3, //每页显示1项
			prev_text: "前一页",
			next_text: "后一页"
		});

	 
	function js_method(){
			alert("hello");
	}
	
	function pageselectCallback(page_index, jq){
		$("#Searchresult").empty();//清空Searchresult，否则上一页的内容还会存留
		
		InitTable(page_index);
		return false;
	}
    function InitTable(pageIndex) {
    	var keyword=$("#search-text").val();
//    	alert(keyword);
    	if(keyword=="")
    		alert("请输入query");
    	else
    	{
 //   		contentType: “application/x-www-form-urlencoded; charset=UTF-8″    		
    		$.ajax({
    			'url' : '/GeekSearch/search_result_server.jsp', // 服务器的地址
    			'data' : {'search-text':encodeURI(keyword),'pageIndex':pageIndex, 'pageSize':pageSize}, // 参数
    			'dataType' : 'json', // 返回数据类型
    			'type' : 'POST', // 请求类型
    			'success' : function(data) {
    				
    				if (data.length) {
    					// 遍历data，添加到自动完成区					
    					$.each(data.results, function(index, term) {
    						//$("#Searchresult").append("<div class=\"first\"> </div>");
    						//$("#Searchresult").append("<div class=\"other\"> </div>");
    						$.each(term, function(j,page){
    						// 创建li标签,添加到下拉列表中,
    						//每个类判断j是不是第一个,//$("#Searchresult").append("    "+"<a href= js_method()>相同新闻</a>");//这里要修改
    						//alert("imshow sth");
    						if (term.length>1){
    							if (j==0){
    							//	alert(j);
    								$("#Searchresult").append("<div class=first"+index+"> "
               						+"<h2><a href="+page.url+">"+page.title+"</a></h2>" 
        							+" <button class=show"+index+" type=button>显示相同新闻</button>    "
        	    					+" <button class=hide"+index+" type=button>隐藏相同新闻</button>"
            						+"<p>"+page.description+"</p>"
            						
            						+"网页来源: "+page.url+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>"

            						+"</div>");
        	    						
        	    					
        	    				}
    							
    						
    							else {
    								//alert("+ index +");
    								$("#Searchresult").append("<div class =others"+index+">" 
               						+"<h2><a href="+page.url+">"+page.title+"</a></h2>"
            						+"<p>"+page.description+"</p>"
            						
            						+"网页来源: "+page.url+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>"
            						+"</div>");
    								$(".others"+index ).hide();	
        	    					
        	    				}
    							$(".hide"+index).click(function(){
   								  $(".others"+index ).hide();
   								  });
							  	$(".show"+index).click(function(){
 								  $(".others"+index ).show();
 								  });
    							/*$(".hide"+1).click(function(){
     								  $(".others"+1 ).hide();
     								  });
  							  	$(".show"+1).click(function(){
   								  $(".others"+1 ).show();
   								  });*/
    							
    						}
    						else{
    							
    							$("#Searchresult").append("<div class=first"+index+"> "
               						+"<h2><a href="+page.url+">"+page.title+"</a></h2>" 
            						+"<p>"+page.description+"</p>"
            						
            						+"网页来源: "+page.url+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>"

            						+"</div>");
    							
    						}
    							
    						/*if (j==0){
    							$("#Searchresult").append("<div class=index>"
    							+" <button class=\"show\" type=\"button\">显示相同新闻</button>    "
    	    					+"<button class=\"hide\" type=\"button\">隐藏相同新闻</button>"
    	    					+"</div>");
    	    					
    	    				}*/
    						
    						/*$("#Searchresult").append(
    								"<h2><a href="+page.url+">"+page.title+"</a></h2>"
    								);//<h2>的作用是换行
    						$("#Searchresult").append("<p>"+page.description+"</p>");
    						
    						$("#Searchresult").append("网页来源: "+page.url+"   时间: "+page.pubTime+"    ");
    						$("#Searchresult").append("<a href='RawPages4Test\163\test.html'>快照</a>");//这里要修改
    						*/
    						});
    						
    						
    					});// 事件注册完毕
    					if(data.recommend_words.length){
    						$.each(data.recommend_words,function(index,term){
    							var html="<a href='search.jsp?search-text="+term+"'>"+term+"</a>";
    							$("#recommend_words").append(html);
    						});
    					
    					}
    				}
    			}
    		});    		
    	}    	
    }
	//ajax加载
	
	//相同新闻链接
	/*function js_method()
	{
		alert("imshow sth in js_method");
		//$("#Searchresult").append("网页来源: "+page.url+"   时间: "+page.pubTime+"    ");
	}*/
	
});
</script>
</head>

<body>

	<%
		String keyword = new String(request.getParameter("search-text")
				.getBytes("ISO-8859-1"), "utf-8");
	%>
	<form id="search" action="search.jsp" method="get">
		<input name="search-text" type="text" maxlength="100" id="search-text" value=<%=keyword%>> 
		<input type="submit" value="搜索一下"id="submit">
	</form>

	<div id="recommend_words"><p>推荐词：</p></div>
	<h3>This is a header</h3>
	
	<div id="Searchresult"></div>
	<div id="Pagination" class="pagination"><!-- 这里显示分页 --></div>
</body>
</html>

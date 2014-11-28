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
    			'error': function(data){alert("请求数据失败"+"+data+");alert(data);},
    			'success' : function(data) {
    				var tag = new Array();//用于判断显示相同新闻点击奇偶次数
    				if (data.results!=null) {
    					//alert("data不为空");
    					// 遍历data，添加到自动完成区					
    					$.each(data.results, function(index, term) {
    					//	alert("results"+index);
    						$.each(term, function(j,page){
    						//	alert("term"+j);
    						// 创建li标签,添加到下拉列表中,
    						//判断该类别个数大于1时分组显示
    						if (term.length>1){
    							if (j==0){
    							//	alert("first"+j);
    								$("#Searchresult").append("<div class=first"+index+"> "
               						+"<h2><a href="+page.url+">"+page.title+"</a></h2>" 
        							
            						+"<p>"+page.description+"</p>"
            						
            						+"网页来源: "+page.source+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>     "
            						+"<a  href='javascript:void(0)' class=samenews"+index+"    >显示相同新闻</a>  "
        	    					+"</div>");
        	 					}
     							else {
    							//	alert("others"+j);
    								$("#Searchresult").append("<div class =others"+index+">" 
               						+"<h4><a href="+page.url+">"+page.title+"</a></h2>"
            						+"网页来源: "+page.source+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>"
            						+"</div>");
    								$(".others"+index ).hide();	//默认隐藏
        	    					
        	    				}
     						}
    						//判断该类别个数只等于1时不分组显示
    						else{
    							$("#Searchresult").append("<div class=first"+index+"> "
               						+"<h4><a href="+page.url+">"+page.title+"</a></h2>" 
            						+"<p>"+page.description+"</p>"
            						+"网页来源: "+page.source+"   时间: "+page.pubTime+"    "
            						+"<a href='RawPages4Test\163\test.html'>快照</a>"
            						+"</div>");
     						}
    						});
    						//判断显示相同新闻点击奇偶次数
    						//必须放在$.each(term,外面，否则执行term.length次
    						tag[index] = 0;
    						$(".samenews"+index).click(function(){
    							if(tag[index]==0){
 								//alert(".hide"+index);
								  $(".others"+index ).show();
								  tag[index]= 1;
								  $(".samenews"+index).text("隐藏相同新闻");
    							}
    							else 
    							{
     								//alert(".hide"+index);
    								  $(".others"+index ).hide();
    								  tag[index]= 0;
    								  $(".samenews"+index).text("显示相同新闻");
        							}
							 });
    						
    					});// $.each(data.results,事件注册完毕
    					//判断有没有推荐词
    				/*	if(data.recommend_words.length){
    						$.each(data.recommend_words,function(index,term){
    							var html="<a href='search.jsp?search-text="+term+"'>"+term+"</a>";
    							$("#recommend_words").append(html);
    						});
    					
    					}*/
    				}//if (data.results!=null) 到此结束
    				//如果data.results==null
    				else 
    					{
    					$("#Searchresult").append("<h2>抱歉！没有相关新闻</h2>");
	    					if(data.recommend_words.length){
	    						$.each(data.recommend_words,function(index,term){
	    							var html="<a href='search.jsp?search-text="+term+"'>"+term+"</a>";
	    							$("#recommend_words").append(html);
	    						});
	    					
	    					}
    					}
    			}//'success' : function(data)到此结束，$.ajax还未添加error的function
    		});// $.ajax到此结束  		
    	} //判断(keyword!="") 到此结束  	
    }//InitTable(pageIndex)到此结束
});//$(function()到此结束
</script>
</head>

<body>

	<%
		String keyword="";
		if(request.getParameter("search-text")!=null)
			keyword = new String(request.getParameter("search-text")
				.getBytes("ISO-8859-1"), "utf-8");
	%>
	<form id="search" action="search.jsp" method="get">
		<input name="search-text" type="text" maxlength="100" id="search-text" value=<%=keyword%>> 
		<input type="submit" value="搜索一下"id="submit">
	</form>

	<div id="recommend_words"><p>推荐词：</p></div>

	
	<div id="Searchresult"></div>
	<div id="Pagination" class="pagination"><!-- 这里显示分页 --></div>
</body>
</html>
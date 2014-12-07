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
<script type="text/javascript" src="jquery.highlighter-1.0.0.min.js"></script> 
<style> .highlight{background-color: #FFFF88;} </style>

<script type="text/javascript">
$(function(){
	var pageSize = 4;
	var pageIndex = 0;
    
//	InitTable(0)
	//此demo通过Ajax加载分页元素		
		var tag_pageselectCallback=0;
		// 创建分页
		//function setnum_entries(num_entries)才能从$.ajax()里边获得返回值
		InitTable(0, function setnum_entries(num_entries){
		
		//tag_pageselectCallback=1;
			$("#Pagination").pagination(num_entries, {
				//num_edge_entries: 1, //边缘页数
				//num_display_entries: 4, //主体页数
				callback: pageselectCallback,
				items_per_page: pageSize, //每页显示几项
				prev_text: "前一页",
				next_text: "后一页"
			});
		});//这里会修改num_entries
	function pageselectCallback(page_index, jq){
		if(tag_pageselectCallback==0)
			tag_pageselectCallback=1;
		else{
			$("#Searchresult").empty();//清空Searchresult，否则上一页的内容还会存留
			$("#num_Searchresult").empty();
			$("#recommend_words").empty();
			InitTable(page_index,function donothing(num_entries){;});
			return false;
		}
	}
    function InitTable(pageIndex,callback) {
    	var keyword=$("#search-text").val();
//    	alert(keyword);
    	if(keyword=="")
    		;
    	else
    	{
 //   		contentType: “application/x-www-form-urlencoded; charset=UTF-8″    		
    		$.ajax({
    			'url' : '/GeekSearch/search_result_server.jsp', // 服务器的地址
    			'data' : {'search-text':encodeURI(keyword),'pageIndex':pageIndex, 'pageSize':pageSize}, // 参数
    			'dataType' : 'json', // 返回数据类型
    			'type' : 'POST', // 请求类型
    			'error': function(data){alert("请求数据失败"+data);},
    			'success' : function(data) {
    				var numberSearchresult ;//得到的新闻总条数
    				numberSearchresult = data.pagecnt_total;
    				var num_entries ;//得到的新闻总类数
    				num_entries = data.resultscnt;
    				/*$.each(data.pagecnt_total, function(k, cnt){
    					num_entries = cnt;
    					alert(cnt);
    				});*/
    				//alert(num_entries);
    				callback(num_entries);
    				var tag_samenews  = new Array();//用于判断显示相同新闻点击奇偶次数
    				var count_samenews = new Array();

    				if (data.results!=null) {
    					//alert("data不为空");
    					// 遍历data，添加到自动完成区					
    					$.each(data.results, function(index, term) {
    					//	alert("results"+index);
	    					count_samenews[index] = term.length;
							count_samenews[index] = term.length;
     						//alert(numberSearchresult);
    						$.each(term, function(j,page){
    						//	alert("term"+j);
    						// 创建li标签,添加到下拉列表中,
    						//判断该类别个数大于1时分组显示
    						if (count_samenews[index]>1){
    							if (j==0){
    							//	alert("first"+j);
    								$("#Searchresult").append("<div class=first"+index+" style=width:500px;> "
               						+"<h4><a href='http://www.baidu.com/'"+page.url+">"+page.title+"</a></h2>" 
               						+page.source+"   "+page.pubTime+"    "
            						+"<br width=40% >"+page.description+"</br>"
            						+"<a  href='javascript:void(0)' class=samenews"+index+"    >显示"+(count_samenews[index]-1)+"条相同新闻</a>    "
            						+"<a href='RawPages4Test//qq//qq3.html'> Geeking快照</a>     "
            						+"</div><p></p>");
        	 					}
     							else {
    							//	alert("others"+j);
    								$("#Searchresult").append("<div class =others"+index+" style=width:500px;>" 
									+"<h5><a href="+page.url+">"+page.title+"</a></h2>" 
	               					+page.source+"   "+page.pubTime+"    "
	            					+"<a href='RawPages4Test//qq//qq3.html'> Geeking快照</a>     "
   	            					+"</div>");
    								$(".others"+index ).hide();	//默认隐藏 
        	    				}
     						}
    						//判断该类别个数只等于1时不分组显示
    						else{
    							$("#Searchresult").append("<div class=first"+index+"> "
               						+"<h4><a href="+page.url+">"+page.title+"</a></h2>" 
               						+"网页来源: "+page.source+"   时间: "+page.pubTime+"    "
            						+"<br>"+page.description+"</br>"
            						+"<a href='RawPages4Test//qq//qq3.html'>快照</a>"
            						+"</div><p></p>");
     						}
    						});
    						//判断显示相同新闻点击奇偶次数
    						//必须放在$.each(term,外面，否则执行term.length次
    							
    						tag_samenews[index] = 0;
    						$(".samenews"+index).click(function(){
    							if(tag_samenews[index]==0){
 								//alert(".hide"+index);
								  $(".others"+index ).show();
								  tag_samenews[index]= 1;
								  $(".samenews"+index).text("隐藏"+(count_samenews[index]-1)+"条相同新闻");
    							}
    							else 
    							{
     								//alert(".hide"+index);
    								  $(".others"+index ).hide();
    								  tag_samenews[index]= 0;
    								  $(".samenews"+index).text("显示"+(count_samenews[index]-1)+"条相同新闻");
        							}
							 });
    						
    					});// $.each(data.results,事件注册完毕，给出新闻总篇数
    					$("#num_Searchresult").append("找到相关新闻"+numberSearchresult+"篇");
    				
    					
    				}//if (data.results!=null) 到此结束
    				//如果data.results==null,//判断有没有推荐词
    				else 
    					{
    					$("#Searchresult").append("<h2>抱歉！没有相关新闻</h2>");
    					$("#Pagination").hide();
	    					if(data.recommend_words!=""){
	    						$("#recommend_words").append("<p></p>您是不是要找：");
	    						$.each(data.recommend_words,function(index,term){
	    							var html="<a href='search.jsp?search-text="+term+"'>"+term+"</a>";
	    							$("#recommend_words").append(html+"   ");
	    						});
	    					}
	    					
    					}
    			$('#Searchresult').highlight($("#search-text").val());
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
	    <img src="geeking.jpg" height="40" width="100"/>
		<input name="search-text" type="text" maxlength="10" id="search-text" value=<%=keyword%>> 
		<input type="submit" value="搜索一下" id="submit">
	</form>
<div style="margin-left:100px">

	<div id="num_Searchresult"></div>	
	<div><p> </p></div>
	<div id="Searchresult"></div>
	<div><p> </p></div>
	<div id="recommend_words"></div>
	<div><p> </p></div>
	<div id="Pagination" class="pagination"><!-- 这里显示分页 --></div>
</div>	
</body>
</html>

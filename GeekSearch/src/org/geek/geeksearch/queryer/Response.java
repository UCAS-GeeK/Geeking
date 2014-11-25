package org.geek.geeksearch.queryer;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.geek.geeksearch.model.PageInfo;
import org.geek.geeksearch.recommender.CheckSpell;


public class Response {
	
	private static QueryProcessor processor = new QueryProcessor();//所有response对象共有
	private boolean need_to_recommend = false;// 对象独有
	
	public Response(){
		//do nothing
	}
		
	/* 获取推荐词 */
	public String get_recommend_query(String query){
		if(need_to_recommend){
			ArrayList<String> sug = CheckSpell.suggestSimilar(query,3);
			return JSONArray.fromObject(sug).toString();
		} else {
			return null;
		}
	}
	
	/*服务器端入口*/
	public String getResponse(String query)
	{
		List<List<PageInfo>> resultList = processor.doQuery(query);
		//若无返回结果，则执行搜索词推荐
		if (resultList == null || resultList.isEmpty()) {
			need_to_recommend = true;
			return null;
		}
		//有结果才将query存入热词库
		CheckSpell.store_query(query);
		
		/*construct json and output it*/
		JSONArray json_result = JSONArray.fromObject(resultList);
		
/*		JSONObject json = new JSONObject();
		JSONArray jsonMembers = new JSONArray();
		JSONObject member1 = new JSONObject();
		member1.put("loginname", "zhangfan");
		member1.put("password", "userpass");
		member1.put("email", "10371443@qq.com");
		member1.put("sign_date", "2007-06-12");
		jsonMembers.add(member1);

		JSONObject member2 = new JSONObject();
		member2.put("loginname", "zf");
		member2.put("password", "userpass");
		member2.put("email", "8223939@qq.com");
		member2.put("sign_date", "2008-07-16");
		jsonMembers.add(member2);
		json.put("users", jsonMembers);*/	

		return json_result.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Response response = new Response();
		response.getResponse("");
		System.out.println(response.get_recommend_query("詹姆"));
	}

}

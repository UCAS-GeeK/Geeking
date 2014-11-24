package org.geek.geeksearch.queryer;

import java.util.List;

import net.sf.json.JSONArray;

import org.geek.geeksearch.model.PageInfo;
import org.geek.geeksearch.recommender.CheckSpell;


public class Response {
	
	private static QueryProcessor processor = new QueryProcessor();//初始化在此完成
	
	public Response(){
		//do nothing
	}
	
	public String get_recommend_query(String query){
		return processor.get_recommend_query(query);
	}
	
	/*服务器端入口*/
	public String getResponse(String query)
	{
		List<List<PageInfo>> resultList = processor.doQuery(query); 
		if (resultList == null || resultList.isEmpty()) {
			processor.setNeed_to_recommend(true);
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
		System.out.println(response.get_recommend_query("詹母斯"));
	}

}

package org.geek.geeksearch.queryer;

public class Result {

	private String title;
	private String content;
	private String url;
	private String date;
	
	public Result(String title, String content, String url, String date)
	{
		this.title = title;
		this.content = content;
		this.url = url;
		this.date = date;
	}
	
	
	
	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public String getDate() {
		return date;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

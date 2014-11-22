package org.geek.geeksearch.spider;

public class StartSpider {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //爬取MSN体育新闻网站
        new SpideredMSN().startSpidermsn();
        //爬取网易体育新闻网站
        //new Spider163().startSpidr163();
        //爬取腾讯体育新闻网站
       // new Spiderqq().startSpiderQq();
      //爬取搜狐体育新闻网站
        //new Spidersohu().startSpiderSohu();
	}

}

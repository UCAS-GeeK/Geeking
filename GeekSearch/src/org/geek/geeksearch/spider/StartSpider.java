package org.geek.geeksearch.spider;

import org.geek.geeksearch.configure.Configuration;


public class StartSpider {
	public static Configuration config = new Configuration();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //爬取MSN体育新闻网站
        new SpiderMSN().startSpidermsn();
        //爬取网易体育新闻网站
        //new Spider163().startSpidr163();
        //爬取腾讯体育新闻网站
        //new SpiderQQ().startSpiderQQ();
        //爬取搜狐体育新闻网站
        //new SpiderSohu().startSpiderSohu();
	}

}

package org.geek.geeksearch.spider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.URL;

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.output.FileSystemOutput;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import cn.edu.hfut.dmic.webcollector.util.LogUtils;

//爬取网易体育新闻
public class Spidered163 extends BreadthCrawler{
	
	 private String crawlPath = "crawl";
	 private String root = "data";
	
	@Override
	public void visit(Page page){
		/*System.out.println("URL:"+page.getUrl());
        System.out.println("Content-Type:"+page.getResponse().getContentType());
        System.out.println("Code:"+page.getResponse().getContentType());
        String title=page.getDoc().title();  
        System.out.println(title);*/
        System.out.println("-----------------------------");
        
        FileSystemOutput fsoutput = new FileSystemOutput(root);
        LogUtils.getLogger().info("visit " + page.getUrl());
     
        page.getHtml();//获取网页信息
        
        //-------------------------------------------------------------
        //得到path的具体信息
        try {
            URL _URL = new URL(page.getUrl());
            String query = "";
            if (_URL.getQuery() != null) {
                query = "_" + _URL.getQuery();
            }
            String path ="";// _URL.getPath();
            if (path.length() == 0) {
                path = "index.html";
            } else {
                if (path.charAt(path.length() - 1) == '/') {
                    path = path + "index.html";
                } else {

                    for (int i = path.length() - 1; i >= 0; i--) {
                        if (path.charAt(i) == '/') {
                            if (!path.substring(i + 1).contains(".")) {
                                path = path + ".html";
                            }
                        }
                    }
                }
            }
            path += query;
            
           System.out.println("-------page.getUrl()-------"+page.getUrl());
           
           String replace_file_name = page.getUrl().replace('/', '#').replace(':', '$');
           int NamLen=replace_file_name.length();
           String LastStr=replace_file_name.substring(NamLen-5, NamLen);
           if(LastStr.equals(".html"))
           {
        	   System.out.println("----保存文件**********"+replace_file_name+"***********到本地！！！----");
        	   String file="E:\\GeekSpider\\sports_163\\"+replace_file_name;
               new CreateHtml().fileOutput(file,page.getHtml().toString());
           }
        	   
             
           
        } catch (Exception e) {
            e.printStackTrace();
        }
   
            //-----------------------------------------------------------------
        
        
	}
	
	
	
	public boolean startSpidr163(){
		 Spidered163 crawler = new Spidered163();  
	        crawler.addSeed("http://sports.163.com/");         
	        
	        crawler.addRegex("+http://sports.163.com/.*");
	        crawler.addRegex("-.*#.*");
	        crawler.addRegex("-.*png.*");
	        crawler.addRegex("-.*jpg.*");
	        crawler.addRegex("-.*gif.*");
	        crawler.addRegex("-.*js.*");
	        crawler.addRegex("-.*css.*");
	        
	        /*设置线程数*/
	        crawler.setThreads(30);
	        
	        /*设置爬虫是否为断点爬取*/
	        crawler.setResumable(false);
	        
	       
	        /*深度为5*/  
	        try {
				crawler.start(6);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
		return true;
	}
	
}

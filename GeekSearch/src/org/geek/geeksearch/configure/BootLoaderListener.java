package org.geek.geeksearch.configure;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.queryer.Response;
import org.geek.geeksearch.recommender.CheckSpell;
import org.geek.geeksearch.util.DBOperator;

public class BootLoaderListener implements ServletContextListener {
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//初始化配置
		new Configuration("configure.properties");
		
		//初始化数据库
		new DBOperator(new Configuration());
		
		//初始化分词，加载词典
//		new Tokenizer(new Configuration());
		
		//初始化Response，加载倒排索引
		new Response();
		
		//初始化CheckSpell,加载关键词
		new CheckSpell();
		
		System.out.println("===== 初始化完成  =====");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//关闭数据库连接
		DBOperator.close();
		
		System.out.println("===== 退出Geeking =====");
		
	}
}

package org.geek.geeksearch.configure;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.geek.geeksearch.indexer.Tokenizer;
import org.geek.geeksearch.queryer.Response;
import org.geek.geeksearch.recommender.CheckSpell;
import org.geek.geeksearch.util.DBOperator;

public class BootLoaderListener implements ServletContextListener {
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long start = System.currentTimeMillis();
		//初始化配置
		new Configuration("configure.properties");
		
		//初始化数据库
		new DBOperator(new Configuration());
		
		//初始化分词，加载词典
		new Tokenizer(new Configuration());
		
		//初始化Response，加载倒排索引
		new Response();
		
		//初始化CheckSpell,加载关键词
		new CheckSpell();
		
		long time = (System.currentTimeMillis()-start)/1000;
		System.out.println("===== 初始化完成  用时:"+time+"秒 =====");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("===== 正在退出Geeking =====");
		//关闭数据库连接
		DBOperator.close();
		
	}
}

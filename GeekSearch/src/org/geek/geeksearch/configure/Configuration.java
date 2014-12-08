package org.geek.geeksearch.configure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	/**
	 * 读取properties文件
	 * 所有对象共用
	 */
	private static final Properties propertie = new Properties();

	/**
	 * 初始化Configuration类
	 */
	public Configuration(String configFile) {
		try {
			propertie.load(getClass().getClassLoader().getResourceAsStream(configFile));
		} catch (FileNotFoundException ex) {
			System.err.println("文件路径错误或者文件不存在");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.err.println("装载文件失败!");
			ex.printStackTrace();
		}
	}
	
	/**/
	public Configuration() {
	}

	/**
	 * @return key的值
	 */
	public String getValue(String key) {
		if (propertie.containsKey(key)) {
			String value = propertie.getProperty(key);//得到某一属性的值
			return value;
		} else
			return "";
	}

}

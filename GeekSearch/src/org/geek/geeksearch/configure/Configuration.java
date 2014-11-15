package org.geek.geeksearch.configure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	/**
	 * 读取properties文件
	 */
	private final Properties propertie;

	/**
	 * 初始化Configuration类
	 */
	public Configuration() {
		propertie = new Properties();
		try {
			propertie.load(getClass().getClassLoader().getResourceAsStream("configure.properties"));
		} catch (FileNotFoundException ex) {
			System.err.println("文件路径错误或者文件不存在");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.err.println("装载文件失败!");
			ex.printStackTrace();
		}
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

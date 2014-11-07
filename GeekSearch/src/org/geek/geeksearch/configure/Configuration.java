package org.geek.geeksearch.configure;

//import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	/**
	 * 读取properties文件
	 */
	private Properties propertie;
	//private FileInputStream inputFile;
	//private FileOutputStream outputFile;

	/**
	 * 初始化Configuration类
	 */
	public Configuration() {
		propertie = new Properties();
		try {
			propertie.load(getClass().getClassLoader().getResourceAsStream("configure.properties"));
		} catch (FileNotFoundException ex) {
			System.out.println("文件路径错误或者文件不存在");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("装载文件失败!");
			ex.printStackTrace();
		}
	}

	/**
	 * @return key的值
	 */
	public String getValue(String key) {
		if (propertie.containsKey(key)) {
			String value = propertie.getProperty(key);//得到某一属性的值ֵ
			return value;
		} else
			return "";
	}


	public static void main(String[] args) {
		Configuration conf = new Configuration();

		String path_GeekDB = conf.getValue("path_GeekDB");
		String user_GeekDB = conf.getValue("user_GeekDB");
		String password_GeekDB = conf.getValue("password_GeekDB");

		System.out.println("the path is " + path_GeekDB);
		System.out.println("the user is " + user_GeekDB);
		System.out.println("the password is " + password_GeekDB);
	}

}

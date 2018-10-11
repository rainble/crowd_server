package fudan.mcd.config;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {
	private static File file;
	private static ConfigLoader manager;
	private static Properties properties;

	private ConfigLoader(String path) {
		try {
			file = new File(path);
			properties = new Properties();
			properties.loadFromXML(new FileInputStream(file));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取参数读取器的实例对象。
	 * 
	 * @return 参数读取器
	 */
	public static ConfigLoader getInstance(String path) {
		if (manager == null)
			manager = new ConfigLoader(path);
		return manager;
	}

	/**
	 * 读取指定的参数名对应的参数值。如果该参数名不存在，返回null。
	 * 
	 * @param key
	 *            参数名
	 * @return 参数值
	 */
	public String load(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 插入一条新的参数条目。如果该参数名已存在，则覆盖原有的值。
	 * 
	 * @param key
	 *            参数名
	 * @param value
	 *            参数值
	 */
	public void save(String key, String value) {
		try {
			properties.put(key, value);
			properties.storeToXML(new FileOutputStream(file), "");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除一条已有的参数条目。如果该参数名不存在，则不执行任何操作。
	 * 
	 * @param key
	 *            参数名
	 */
	public void remove(String key) {
		try {
			properties.remove(key);
			properties.storeToXML(new FileOutputStream(file), "");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package fudan.mcd.utils;

import com.alibaba.fastjson.JSON;

public class JSONUtils {
	/**
	 * 将指定的字符串反序列化为Java对象
	 * 
	 * @param json
	 *            需要解析的字符串
	 * @param clazz
	 *            Java对象类型
	 * @return Java对象
	 */
	public static <T> T toBean(String json, Class<T> clazz) {
		return (T) JSON.parseObject(json, clazz);
	}

	/**
	 * 将指定的对象序列化为Java对象
	 * 
	 * @param object
	 *            需要序列化的对象
	 * @return JSON字符串
	 */
	public static String toJSONString(Object object) {
		return JSON.toJSONString(object);
	}
}

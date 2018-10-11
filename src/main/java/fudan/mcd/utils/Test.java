package fudan.mcd.utils;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		WeixinUtil weixin = new WeixinUtil();
		Map<String,String> data = new HashMap<String, String>();
		data.put("title","新任务发布通知");
		data.put("description", "用户xxx发布了新任务：取快递。目的地为阿康烧烤，截止时间为2017/7/10 21:00:00");
		data.put("url", "http://www.baidu.com");
		weixin.sendMessage("textcard",data);
		data.clear();
		data.put("content", "测试发送消息，你有收到吗？");
		weixin.sendMessage("text",data);
		
	}

}

package fudan.mcd.utils;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;

public class WeixinUtil {
	//企业号相关信息
	private static final String CORPID = "wx751e041cdef857d6";
	private static final String CORPSECRET = "HPQwjnca_An3LjptuP8zdQWvxkOANDn3FhLygAHLwe0";
	//相关API调用地址
	private static String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CORPID&corpsecret=CORPSECRET";
	private static String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
	//固定参数
	private static final int CROWDFRAME_AGENTID = 1;
	private String access_token;
	
	public WeixinUtil(){	
		try{
	         // 本地连接 Memcached 服务
	         MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));
	         System.out.println("Connection to server sucessful.");
	         System.out.println(mcc.get("java_access_token"));
	         //查询缓存当中是否有未过期的access token
	         if(mcc.get("java_access_token") != null){
	        	 this.access_token = mcc.get("java_access_token").toString();
		         System.out.println("java_access_token value in cache - " + mcc.get("java_access_token"));
	         }
	     	 //缓存中没有，则发送请求获取access_token
	         else{
	     		String requestUrl = ACCESS_TOKEN_URL.replace("CORPID", CORPID).replace("CORPSECRET", CORPSECRET);
	     		JSONObject jsonObject = HttpRequestUtil.httpRequest(requestUrl, EnumMethod.GET.name(), null);
	     		if (null != jsonObject) {
	     			try {
	     				System.out.println("Get access token through the api. The value is " + jsonObject.getString("access_token"));
	     				this.access_token = jsonObject.getString("access_token");
	     				//虽然Future看起来只是获取状态的作用，但是不写的话就会没效果，很奇怪。猜想是跟缓冲一样的道理
	     				Future<Boolean> fo = mcc.set("java_access_token",7200,jsonObject.getString("access_token"));
	     				System.out.println("set status:" + fo.get());
	     			} catch (JSONException e) {
	     		         System.out.println( e.getMessage() );
	     			}
	     		}
	         }
	         // 关闭连接
	         mcc.shutdown();
	         
	     }catch(Exception ex){
	         System.out.println( ex.getMessage() );
	     }
	}

	//发送消息
	public void sendMessage(String type, Map<String, String> data){
		String requestUrl = SEND_MESSAGE_URL.replace("ACCESS_TOKEN", this.access_token);
		//根据消息类型构造消息体
		String postJson = "";
		String outputStr = "";
		switch(type){
			case "text":
				postJson = "{\"agentid\":%d,\"touser\": \"%s\","+
					     "\"msgtype\":\"%s\",\"text\": {\"content\": \"%s\"},\"safe\":0}";
				outputStr = String.format(postJson,CROWDFRAME_AGENTID,data.get("touser"),"text",data.get("content"));
			break;
			case "textcard":
				postJson = "{\"agentid\":%d,\"touser\": \"%s\","+
					     "\"msgtype\":\"%s\",\"textcard\": {\"title\": \"%s\",\"description\": \"%s\",\"url\": \"%s\"},\"safe\":0}";
				outputStr = String.format(postJson,CROWDFRAME_AGENTID,data.get("touser"),"textcard",data.get("title"),data.get("description"),data.get("url"));
			break;
		}
		//发送消息并接受打印返回信息
		JSONObject jsonObject = HttpRequestUtil.httpRequest(requestUrl, EnumMethod.POST.name(), outputStr);
		if(null != jsonObject)
			System.out.println(jsonObject.getInt("errcode"));
		
	}
}

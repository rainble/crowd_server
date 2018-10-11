package fudan.mcd.runtime;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class JPushUtil {
	private static final Log LOG = LogFactory.getLog(JPushUtil.class);
	//测试值
	private static final String MASTER_SECRET = "e23bc8ebdcd9ea87197b9402";
	private static final String APP_KEY = "50fa0851a51fb44bc12bfa3e";
	//运行值
//	private static final String MASTER_SECRET = "ccf6bddc18a73a41293673e1";
//	private static final String APP_KEY = "a4d9b7c502630435f172154b";
	public static String TYPE_OF_ACCEPTTASK = "1", TYPE_OF_COMPLETETASK = "2",TYPE_OF_PUBLISHTASK = "3";
	
	public static void pushMessage(String alias,String content,Map<String,String> map){
		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

	    // For push, all you need do is to build PushPayload object.
	    PushPayload payload = buildMyPayload(alias,content,map);

	    try {
	        PushResult result = jpushClient.sendPush(payload);
	        LOG.info("Got result - " + result);

	    } catch (APIConnectionException e) {
	        // Connection error, should retry later
	        LOG.error("Connection error, should retry later", e);

	    } catch (APIRequestException e) {
	        // Should review the error, and fix the request
	        LOG.error("Should review the error, and fix the request", e);
	        LOG.info("HTTP Status: " + e.getStatus());
	        LOG.info("Error Code: " + e.getErrorCode());
	        LOG.info("Error Message: " + e.getErrorMessage());
	    }
	}
	
	public static PushPayload buildMyPayload(String alias, String content,Map<String,String> map) {
		//设定生产环境
		Options option = Options.newBuilder()
				.setApnsProduction(true)
				.build();
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.android("CrowdFrame", content, map))
                .setOptions(option)
                .build();
    }

}

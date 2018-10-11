package fudan.mcd.test;

import fudan.mcd.config.ConfigLoader;
import fudan.mcd.config.ConfigParameter;
import fudan.mcd.dao.abs.AbstractDAO;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class HttpPostProxy {
	private static String ip;
	private static String port;
	private static String resource_name;


	static {
		ConfigLoader loader = ConfigLoader.getInstance(AbstractDAO.DEVELOP_CONFIG_PATH);
		ip = loader.load(ConfigParameter.SERVER_IP);
		port = loader.load(ConfigParameter.SERVER_PORT);
		resource_name = loader.load(ConfigParameter.SERVER_RESOURCE_NAME);
	}

	public static String doPost(String servlet, Map<String, String> paramMap) {
		try {
			@SuppressWarnings("resource")
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(String.format("http://%s:%s/%s/%s", ip, port, resource_name, servlet));
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				paramList.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			UrlEncodedFormEntity params = new UrlEncodedFormEntity(paramList, "utf-8");
			post.setEntity(params);
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
			String result = (IOUtils.toString(reader));
			post.abort();
			reader.close();
			client.getConnectionManager().shutdown();
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String doGet(double src_lat, double src_lon, double dest_lat, double dest_lon){
		try {
			@SuppressWarnings("resource")
			HttpClient client = new DefaultHttpClient();
			String string = String.format("http://api.map.baidu.com/routematrix/v2/walking?output=json&origins=%f,%f&destinations=%f,%f&ak=rc0bdxZq39fY9aDahOGDLLVmYgh2hrnI", src_lat, src_lon, dest_lat, dest_lon);
			HttpGet post = new HttpGet(string);
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
			String result = (IOUtils.toString(reader));
			post.abort();
			reader.close();
			client.getConnectionManager().shutdown();
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

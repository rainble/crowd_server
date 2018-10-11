package fudan.mcd.servlet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServletUtils {
	public static ServletResponseData generateParseFailedData() {
		ServletResponseData data = new ServletResponseData();
		data.setResult(ServletResponseData.RESULT_PARSE_FAILED);
		data.setData("");
		return data;
	}
	
	public static String getTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(new Date());
		return time;
	}
	
	public static String getTime(Timestamp time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(time);
        return timeStr;
	}
}

package fudan.mcd.servlet;

import fudan.mcd.service.PublishSimpleTaskService;
import fudan.mcd.utils.HttpRequestUtil;
import fudan.mcd.utils.JSONUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fudan.mcd.vo.SimpleTaskVO;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@WebServlet("/PublishSimpleTaskServlet")
public class PublishSimpleTaskServlet extends HttpServlet{

    private static final Log LOG = LogFactory.getLog(PublishSimpleTaskServlet.class);

    public PublishSimpleTaskServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        RequestBO requestBO;
//        SimpleTaskVO simpleTaskVO = new SimpleTaskVO();
//        try {
//            String jsonString = req.getParameter("data");
//            System.out.print(jsonString);
//            requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
//            LOG.info(String.format("User [ %s ] request to publish task request at [ %s ].", requestBO.getUserId(), ServletUtils.getTime()));
//            // Set the info of TaskVO
//            simpleTaskVO.setUserId(Integer.parseInt(requestBO.getUserId()));
//            simpleTaskVO.setTaskId(Integer.parseInt(requestBO.getTaskId()));
//            simpleTaskVO.setTaskDesc((requestBO.getTaskDesc()));
//            simpleTaskVO.setBonus(Integer.parseInt(requestBO.getBonus()));
//            simpleTaskVO.setDuration(Integer.parseInt(requestBO.getDuration()));
//            simpleTaskVO.setLocationDesc(requestBO.getLocationDesc());
//            simpleTaskVO.setCallbackUrl(requestBO.getUrl());
//        }catch (Exception e){
//            String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
//            resp.getOutputStream().println(responseString);
//            LOG.info(String.format("Receive publish task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
//            return;
//        }
//
//        PublishSimpleTaskService service = new PublishSimpleTaskService(getServletContext());
//
//        int result = service.insertTask(simpleTaskVO);
//        ServletResponseData responseData = new ServletResponseData();
//        ResponseBO responseBO = new ResponseBO();
//        responseBO.setSimTaskId(simpleTaskVO.getTaskId());
//        responseBO.setLog(req.getParameter("data"));
//        responseBO.setDesc_test(simpleTaskVO.getTaskDesc());
//
//
//
//        responseData.setResult(result);
//        responseData.setData(JSONUtils.toJSONString(responseBO));
//        resp.setContentType("text/html;charset=UTF-8");
//        resp.getWriter().println(JSONUtils.toJSONString(responseData));
//        LOG.info(String.format("The result of insert is [ %s ]", result));
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SimpleTaskVO simpleTaskVO = new SimpleTaskVO();

        try {
            simpleTaskVO.setUserId(Integer.parseInt(request.getParameter("userId")));
            simpleTaskVO.setBonus(Integer.parseInt(request.getParameter("bonus")));
            simpleTaskVO.setDuration(Integer.parseInt(request.getParameter("duration")));
            simpleTaskVO.setLocationDesc(request.getParameter("locationDesc"));
            simpleTaskVO.setTaskDesc(request.getParameter("taskDesc"));
            simpleTaskVO.setCallbackUrl(request.getParameter("callbackUrl"));
        } catch (Exception e) {
            LOG.info(String.format("Receive publish task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            e.getStackTrace();
            return;
        }

        PublishSimpleTaskService service = new PublishSimpleTaskService(getServletContext());
        int result = service.insertTask(simpleTaskVO);

        String SendMessageContent = "This task description is [ " + simpleTaskVO.getTaskDesc() +"]  at [ " + simpleTaskVO.getLocationDesc() + " ], which is published bu user NO." +
                simpleTaskVO.getUserId() + ". Bonus is " + simpleTaskVO.getBonus() + ". Publisher want you to finish this task in " + simpleTaskVO.getDuration() + " minutes. If you want to " +
                "accept this task, click please.";
//        String AcceptTaskUrl = HttpRequestUtil.AcceptTask_URL + "?userId=" + simpleTaskVO.getUserId() + "%26taskId=" + simpleTaskVO.getTaskId();
        String AcceptTaskUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?";
        String AcceptTaskUrlPara = "appid=wxcb6063d8a90280c6%26redirect_uri=http%3A%2F%2Fwww.fudanse.club%2Fwm%2Fwx%2Fredirect%2Fwxcb6063d8a90280c6%2FAcceptTask" + simpleTaskVO.getTaskId() + "%26response_type=code%26scope=snsapi_userinfo%26state=%26connect_redirect=1#wechat_redirect\"";
//        Map<String, String> AcceptTaskUrlPara = new HashMap<String, String>();
//        AcceptTaskUrlPara.put("appid", "wxcb6063d8a90280c6");
//        AcceptTaskUrlPara.put("redirect_uri", "http%3A%2F%2Fwww.fudanse.club%2Fwm%2Fwx%2Fredirect%2Fwxcb6063d8a90280c6%2FAcceptTask" + simpleTaskVO.getTaskId() + "%26response_type=code%26scope=snsapi_userinfo%26state=%26connect_redirect=1#wechat_redirect");

        String SendMessagePara = "content='" + SendMessageContent + "'&url=" + AcceptTaskUrl + AcceptTaskUrlPara;
//        String SendMessageUrl = String.format("http://%s?%s", HttpRequestUtil.WXMessage_URL, SendMessagePara);
        String res = null;
        res = sendPost(HttpRequestUtil.WXMessage_URL, SendMessagePara);
        LOG.info(String.format("Send wechat message is [ %s ].", res));

        ServletResponseData responseData = new ServletResponseData();
        ResponseBO responseBO = new ResponseBO();
        responseBO.setSimTaskId(simpleTaskVO.getTaskId());
        responseBO.setLog(request.getParameter("data"));
        responseBO.setDesc_test(simpleTaskVO.getTaskDesc());


        responseData.setResult(result);
        responseData.setData(JSONUtils.toJSONString(responseBO));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(JSONUtils.toJSONString(responseData));
        LOG.info(String.format("The result of insert is [ %s ]", result));


    }

    public static class RequestBO {

        private String userId;
        private String taskDesc;
        private String locationDesc;
        private String duration;
        private String bonus;
        private String url;
        private String taskId;

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setTaskDesc(String taskDesc) {
            this.taskDesc = taskDesc;
        }

        public void setLocationDesc(String locationDesc) {
            this.locationDesc = locationDesc;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public void setBonus(String bonus) {
            this.bonus = bonus;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getDuration() {
            return duration;
        }

        public String getBonus() {
            return bonus;
        }

        public String getTaskDesc() {
            return taskDesc;
        }

        public String getLocationDesc() {
            return locationDesc;
        }

        public String getUserId() {
            return userId;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class ResponseBO {

        private int simTaskId;
        private String log;
        private String desc_test;

        public void setDesc_test(String desc_test) {
            this.desc_test = desc_test;
        }

        public String getDesc_test() {
            return desc_test;
        }

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public void setSimTaskId(int simTaskId) {
            this.simTaskId = simTaskId;
        }

        public int getSimTaskId() {
            return simTaskId;
        }
    }

    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}

package fudan.mcd.servlet;

import fudan.mcd.service.PublishSimpleTaskService;
import fudan.mcd.utils.HttpRequestUtil;
import fudan.mcd.utils.JSONUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import fudan.mcd.vo.SimpleTaskVO;
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
            simpleTaskVO.setTaskId(Integer.parseInt(request.getParameter("taskId")));
            simpleTaskVO.setBonus(Integer.parseInt(request.getParameter("bonus")));
            simpleTaskVO.setDuration(Integer.parseInt(request.getParameter("duration")));
            simpleTaskVO.setLocationDesc(request.getParameter("locationDesc"));
            simpleTaskVO.setTaskDesc(request.getParameter("taskDesc"));
            simpleTaskVO.setCallbackUrl(request.getParameter("url"));
        } catch (Exception e) {
            LOG.info(String.format("Receive publish task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            e.getStackTrace();
            return;
        }


        PublishSimpleTaskService service = new PublishSimpleTaskService(getServletContext());
        int result = service.insertTask(simpleTaskVO);

        String SendMessageContent = "This task need you " + simpleTaskVO.getTaskDesc() +" at " + simpleTaskVO.getLocationDesc() + ", which is published bu user NO." +
                simpleTaskVO.getUserId() + ". Bonus is " + simpleTaskVO.getBonus() + ". Publisher want you to finish this task in " + simpleTaskVO.getDuration() + " minutes. If you want to " +
                "accept this task, click please.";
        String AcceptTaskUrl = HttpRequestUtil.AcceptTask_URL + "?userId=" + simpleTaskVO.getUserId() + "&taskId=" + simpleTaskVO.getTaskId();
        String SendMessagePara = "content=" + SendMessageContent + "&url=" + AcceptTaskUrl;
        String SendMessageUrl = String.format("http://%s?%s", HttpRequestUtil.WXMessage_URL, SendMessagePara);
        String res = null;
        res = HttpRequestUtil.HTTPRequestDoGet(SendMessageUrl);
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

}

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        RequestBO requestBO;
        SimpleTaskVO simpleTaskVO = new SimpleTaskVO();
        try {
            String jsonString = req.getParameter("data");
            System.out.print(jsonString);
            requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
            LOG.info(String.format("User [ %d ] request to publish task request at [ %s ].", requestBO.getUserId(), ServletUtils.getTime()));
            // Set the info of TaskVO
            simpleTaskVO.setUserId(requestBO.getUserId());
            simpleTaskVO.setTaskId(requestBO.getTaskId());
            simpleTaskVO.setTaskDesc(requestBO.getTaskDesc());
            simpleTaskVO.setBonus(requestBO.getBonus());
            simpleTaskVO.setDuration(requestBO.getDuration());
            simpleTaskVO.setLocationDesc(requestBO.getLocationDesc());
            simpleTaskVO.setCallbackUrl(requestBO.getUrl());
        }catch (Exception e){
            String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
            resp.getOutputStream().println(responseString);
            LOG.info(String.format("Receive publish task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            return;
        }

        PublishSimpleTaskService service = new PublishSimpleTaskService(getServletContext());

        int result = service.insertTask(simpleTaskVO);
        ServletResponseData responseData = new ServletResponseData();
        ResponseBO responseBO = new ResponseBO();
        responseBO.setSimTaskId(simpleTaskVO.getTaskId());
        responseBO.setLog(req.getParameter("data"));
        responseBO.setDesc_test(simpleTaskVO.getTaskDesc());



        responseData.setResult(result);
        responseData.setData(JSONUtils.toJSONString(responseBO));
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println(JSONUtils.toJSONString(responseData));
        LOG.info(String.format("The result of insert is [ %s ]", result));
    }





    public static class RequestBO {

        private int userId;
        private String taskDesc;
        private String locationDesc;
        private int duration;
        private int bonus;
        private String url;
        private int taskId;

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setTaskDesc(String taskDesc) {
            this.taskDesc = taskDesc;
        }

        public void setLocationDesc(String locationDesc) {
            this.locationDesc = locationDesc;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setBonus(int bonus) {
            this.bonus = bonus;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public int getTaskId() {
            return taskId;
        }

        public int getDuration() {
            return duration;
        }

        public int getBonus() {
            return bonus;
        }

        public String getTaskDesc() {
            return taskDesc;
        }

        public String getLocationDesc() {
            return locationDesc;
        }

        public int getUserId() {
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

package fudan.mcd.servlet;


import fudan.mcd.service.AcceptSimpleTaskService;
import fudan.mcd.utils.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/AcceptSimpleTaskServlet")
public class AcceptSimpleTaskServlet extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(AcceptSimpleTaskServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        int userId, taskId;
        try {
//            String json = request.getParameter("data");
//            requestBO requestBO = JSONUtils.toBean(json, requestBO.class);
//            userId = requestBO.getUserId();
//            taskId = requestBO.getTaskId();
            userId = Integer.parseInt(request.getParameter("userId"));
            taskId = Integer.parseInt(request.getParameter("taskId"));

        } catch (Exception e) {
            String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
            response.getOutputStream().println(responseString);
            LOG.info(String.format("request data is [ %s ]", request.getParameter("data")));
            LOG.info(String.format("Receive acceptTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            return;
        }

        AcceptSimpleTaskService acceptSimpleTaskService = new AcceptSimpleTaskService(getServletContext());
        int res = acceptSimpleTaskService.insert_task(userId, taskId);

        // Generate response data

        /*
        callback部分
         */
        String callback = "data";

        ServletResponseData responseData = new ServletResponseData();
        ResponseBO responseBO = new ResponseBO();
        responseData.setResult(res);
        responseData.setData(JSONUtils.toJSONString(responseBO));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(callback + "(" + JSONUtils.toJSONString(responseData) + ")" + ", 您已接受该任务。");
    }


    public static class requestBO {
        private int userId;
        private int taskId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }

    public static class ResponseBO {

    }


    }

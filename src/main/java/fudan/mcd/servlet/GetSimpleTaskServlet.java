package fudan.mcd.servlet;

import fudan.mcd.service.PublishSimpleTaskService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.SimpleTaskVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runners.Parameterized;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//查看所有的正在等待完成的任务

@WebServlet("/GetSimpleTaskServlet")
public class GetSimpleTaskServlet extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(GetSimpleTaskServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        int userId;
        try {
            String json = request.getParameter("data");
            requestBO requestBO = JSONUtils.toBean(json, requestBO.class);
            userId = requestBO.getUserId();
        } catch (Exception e) {
            String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
            response.getOutputStream().println(responseString);
            LOG.info(String.format("Receive acceptTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            return;
        }

        PublishSimpleTaskService simpleTaskService = new PublishSimpleTaskService(getServletContext());
        List<SimpleTaskVO> res = simpleTaskService.querySimpleTaskByUser(userId);
        List<SimpleTaskVO> simpleTaskVOList = new ArrayList<SimpleTaskVO>();
        for (SimpleTaskVO simpleTaskVO : res) {
            SimpleTaskVO simpleTaskVO1 = new SimpleTaskVO();
            simpleTaskVO1.setUserId(simpleTaskVO.getUserId());
            simpleTaskVO1.setTaskId(simpleTaskVO.getTaskId());
            simpleTaskVO1.setTaskDesc(simpleTaskVO.getTaskDesc());
            simpleTaskVO1.setLocationDesc(simpleTaskVO.getLocationDesc());
            simpleTaskVO1.setDuration(simpleTaskVO.getDuration());
            simpleTaskVO1.setBonus(simpleTaskVO.getBonus());
            simpleTaskVO1.setPublishTime(simpleTaskVO.getPublishTime());
            simpleTaskVOList.add(simpleTaskVO1);
        }

        // Generate response data
        ServletResponseData responseData = new ServletResponseData();
        ResponseBO responseBO = new ResponseBO();
        responseData.setResult(1);
        responseBO.setSimpleTaskVOS(simpleTaskVOList);
        responseData.setData(JSONUtils.toJSONString(responseBO));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(JSONUtils.toJSONString(responseData));

    }


    public static class requestBO {
        private int userId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

    }

    public static class ResponseBO {
        private List<SimpleTaskVO> simpleTaskVOS;

        public List<SimpleTaskVO> getSimpleTaskVOS() {
            return simpleTaskVOS;
        }

        public void setSimpleTaskVOS(List<SimpleTaskVO> simpleTaskVOS) {
            this.simpleTaskVOS = simpleTaskVOS;
        }
    }




}

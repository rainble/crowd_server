package fudan.mcd.servlet;

import fudan.mcd.service.AcceptSimpleTaskService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.SimpleTaskVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetAcceptSimpleTaskServlet")
public class GetAcceptSimpleTaskServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(AcceptSimpleTaskServlet.class);


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

        AcceptSimpleTaskService service = new AcceptSimpleTaskService(getServletContext());
        List<SimpleTaskVO> rvoList = service.queryAcceptSimpleTaskListByUser(userId);

        ServletResponseData responseData = new ServletResponseData();
        responseBO responseBO = new responseBO();
        List<SimpleTaskVO> simpleTasks = new ArrayList<SimpleTaskVO>();
        for (SimpleTaskVO vo : rvoList) {
            SimpleTaskVO simpleTaskBO = new SimpleTaskVO();
            simpleTaskBO.setUserId(vo.getUserId());
            simpleTaskBO.setTaskId(vo.getTaskId());
            simpleTaskBO.setPublishTime(vo.getPublishTime());
            simpleTaskBO.setTaskDesc(vo.getTaskDesc());
            simpleTaskBO.setLocationDesc(vo.getLocationDesc());
            simpleTaskBO.setDuration(vo.getDuration());
            simpleTaskBO.setBonus(vo.getBonus());
            simpleTasks.add(simpleTaskBO);
        }
        responseBO.setSimpleTasks(simpleTasks);

        /*
        callback部分
         */

        String callback = "data";

        // Transform the responseBO to json string and output it
        responseData.setResult(1);
        responseData.setData(JSONUtils.toJSONString(responseBO));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(callback + "(" + JSONUtils.toJSONString(responseData) + ")");
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

    public static class responseBO {
        private List<SimpleTaskVO> simpleTasks;

        public List<SimpleTaskVO> getSimpleTasks() {
            return simpleTasks;
        }

        public void setSimpleTasks(List<SimpleTaskVO> simpleTasks) {
            this.simpleTasks = simpleTasks;
        }
    }

}

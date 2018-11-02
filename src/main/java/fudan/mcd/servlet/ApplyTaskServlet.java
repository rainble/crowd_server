package fudan.mcd.servlet;

import fudan.mcd.service.AcceptTaskService;
import fudan.mcd.service.ApplyTaskService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.ApplyVO;
import fudan.mcd.vo.LocationVO;
import fudan.mcd.vo.UndertakeVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/ApplyTaskServlet")
public class ApplyTaskServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(ApplyTaskServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        // Parse request parameters
        ApplyVO avo = new ApplyVO();
        ApplyTaskServlet.RequestBO requestBO;
        try {
            String jsonString = req.getParameter("data");
            requestBO = JSONUtils.toBean(jsonString, ApplyTaskServlet.RequestBO.class);
            avo.setUserId(requestBO.getUserId());
            avo.setStartTime(requestBO.getStartTime());
            avo.setContractTime(requestBO.getContractTime());
            avo.setTaskId(requestBO.getTaskId());
            avo.setCurrentStage(requestBO.getCurrentStage());
            //LOG.info(String.format("Receive acceptTask request [ userId = %d, taskId = %d ].", requestBO.getUserId(), requestBO.getTaskId()));
        }
        catch (Exception e) {
            String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
            resp.getOutputStream().println(responseString);
            LOG.info(String.format("Receive acceptTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
            return;
        }

        // Do business operation
        ApplyTaskService service = new ApplyTaskService(getServletContext());
//        avo.setLocationId(insertLoc(requestBO.getLocationBO(), avo.getCurrentStage(), service));

        //将请求插入报名表Apply当中
        service.insertApply(avo);
        int result;

    }


    private int insertLoc(LocationBO locationBO, int stageId, ApplyTaskService service) {
        LocationVO location = new LocationVO();
        location.setStageId(stageId);
        location.setAddress(locationBO.getAddress());
        location.setLatitude(locationBO.getLatitude());
        location.setLongitude(locationBO.getLongitude());
        location.setType(locationBO.getType());

        return service.insertLocation(location);
    }

    public static class RequestBO {
        private int userId;
        private int taskId;
        private int currentStage;
        private Timestamp startTime;
        private Timestamp contractTime;
//        private LocationBO locationBO;

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

        public int getCurrentStage() {
            return currentStage;
        }

        public void setCurrentStage(int currentStage) {
            this.currentStage = currentStage;
        }

        public Timestamp getStartTime() {
            return startTime;
        }

        public void setStartTime(Timestamp startTime) {
            this.startTime = startTime;
        }

        public Timestamp getContractTime() {
            return contractTime;
        }

        public void setContractTime(Timestamp contractTime) {
            this.contractTime = contractTime;
        }

//        public LocationBO getLocationBO() {
//            return locationBO;
//        }

//        public void setLocationBO(LocationBO locationBO) {
//            this.locationBO = locationBO;
//        }
    }

    public static class LocationBO {
        private int type;
        private String address;
        private double longitude;
        private double latitude;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

    }
}

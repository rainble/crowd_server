package fudan.mcd.vo;

import fudan.mcd.servlet.ApplyTaskServlet;

import java.sql.Timestamp;

public class ApplyVO {

    private int userId;
    private int taskId;
    private int currentStage;
    private Timestamp startTime;
    private Timestamp contractTime;
    private ApplyTaskServlet.LocationBO locationBO;
    private int locationId;


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

    public ApplyTaskServlet.LocationBO getLocationBOList() {
        return locationBO;
    }

    public void setLocationBO(ApplyTaskServlet.LocationBO locationBO) {
        this.locationBO = locationBO;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getLocationId() {
        return locationId;
    }
}

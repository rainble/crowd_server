
package fudan.mcd.vo;

import java.sql.Timestamp;

public class SimpleTaskVO {

    private int taskId;
    private int userId;
    private String taskDesc;
    private String locationDesc;
    private int duration;
    private int bonus;
    private Timestamp publishTime;
    private String callbackUrl;

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getUserId() {
        return userId;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPublishTime(Timestamp publishTime) {
        this.publishTime = publishTime;
    }

    public int getBonus() {
        return bonus;
    }

    public int getDuration() {
        return duration;
    }

    public Timestamp getPublishTime() {
        return publishTime;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

}
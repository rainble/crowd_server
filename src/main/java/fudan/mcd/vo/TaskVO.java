package fudan.mcd.vo;

import java.sql.Timestamp;

public class TaskVO {
	public static final int STATUS_ONGOING = 0, STATUS_EXPIRED = -1, STATUS_FINISHED = 1, STATUS_CREDIT_GIVEN = 5;
	private int id;
	private int templateId;
	private int userId;
	private String title;
	private String description;
	private int status;
	private int currentStage;
	private double bonusReward;
	private Timestamp publishTime;
	private Timestamp deadline;
	private int userType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
	}

	public double getBonusReward() {
		return bonusReward;
	}

	public void setBonusReward(double bonusReward) {
		this.bonusReward = bonusReward;
	}

	public Timestamp getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}

	public Timestamp getDeadline() {
		return deadline;
	}

	public void setDeadline(Timestamp deadline) {
		this.deadline = deadline;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
}

package fudan.mcd.vo;

import java.sql.Timestamp;

public class TemplateVO {
	private int id;
	private int userId;
	private String name;
	private String description;
	private int heat;
	private String uri;
	private Timestamp createTime;
	private int totalStageNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHeat() {
		return heat;
	}

	public void setHeat(int heat) {
		this.heat = heat;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getTotalStageNum() {
		return totalStageNum;
	}

	public void setTotalStageNum(int totalStageNum) {
		this.totalStageNum = totalStageNum;
	}
}

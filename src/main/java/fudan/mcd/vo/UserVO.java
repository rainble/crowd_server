package fudan.mcd.vo;

public class UserVO {
	private int id;
	private String account;
	private String password;
	private double publishCredit;
	private double withdrawCredit;
	private String avatar;
	private int tag;
	private int loginFlag;
	private String weChatId;
	private String phoneNum;
	public static int USER_NORMAL = 0, USER_VIP = 1;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getPublishCredit() {
		return publishCredit;
	}

	public void setPublishCredit(double publishCredit) {
		this.publishCredit = publishCredit;
	}

	public double getWithdrawCredit() {
		return withdrawCredit;
	}

	public void setWithdrawCredit(double withdrawCredit) {
		this.withdrawCredit = withdrawCredit;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(int loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getWeChatId() {
		return weChatId;
	}

	public void setWeChatId(String weChatId) {
		this.weChatId = weChatId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
}

package fudan.mcd.runtime;

import fudan.mcd.servlet.AcceptTaskServlet;

import java.util.List;

public class AcceptStructure {
	private List<AcceptTaskServlet.RequestBO> requests;
	private int timeWindow;
	private boolean flag;//标志是否还在计时范围内
	
	public AcceptStructure(List<AcceptTaskServlet.RequestBO> requests,int timeWindow){
		this.requests = requests;
		this.timeWindow = timeWindow;
		this.flag = true;
	}

	public List<AcceptTaskServlet.RequestBO> getRequests() {
		return requests;
	}

	public void setRequests(List<AcceptTaskServlet.RequestBO> requests) {
		this.requests = requests;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}

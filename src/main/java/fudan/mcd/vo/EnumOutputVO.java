package fudan.mcd.vo;

public class EnumOutputVO extends OutputVO {
	private String entries;
	private int aggregationMethod;

	public String getEntries() {
		return entries;
	}

	public void setEntries(String entries) {
		this.entries = entries;
	}

	public int getAggregationMethod() {
		return aggregationMethod;
	}

	public void setAggregationMethod(int aggregationMethod) {
		this.aggregationMethod = aggregationMethod;
	}
}

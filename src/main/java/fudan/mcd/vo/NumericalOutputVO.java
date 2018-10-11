package fudan.mcd.vo;

public class NumericalOutputVO extends OutputVO {
	private int interval;
	private double upperBound;
	private double lowerBound;
	private int aggregationMethod;

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getAggregationMethod() {
		return aggregationMethod;
	}

	public void setAggregationMethod(int aggregationMethod) {
		this.aggregationMethod = aggregationMethod;
	}
}

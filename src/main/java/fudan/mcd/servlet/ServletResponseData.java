package fudan.mcd.servlet;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "result", "data" })
public class ServletResponseData {
	public static final int RESULT_PARSE_FAILED = -404;
	private int result;
	private String data;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}

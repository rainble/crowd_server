package fudan.mcd.service;

import javax.servlet.ServletContext;

public class AbstractService {
	protected ServletContext context;

	public AbstractService(ServletContext context) {
		this.context = context;
	}
}

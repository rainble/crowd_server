package fudan.mcd.dao.abs;

import fudan.mcd.vo.TextOutputVO;

import javax.servlet.ServletContext;

public abstract class AbstractTextOutputDAO extends AbstractOutputDAO<TextOutputVO> {
	public AbstractTextOutputDAO(ServletContext context) {
		super(context);
	}

	public AbstractTextOutputDAO(String configPath) {
		super(configPath);
	}
}

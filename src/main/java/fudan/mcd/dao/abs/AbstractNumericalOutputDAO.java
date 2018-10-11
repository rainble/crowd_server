package fudan.mcd.dao.abs;

import fudan.mcd.vo.NumericalOutputVO;

import javax.servlet.ServletContext;

public abstract class AbstractNumericalOutputDAO extends AbstractOutputDAO<NumericalOutputVO> {
	public AbstractNumericalOutputDAO(ServletContext context) {
		super(context);
	}

	public AbstractNumericalOutputDAO(String configPath) {
		super(configPath);
	}
}

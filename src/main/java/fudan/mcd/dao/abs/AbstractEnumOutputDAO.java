package fudan.mcd.dao.abs;

import fudan.mcd.vo.EnumOutputVO;

import javax.servlet.ServletContext;

public abstract class AbstractEnumOutputDAO extends AbstractOutputDAO<EnumOutputVO> {
	public AbstractEnumOutputDAO(ServletContext context) {
		super(context);
	}

	public AbstractEnumOutputDAO(String configPath) {
		super(configPath);
	}
}

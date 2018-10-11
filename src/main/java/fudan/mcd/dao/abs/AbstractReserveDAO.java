package fudan.mcd.dao.abs;

import fudan.mcd.vo.ReserveVO;

import javax.servlet.ServletContext;

public abstract class AbstractReserveDAO extends AbstractDAO<Integer, ReserveVO> {
	public AbstractReserveDAO(ServletContext context) {
		super(context);
	}

	public AbstractReserveDAO(String configPath) {
		super(configPath);
	}
}

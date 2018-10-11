package fudan.mcd.dao.abs;

import fudan.mcd.vo.ActionVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractActionDAO extends AbstractDAO<Integer, ActionVO> {
	public AbstractActionDAO(ServletContext context) {
		super(context);
	}

	public AbstractActionDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找位置对应的行为对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param locationId
	 *            位置ID
	 * @return 行为对象列表
	 */
	public abstract List<ActionVO> queryActionListByLocation(int locationId);
}

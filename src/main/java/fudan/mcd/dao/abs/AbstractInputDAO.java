package fudan.mcd.dao.abs;

import fudan.mcd.vo.InputVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractInputDAO extends AbstractDAO<Integer, InputVO> {
	public AbstractInputDAO(ServletContext context) {
		super(context);
	}

	public AbstractInputDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找行为对应的输入对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param actionId
	 *            行为ID
	 * @return 输入对象列表
	 */
	public abstract List<InputVO> queryInputListByAction(int actionId);
}

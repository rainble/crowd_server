package fudan.mcd.dao.abs;

import fudan.mcd.vo.OutputVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractOutputDAO<T extends OutputVO> extends AbstractDAO<Integer, T> {

	public AbstractOutputDAO(ServletContext context) {
		super(context);
	}

	public AbstractOutputDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找行为对应的输出对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param actionId
	 *            行为ID
	 * @return 输出对象列表
	 */
	public abstract List<T> queryOutputListByAction(int actionId);
	
	/**
	 * 查找行为对应的指定标记的输出对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param actionId 行为ID
	 * @param indicator 标记值
	 * @return 输出对象列表
	 */
	public abstract List<T> queryOutputListByActionAndIndicator(int actionId, int indicator);
	
	/**
	 * 查找行为和用户对应的指定标记的输出对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param actionId 行为ID
	 * @param userId 用户ID
	 * @param indicator 标记值
	 * @return 输出对象列表
	 */
	public abstract List<T> queryOutputListByActionAndUser(int actionId, int userId, int indicator);
}

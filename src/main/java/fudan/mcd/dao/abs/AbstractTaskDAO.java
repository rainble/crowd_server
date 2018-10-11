package fudan.mcd.dao.abs;

import fudan.mcd.vo.TaskVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractTaskDAO extends AbstractDAO<Integer, TaskVO> {

	public AbstractTaskDAO(ServletContext context) {
		super(context);
	}

	public AbstractTaskDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找用户发布的任务对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param userId
	 *            用户ID
	 * @return 任务对象列表
	 */
	public abstract List<TaskVO> queryTaskListByUser(int userId);

	/**
	 * 返回当前全部的任务对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @return 任务对象列表
	 */
	public abstract List<TaskVO> queryAllTaskList();
}

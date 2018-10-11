package fudan.mcd.dao.abs;

import fudan.mcd.vo.StageVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractStageDAO extends AbstractDAO<Integer, StageVO> {
	public AbstractStageDAO(ServletContext context) {
		super(context);
	}

	public AbstractStageDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找任务包含的阶段对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param taskId
	 *            任务ID
	 * @return 阶段对象列表
	 */
	public abstract List<StageVO> queryStageListByTask(int taskId);

	/**
	 * 查找任务中指定索引的的阶段对象。如果该对象不存在，返回null。
	 * 
	 * @param taskId
	 *            任务ID
	 * @param currentStageIndex
	 *            指定的阶段索引
	 * @return 阶段对象
	 */
	public abstract StageVO queryByTaskAndIndex(int taskId, int stageIndex);
}

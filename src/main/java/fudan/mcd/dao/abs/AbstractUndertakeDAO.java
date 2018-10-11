package fudan.mcd.dao.abs;

import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.UndertakeVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractUndertakeDAO extends AbstractDAO<Integer, UndertakeVO> {
	public AbstractUndertakeDAO(ServletContext context) {
		super(context);
	}

	public AbstractUndertakeDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找用户接受的具有指定状态的阶段对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param userId
	 *            用户ID
	 * @param status
	 *            状态
	 * @return 阶段对象列表
	 */
	public abstract List<StageVO> queryStageListByUserAndStatus(int userId, int status);

	/**
	 * 查找用户有关的指定阶段的接受对象。如果对象不存在，返回null。
	 * 
	 * @param userId
	 *            用户ID
	 * @param stageId
	 *            阶段ID
	 * @return 接受对象
	 */
	public abstract UndertakeVO queryByUserAndStage(int userId, int stageId);

	/**
	 * 查找所有指定阶段的接受对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param stageId
	 *            阶段ID
	 * @return 接受对象列表
	 */
	public abstract List<UndertakeVO> queryByStage(int stageId);
}

package fudan.mcd.dao.abs;

import fudan.mcd.vo.ApplicationVO;

import javax.servlet.ServletContext;

public abstract class AbstractApplicationDAO extends AbstractDAO<Integer, ApplicationVO> {
	public AbstractApplicationDAO(ServletContext context) {
		super(context);
	}

	public AbstractApplicationDAO(String configPath) {
		super(configPath);
	}
	
	/**
	 * 查询具有指定用户和阶段的应用对象。如果该对象不存在，则返回null。
	 * 
	 * @param userId 用户ID
	 * @param stageId 阶段ID
	 * @return 查询的应用对象
	 */
	public abstract ApplicationVO queryByUserAndStage(int userId, int stageId);
	
	/**
	 * 删除具有指定用户和阶段的应用对象。如果该对象不存在，则返回null。
	 * 
	 * @param userId 用户ID
	 * @param stageId 阶段ID
	 * @return 删除的应用对象
	 */
	public abstract ApplicationVO deleteByUserAndStage(int userId, int stageId);
}

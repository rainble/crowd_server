package fudan.mcd.dao.abs;

import fudan.mcd.vo.LocationVO;

import javax.servlet.ServletContext;

public abstract class AbstractLocationDAO extends AbstractDAO<Integer, LocationVO> {
	public AbstractLocationDAO(ServletContext context) {
		super(context);
	}

	public AbstractLocationDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 
	 * 查找阶段中指定类型的位置对象。如果该对象不存在，返回null。
	 * 
	 * @param stageId
	 *            阶段ID
	 * @param type
	 *            指定的类型
	 * @return 位置对象
	 */
	public abstract LocationVO queryByStageAndType(int stageId, int type);
}

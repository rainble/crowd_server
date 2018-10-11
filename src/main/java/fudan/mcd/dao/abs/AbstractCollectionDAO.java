package fudan.mcd.dao.abs;

import fudan.mcd.vo.CollectionVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractCollectionDAO extends AbstractDAO<Integer, CollectionVO> {
	public AbstractCollectionDAO(ServletContext context) {
		super(context);
	}

	public AbstractCollectionDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找用户收藏的模板对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param userId
	 *            用户ID
	 * @return 模板对象列表
	 */
	public abstract List<TemplateVO> queryTemplateListByUser(int userId);

	/**
	 * 查找收藏模板的用户对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param templateId
	 *            模板ID
	 * @return 用户对象列表
	 */
	public abstract List<UserVO> queryUserListByTemplate(int templateId);

	/**
	 * 删除具有指定模板和用户的收藏对象。如果该对象不存在，返回null。
	 * 
	 * @param userId
	 *            用户ID
	 * @param templateId
	 *            模板ID
	 * @return 收藏对象
	 */
	public abstract CollectionVO deleteByUserAndTemplate(int userId, int templateId);
}

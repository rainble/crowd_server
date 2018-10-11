package fudan.mcd.dao.abs;

import fudan.mcd.vo.TemplateVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractTemplateDAO extends AbstractDAO<Integer, TemplateVO> {

	public AbstractTemplateDAO(ServletContext context) {
		super(context);
	}

	public AbstractTemplateDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找用户创建的模板对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @param userId
	 *            用户ID
	 * @return 模板对象列表
	 */
	public abstract List<TemplateVO> queryTemplateListByUser(int userId);

	/**
	 * 返回当前全部的模板对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @return 模板对象列表
	 */
	public abstract List<TemplateVO> queryAllTemplateList();

	/**
	 * 返回名称中包含指定字符串的模板对象列表。如果结果为空集，返回空的列表。
	 * 
	 * @return 模板对象列表
	 */
	public abstract List<TemplateVO> queryTemplateListContainingName(String name);
}

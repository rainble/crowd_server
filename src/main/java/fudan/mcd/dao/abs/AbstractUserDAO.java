package fudan.mcd.dao.abs;

import fudan.mcd.vo.UserVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractUserDAO extends AbstractDAO<Integer, UserVO> {
	public AbstractUserDAO(ServletContext context) {
		super(context);
	}

	public AbstractUserDAO(String configPath) {
		super(configPath);
	}

	/**
	 * 查找账号对应的用户对象。如果该对象不存在，返回null。
	 * 
	 * @param account
	 *            账号
	 * @return 用户对象
	 */
	public abstract UserVO queryByAccount(String account);
	
	/**
	 * 查询所有用户列表
	 */
	public abstract List<UserVO> queryAllUser();
}

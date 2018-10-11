package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractOutputDAO;
import fudan.mcd.dao.abs.DAOMethodNotSupportedException;
import fudan.mcd.vo.*;

import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class OutputDAO extends AbstractOutputDAO<OutputVO> {
	public static final String FIELD_ID = "outputId";
	public static final String FIELD_ACTION_ID = "actionId";
	public static final String FIELD_VALUE = "outputValue";
	public static final String FIELD_DESCRIPTION = "outputDesc";
	private static final Class<AbstractOutputDAO<OutputVO>>[] DAO_CLASS_ARRAY = new Class[] { EnumOutputDAO.class, PictureOutputDAO.class,
			TextOutputDAO.class, NumericalOutputDAO.class };
	private static final Class<OutputVO>[] VO_CLASS_ARRAY = new Class[] { EnumOutputVO.class, PictureOutputVO.class, TextOutputVO.class,
			NumericalOutputVO.class };
	private List<AbstractOutputDAO<OutputVO>> daoList;

	public OutputDAO(ServletContext context) {
		super(context);
		daoList = new ArrayList<AbstractOutputDAO<OutputVO>>();
		try {
			for (int i = 0; i < DAO_CLASS_ARRAY.length; i++) {
				Class<? extends AbstractOutputDAO<OutputVO>> clazz = DAO_CLASS_ARRAY[i];
				Constructor<? extends AbstractOutputDAO<OutputVO>> constructor = clazz.getConstructor(ServletContext.class);
				AbstractOutputDAO<OutputVO> dao = constructor.newInstance(context);
				daoList.add(dao);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OutputDAO(String configPath) {
		super(configPath);
		daoList = new ArrayList<AbstractOutputDAO<OutputVO>>();
		try {
			for (int i = 0; i < DAO_CLASS_ARRAY.length; i++) {
				Class<AbstractOutputDAO<OutputVO>> clazz = DAO_CLASS_ARRAY[i];
				Constructor<AbstractOutputDAO<OutputVO>> constructor = clazz.getConstructor(String.class);
				AbstractOutputDAO<OutputVO> dao = constructor.newInstance(configPath);
				daoList.add(dao);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Integer insert(OutputVO vo) {
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			if (vo.getClass().equals(VO_CLASS_ARRAY[i])) {
				AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
				return DAO_CLASS_ARRAY[i].cast(dao).insert(vo);
			}
		}
		return -1;
	}

	/**
	 * 由于参数无法指明删除的具体类型，请勿直接调用此方法。如果需要进行删除操作，请调用delete(Integer pk, Class<?> clazz)的重载方法。
	 * 
	 * @param pk
	 *            删除的值对象的主键
	 * @throws DAOMethodNotSupportedException
	 *             由于无法支持查询操作，此方法总是会抛出该异常
	 * @return 如果删除成功，返回删除的值对象。如果删除失败，返回null。
	 */
	@Override
	@Deprecated
	public OutputVO delete(Integer pk) {
		throw new DAOMethodNotSupportedException();
	}

	/**
	 * 删除主键对应的输出对象。
	 * 
	 * @param pk
	 *            删除的输出对象的主键
	 * @param clazz
	 *            删除的输出对象的类型
	 * @return 如果删除成功，返回删除的值对象。如果删除失败，返回null。
	 */
	public OutputVO delete(Integer pk, Class<?> clazz) {
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			if (clazz.equals(VO_CLASS_ARRAY[i])) {
				AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
				return DAO_CLASS_ARRAY[i].cast(dao).delete(pk);
			}
		}
		return null;
	}

	@Override
	public int update(OutputVO vo) {
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			if (vo.getClass().equals(VO_CLASS_ARRAY[i])) {
				AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
				return DAO_CLASS_ARRAY[i].cast(dao).update(vo);
			}
		}
		return -1;
	}

	/**
	 * 由于参数无法指明查找的具体类型，请勿直接调用此方法。如果需要进行查询操作，请调用query(Integer pk, Class<?> clazz)的重载方法。
	 * 
	 * @param pk
	 *            查找的值对象的主键
	 * @throws DAOMethodNotSupportedException
	 *             由于无法支持查询操作，此方法总是会抛出该异常
	 * @return 如果主键对应的值对象存在数据表中，返回该值对象；否则，返回null。
	 */
	@Override
	@Deprecated
	public OutputVO query(Integer pk) {
		throw new DAOMethodNotSupportedException();
	}

	/**
	 * 查找主键对应的输出对象。
	 * 
	 * @param pk
	 *            查找的输出对象的主键
	 * @param clazz
	 *            查找的输出对象的类型
	 * @return 如果主键对应的值对象存在数据表中，返回该值对象；否则，返回null。
	 */
	public OutputVO query(Integer pk, Class<?> clazz) {
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			if (clazz.equals(VO_CLASS_ARRAY[i])) {
				AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
				return DAO_CLASS_ARRAY[i].cast(dao).query(pk);
			}
		}
		return null;
	}

	@Override
	public List<OutputVO> queryOutputListByAction(int actionId) {
		List<OutputVO> list = new ArrayList<OutputVO>();
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
			List<OutputVO> tempList = DAO_CLASS_ARRAY[i].cast(dao).queryOutputListByAction(actionId);
			for (int j = 0; j < tempList.size(); j++)
				list.add(tempList.get(j));
		}
		return list;
	}

	@Override
	public List<OutputVO> queryOutputListByActionAndIndicator(int actionId, int indicator) {
		List<OutputVO> list = new ArrayList<OutputVO>();
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
			List<OutputVO> tempList = DAO_CLASS_ARRAY[i].cast(dao).queryOutputListByActionAndIndicator(actionId, indicator);
			for (int j = 0; j < tempList.size(); j++)
				list.add(tempList.get(j));
		}
		return list;
	}

	@Override
	public List<OutputVO> queryOutputListByActionAndUser(int actionId, int userId, int indicator) {
		List<OutputVO> list = new ArrayList<OutputVO>();
		for (int i = 0; i < VO_CLASS_ARRAY.length; i++) {
			AbstractOutputDAO<? extends OutputVO> dao = daoList.get(i);
			List<OutputVO> tempList = DAO_CLASS_ARRAY[i].cast(dao).queryOutputListByActionAndUser(actionId, userId, indicator);
			for (int j = 0; j < tempList.size(); j++)
				list.add(tempList.get(j));
		}
		return list;
	}
}

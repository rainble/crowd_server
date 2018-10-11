package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.vo.CollectionVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestCollectionDAO {
	private int userId;
	private int templateId;
	private CollectionDAO collectionDAO;
	private CollectionVO collectionVO;

	@Before
	public void init() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		collectionDAO = new CollectionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		int collectionPK = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK > 0);

		CollectionVO resultCollectionVO = collectionDAO.delete(collectionPK);
		assertNotNull(resultCollectionVO);
		assertEquals(collectionPK, resultCollectionVO.getId());
		assertEquals(collectionVO.getUserId(), resultCollectionVO.getUserId());
		assertEquals(collectionVO.getTemplateId(), resultCollectionVO.getTemplateId());
	}

	@Test
	public void testInsertDataWithPK() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		int collectionPK1 = -1;
		collectionPK1 = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK1 > 0);
		collectionVO.setId(collectionPK1);
		int collectionPK2 = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK2 > 0);
		assertNotEquals(collectionPK1, collectionPK2);
		collectionDAO.delete(collectionPK1);
		collectionDAO.delete(collectionPK2);
	}

	@Test
	public void testInvalidDelete() {
		int collectionPK = JUnitTestUtils.INVALID_PK;
		assertNull(collectionDAO.query(collectionPK));
		collectionVO = collectionDAO.delete(collectionPK);
		assertNull(collectionVO);
	}

	@Test
	public void testUpdate() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		int collectionPK = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK > 0);

		CollectionVO newCollectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		newCollectionVO.setId(collectionPK);
		int result = collectionDAO.update(newCollectionVO);
		assertTrue(result > 0);
		CollectionVO resultCollectionVO = collectionDAO.query(collectionPK);
		assertNotNull(resultCollectionVO);
		assertEquals(newCollectionVO.getUserId(), resultCollectionVO.getUserId());
		assertEquals(newCollectionVO.getTemplateId(), resultCollectionVO.getTemplateId());
		collectionDAO.delete(collectionPK);
	}

	@Test
	public void testInvalidUpdate() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		assertNull(collectionDAO.query(JUnitTestUtils.INVALID_PK));

		int result = collectionDAO.update(collectionVO);
		assertTrue(result < 0);
		assertNull(collectionDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		int collectionPK = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK > 0);

		CollectionVO resultCollectionVO = collectionDAO.query(collectionPK);
		assertNotNull(resultCollectionVO);
		assertEquals(collectionVO.getUserId(), resultCollectionVO.getUserId());
		assertEquals(collectionVO.getTemplateId(), resultCollectionVO.getTemplateId());
		collectionDAO.delete(collectionPK);
	}

	@Test
	public void testInvalidQuery() {
		int collectionPK = JUnitTestUtils.INVALID_PK;
		collectionVO = collectionDAO.query(collectionPK);
		assertNull(collectionVO);
	}

	@Test
	public void testQueryTemplateListByUser() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO2 = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO2);
		assertTrue(userPK2 > 0);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);
		int collectionPK2 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templateId));
		int collectionPK3 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templatePK2));
		int collectionPK4 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userPK2, templateId));

		List<TemplateVO> templateVOList = collectionDAO.queryTemplateListByUser(userId);
		assertEquals(2, templateVOList.size());
		List<Integer> templatePKList = new ArrayList<Integer>();
		for (TemplateVO vo : templateVOList)
			templatePKList.add(vo.getId());
		assertTrue(templatePKList.contains(templateId));
		assertTrue(templatePKList.contains(templatePK2));

		for (TemplateVO resultTemplateVO : templateVOList) {
			if (resultTemplateVO.getId() == templatePK2) {
				assertEquals(templateVO.getUserId(), resultTemplateVO.getUserId());
				assertEquals(templateVO.getName(), resultTemplateVO.getName());
				assertEquals(templateVO.getUri(), resultTemplateVO.getUri());
				JUnitTestUtils.assertTimestampEquals(templateVO.getCreateTime(), resultTemplateVO.getCreateTime());
				assertEquals(templateVO.getTotalStageNum(), resultTemplateVO.getTotalStageNum());
			}
		}

		collectionDAO.delete(collectionPK2);
		collectionDAO.delete(collectionPK3);
		collectionDAO.delete(collectionPK4);
		templateDAO.delete(templatePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testInvalidQueryTemplateListByUser() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO);
		assertTrue(userPK2 > 0);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);
		int collectionPK2 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templateId));
		int collectionPK3 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templatePK2));
		int collectionPK4 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userPK2, templateId));

		List<TemplateVO> templateVOList = collectionDAO.queryTemplateListByUser(JUnitTestUtils.INVALID_PK);
		assertEquals(0, templateVOList.size());

		collectionDAO.delete(collectionPK2);
		collectionDAO.delete(collectionPK3);
		collectionDAO.delete(collectionPK4);
		templateDAO.delete(templatePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testQueryUserListByTemplate() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO);
		assertTrue(userPK2 > 0);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);
		int collectionPK2 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templateId));
		int collectionPK3 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templatePK2));
		int collectionPK4 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userPK2, templateId));

		List<UserVO> userVOList = collectionDAO.queryUserListByTemplate(templateId);
		assertEquals(2, userVOList.size());
		List<Integer> userPKList = new ArrayList<Integer>();
		for (UserVO vo : userVOList)
			userPKList.add(vo.getId());
		assertTrue(userPKList.contains(userId));
		assertTrue(userPKList.contains(userPK2));

		for (UserVO resultUserVO : userVOList) {
			if (resultUserVO.getId() == userPK2) {
				assertEquals(userVO.getAccount(), resultUserVO.getAccount());
				assertEquals(userVO.getPassword(), resultUserVO.getPassword());
				assertEquals(userVO.getPublishCredit(), resultUserVO.getPublishCredit(), JUnitTestUtils.FLOAT_ERROR);
			}
		}

		collectionDAO.delete(collectionPK2);
		collectionDAO.delete(collectionPK3);
		collectionDAO.delete(collectionPK4);
		templateDAO.delete(templatePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testInvalidQueryUserListByTemplate() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO);
		assertTrue(userPK2 > 0);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);
		int collectionPK2 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templateId));
		int collectionPK3 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userId, templatePK2));
		int collectionPK4 = collectionDAO.insert(JUnitTestUtils.generateCollectionVO(userPK2, templateId));

		List<UserVO> userVOList = collectionDAO.queryUserListByTemplate(JUnitTestUtils.INVALID_PK);
		assertEquals(0, userVOList.size());

		collectionDAO.delete(collectionPK2);
		collectionDAO.delete(collectionPK3);
		collectionDAO.delete(collectionPK4);
		templateDAO.delete(templatePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testDeleteByUserAndTemplate() {
		collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		int collectionPK = collectionDAO.insert(collectionVO);
		assertTrue(collectionPK > 0);

		CollectionVO resultCollectionVO = collectionDAO.deleteByUserAndTemplate(userId, templateId);
		assertNotNull(resultCollectionVO);
		assertEquals(collectionPK, resultCollectionVO.getId());
		assertEquals(collectionVO.getUserId(), resultCollectionVO.getUserId());
		assertEquals(collectionVO.getTemplateId(), resultCollectionVO.getTemplateId());

		assertNull(collectionDAO.deleteByUserAndTemplate(userId, templateId));
		assertNull(collectionDAO.deleteByUserAndTemplate(JUnitTestUtils.INVALID_PK, templateId));
		assertNull(collectionDAO.deleteByUserAndTemplate(userId, JUnitTestUtils.INVALID_PK));
		assertNull(collectionDAO.deleteByUserAndTemplate(JUnitTestUtils.INVALID_PK, JUnitTestUtils.INVALID_PK));
	}

	@After
	public void recycle() {
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateDAO.delete(templateId);
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
	}

}

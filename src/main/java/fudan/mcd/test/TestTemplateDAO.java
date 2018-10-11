package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTemplateDAO {
	private int userId;
	private TemplateDAO templateDAO;
	private TemplateVO templateVO;

	@Before
	public void init() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateVO = new TemplateVO();
	}

	@Test
	public void testInsertAndDelete() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK = templateDAO.insert(templateVO);
		assertTrue(templatePK > 0);

		TemplateVO resultTemplateVO = templateDAO.delete(templatePK);
		assertNotNull(resultTemplateVO);
		assertEquals(templatePK, resultTemplateVO.getId());
		assertEquals(templateVO.getUserId(), resultTemplateVO.getUserId());
		assertEquals(templateVO.getName(), resultTemplateVO.getName());
		assertEquals(templateVO.getUri(), resultTemplateVO.getUri());
		JUnitTestUtils.assertTimestampEquals(templateVO.getCreateTime(), resultTemplateVO.getCreateTime());
		assertEquals(templateVO.getTotalStageNum(), resultTemplateVO.getTotalStageNum());
		assertEquals(templateVO.getHeat(), resultTemplateVO.getHeat());
		assertEquals(templateVO.getDescription(), resultTemplateVO.getDescription());
	}

	@Test
	public void testInsertDataWithPK() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		templateVO.setId(templatePK1);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);
		assertNotEquals(templatePK1, templatePK2);
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testInvalidDelete() {
		int templatePK = JUnitTestUtils.INVALID_PK;
		assertNull(templateDAO.query(templatePK));
		templateVO = templateDAO.delete(templatePK);
		assertNull(templateVO);
	}

	@Test
	public void testUpdate() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK = templateDAO.insert(templateVO);
		assertTrue(templatePK > 0);

		TemplateVO newtemplateVO = JUnitTestUtils.generateTemplateVO(userId);
		newtemplateVO.setId(templatePK);
		int result = templateDAO.update(newtemplateVO);
		assertTrue(result > 0);
		TemplateVO resultTemplateVO = templateDAO.query(templatePK);
		assertNotNull(resultTemplateVO);
		assertEquals(newtemplateVO.getUserId(), resultTemplateVO.getUserId());
		assertEquals(newtemplateVO.getName(), resultTemplateVO.getName());
		assertEquals(newtemplateVO.getUri(), resultTemplateVO.getUri());
		JUnitTestUtils.assertTimestampEquals(newtemplateVO.getCreateTime(), resultTemplateVO.getCreateTime());
		assertEquals(newtemplateVO.getTotalStageNum(), resultTemplateVO.getTotalStageNum());
		assertEquals(newtemplateVO.getHeat(), resultTemplateVO.getHeat());
		assertEquals(newtemplateVO.getDescription(), resultTemplateVO.getDescription());
		templateDAO.delete(templatePK);
	}

	@Test
	public void testInvalidUpdate() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		assertNull(templateDAO.query(JUnitTestUtils.INVALID_PK));

		int result = templateDAO.update(templateVO);
		assertTrue(result < 0);
		assertNull(templateDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK = templateDAO.insert(templateVO);
		assertTrue(templatePK > 0);

		TemplateVO resultTemplateVO = templateDAO.query(templatePK);
		assertNotNull(resultTemplateVO);
		assertEquals(templateVO.getUserId(), resultTemplateVO.getUserId());
		assertEquals(templateVO.getName(), resultTemplateVO.getName());
		assertEquals(templateVO.getUri(), resultTemplateVO.getUri());
		JUnitTestUtils.assertTimestampEquals(templateVO.getCreateTime(), resultTemplateVO.getCreateTime());
		assertEquals(templateVO.getTotalStageNum(), resultTemplateVO.getTotalStageNum());
		assertEquals(templateVO.getHeat(), resultTemplateVO.getHeat());
		assertEquals(templateVO.getDescription(), resultTemplateVO.getDescription());
		templateDAO.delete(templatePK);
	}

	@Test
	public void testInvalidQuery() {
		int templatePK = JUnitTestUtils.INVALID_PK;
		templateVO = templateDAO.query(templatePK);
		assertNull(templateVO);
	}

	@Test
	public void testQueryByUser() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);

		List<TemplateVO> resultTemplateVOList = templateDAO.queryTemplateListByUser(userId);
		assertEquals(2, resultTemplateVOList.size());
		List<Integer> resultTemplatePKList = new ArrayList<Integer>();
		for (TemplateVO vo : resultTemplateVOList)
			resultTemplatePKList.add(vo.getId());
		assertTrue(resultTemplatePKList.contains(templatePK1));
		assertTrue(resultTemplatePKList.contains(templatePK2));
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testInvalidQueryByUser() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);

		List<TemplateVO> resultTemplateVOList = templateDAO.queryTemplateListByUser(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultTemplateVOList.size());
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testQueryAll() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);

		List<TemplateVO> resultTemplateVOList = templateDAO.queryAllTemplateList();
		assertTrue(resultTemplateVOList.size() >= 2);
		List<Integer> resultTemplatePKList = new ArrayList<Integer>();
		for (TemplateVO vo : resultTemplateVOList)
			resultTemplatePKList.add(vo.getId());
		assertTrue(resultTemplatePKList.contains(templatePK1));
		assertTrue(resultTemplatePKList.contains(templatePK2));
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testQueryTemplateListContainingName() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateVO.setName("name_containing");
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);

		List<TemplateVO> resultTemplateVOList = templateDAO.queryTemplateListContainingName("containing");
		assertEquals(2, resultTemplateVOList.size());
		List<Integer> resultTemplatePKList = new ArrayList<Integer>();
		for (TemplateVO vo : resultTemplateVOList)
			resultTemplatePKList.add(vo.getId());
		assertTrue(resultTemplatePKList.contains(templatePK1));
		assertTrue(resultTemplatePKList.contains(templatePK2));
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testInvaidQueryTemplateListContainingName() {
		templateVO = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK1 = templateDAO.insert(templateVO);
		assertTrue(templatePK1 > 0);
		int templatePK2 = templateDAO.insert(templateVO);
		assertTrue(templatePK2 > 0);

		List<TemplateVO> resultTemplateVOList = templateDAO.queryTemplateListContainingName(JUnitTestUtils.INVALID_FIELD_STRING);
		assertEquals(0, resultTemplateVOList.size());
		templateDAO.delete(templatePK1);
		templateDAO.delete(templatePK2);
	}

	@After
	public void recycle() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
	}
}

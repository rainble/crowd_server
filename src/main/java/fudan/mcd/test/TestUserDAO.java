package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.vo.UserVO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUserDAO {
	private UserDAO dao;
	private UserVO vo;

	@Before
	public void init() {
		dao = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		vo = new UserVO();
	}

	@Test
	public void testInsertAndDelete() {
		vo = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(vo);
		assertTrue(pk > 0);

		UserVO resultVO = dao.delete(pk);
		assertNotNull(vo);
		assertEquals(pk, resultVO.getId());
		assertEquals(vo.getAccount(), resultVO.getAccount());
		assertEquals(vo.getPassword(), resultVO.getPassword());
		assertEquals(vo.getPublishCredit(), resultVO.getPublishCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getWithdrawCredit(), resultVO.getWithdrawCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getAvatar(), resultVO.getAvatar());
		assertEquals(vo.getTag(), resultVO.getTag());
		assertEquals(vo.getLoginFlag(), resultVO.getLoginFlag());
		assertEquals(vo.getWeChatId(), resultVO.getWeChatId());
		assertEquals(vo.getPhoneNum(), resultVO.getPhoneNum());
	}

	@Test
	public void testInsertDataWithPK() {
		vo = JUnitTestUtils.generateUserVO();
		int pk1 = dao.insert(vo);
		assertTrue(pk1 > 0);
		vo.setId(pk1);
		int pk2 = dao.insert(vo);
		assertTrue(pk2 > 0);
		assertNotEquals(pk1, pk2);
		dao.delete(pk1);
		dao.delete(pk2);
	}

	@Test
	public void testInvalidDelete() {
		int pk = JUnitTestUtils.INVALID_PK;
		assertNull(dao.query(pk));
		vo = dao.delete(pk);
		assertNull(vo);
	}

	@Test
	public void testUpdate() {
		vo = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(vo);
		assertTrue(pk > 0);

		UserVO newVO = JUnitTestUtils.generateUserVO();
		newVO.setId(pk);
		int result = dao.update(newVO);
		assertTrue(result > 0);
		UserVO resultVO = dao.query(pk);
		assertNotNull(resultVO);
		assertEquals(newVO.getAccount(), resultVO.getAccount());
		assertEquals(newVO.getPassword(), resultVO.getPassword());
		assertEquals(newVO.getPublishCredit(), resultVO.getPublishCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newVO.getWithdrawCredit(), resultVO.getWithdrawCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newVO.getAvatar(), resultVO.getAvatar());
		assertEquals(newVO.getTag(), resultVO.getTag());
		assertEquals(newVO.getLoginFlag(), resultVO.getLoginFlag());
		assertEquals(newVO.getWeChatId(), resultVO.getWeChatId());
		assertEquals(newVO.getPhoneNum(), resultVO.getPhoneNum());
		dao.delete(pk);
	}

	@Test
	public void testInvalidUpdate() {
		vo = JUnitTestUtils.generateUserVO();
		assertNull(dao.query(JUnitTestUtils.INVALID_PK));

		int result = dao.update(vo);
		assertTrue(result < 0);
		assertNull(dao.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		vo = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(vo);
		assertTrue(pk > 0);

		UserVO resultVO = dao.query(pk);
		assertNotNull(resultVO);
		assertEquals(vo.getAccount(), resultVO.getAccount());
		assertEquals(vo.getPassword(), resultVO.getPassword());
		assertEquals(vo.getPublishCredit(), resultVO.getPublishCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getWithdrawCredit(), resultVO.getWithdrawCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getAvatar(), resultVO.getAvatar());
		assertEquals(vo.getTag(), resultVO.getTag());
		assertEquals(vo.getLoginFlag(), resultVO.getLoginFlag());
		assertEquals(vo.getWeChatId(), resultVO.getWeChatId());
		assertEquals(vo.getPhoneNum(), resultVO.getPhoneNum());
		dao.delete(pk);
	}

	@Test
	public void testInvalidQuery() {
		int pk = JUnitTestUtils.INVALID_PK;
		vo = dao.query(pk);
		assertNull(vo);
	}

	@Test
	public void testQueryByAccount() {
		vo = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(vo);
		assertTrue(pk > 0);

		UserVO resultVO = dao.queryByAccount(vo.getAccount());
		assertNotNull(resultVO);
		assertEquals(pk, resultVO.getId());
		assertEquals(vo.getPassword(), resultVO.getPassword());
		assertEquals(vo.getPublishCredit(), resultVO.getPublishCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getWithdrawCredit(), resultVO.getWithdrawCredit(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(vo.getAvatar(), resultVO.getAvatar());
		assertEquals(vo.getTag(), resultVO.getTag());
		assertEquals(vo.getLoginFlag(), resultVO.getLoginFlag());
		assertEquals(vo.getWeChatId(), resultVO.getWeChatId());
		assertEquals(vo.getPhoneNum(), resultVO.getPhoneNum());
		dao.delete(pk);
	}

	@Test
	public void testInvalidQueryByAccount() {
		String inValidAccount = JUnitTestUtils.generateTestString("invalid_account");
		vo = dao.queryByAccount(inValidAccount);
		assertNull(vo);
	}
}

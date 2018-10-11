package fudan.mcd.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestUserDAO.class, TestTemplateDAO.class, TestTaskDAO.class, TestStageDAO.class, TestLocationDAO.class, TestActionDAO.class,
		TestInputDAO.class, TestCollectionDAO.class, TestUndertakeDAO.class, TestEnumOutputDAO.class, TestPictureOutputDAO.class,
		TestTextOutputDAO.class, TestNumericalOutputDAO.class, TestReserveDAO.class, TestApplicationDAO.class })
public class TestAllDAO {
}
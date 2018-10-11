package fudan.mcd.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestLoginServlet.class, TestRegisterServlet.class, TestGetDesignedTemplateServlet.class,
		TestGetTemplateCollectionServlet.class, TestGetAllTemplateServlet.class, TestGetAcceptedTaskServlet.class, TestGetCompletedTaskServlet.class,
		TestGetPublishedTaskServlet.class, TestGetRecommendedTaskServlet.class, TestPublishTaskServlet.class, TestSearchTemplateServlet.class,
		TestAcceptTaskServlet.class, TestSaveTemplateServlet.class, TestCollectTemplateServlet.class, TestGetTaskInfoServlet.class,
		TestGetStageInfoServlet.class, TestCompleteTaskServlet.class })
public class TestAllServlet {
}
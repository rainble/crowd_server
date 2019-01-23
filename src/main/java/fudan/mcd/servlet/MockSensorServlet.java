package fudan.mcd.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/MockSensorServlet")
public class MockSensorServlet extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(GetSimpleTaskServlet.class);

    public MockSensorServlet() {
        super();
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info(String.format("MockSensor is called at [ %s ]", ServletUtils.getTime() ));
        response.getWriter().println(1);
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

}

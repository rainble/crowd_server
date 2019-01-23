package fudan.mcd.servlet;

import fudan.mcd.service.AcceptTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

@WebServlet("/GetImg")
public class GetImgServlet extends HttpServlet {
    public GetImgServlet() {
        super();
    }
    private static final Log LOG = LogFactory.getLog(GetImgServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String base64str = new String(ImgToByte("/Users/sunruoyu/Desktop/test.png"));
        String str = new String("\"data:image/jpg;base64,"+base64str+"\"");
        LOG.info(String.format("img success"));
        LOG.info(String.format("image byte is %s", str));
        response.getWriter().write(str);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public String ImgToByte(String path) {
        byte[] FileByte = null;
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            FileByte = new byte[in.available()];
            in.read(FileByte);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(FileByte);
    }

}

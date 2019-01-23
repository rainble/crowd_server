package fudan.mcd.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLOutput;

public class TestImgConvert {
    public static void main(String[] args) {
        try {
            File file = new File("/Users/sunruoyu/Desktop/test.png");
            byte[] fileByte = Files.readAllBytes(file.toPath());
            System.out.println(fileByte);
            System.out.println("Success");
            ByteToFile(fileByte);
            System.out.println("convert success");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ByteToFile(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        try {
            File file = new File("/Users/sunruoyu/Desktop/out.jpg");
            ImageIO.write(bufferedImage, ".jpg", file);
            System.out.println("convert successfully");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            byteArrayInputStream.close();
        }
    }


}

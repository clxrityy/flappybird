import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;


public class ImageLoader {
    public BufferedImage loadImage(String path) {
        try {
            // InputStream is = getClass().getResourceAsStream(path);
            return ImageIO.read(new FileInputStream(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}

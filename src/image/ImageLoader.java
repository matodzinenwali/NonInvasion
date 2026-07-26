package image;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;

public class ImageLoader {
    public Image loadImage(File file) throws Exception {
        return new Image(new FileInputStream(file));
    }
}
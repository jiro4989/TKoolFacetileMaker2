package com.jiro4989.tkfm.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public class ImageUtil {
  public static void writeFile(Image image, File file) throws IOException {
    BufferedImage img = SwingFXUtils.fromFXImage(image, null);
    ImageIO.write(img, "png", file);
  }
}

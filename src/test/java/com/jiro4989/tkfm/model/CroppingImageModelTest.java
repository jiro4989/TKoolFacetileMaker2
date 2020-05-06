package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.io.File;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class CroppingImageModelTest {
  @Test
  public void testCroppedImageWidthAndHeightEqualsRectangle() throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(0, 0);
    var rect = new Rectangle(20, 20);
    var scale = 100.0;
    var c = new CroppingImageModel(img, pos, rect, scale);
    var got = c.crop();

    assertEquals(got.getWidth(), 20.0);
    assertEquals(got.getHeight(), 20.0);
  }
}

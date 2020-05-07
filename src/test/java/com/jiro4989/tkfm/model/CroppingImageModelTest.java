package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.io.File;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
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

  @ParameterizedTest
  @CsvSource({
    "0,10",
    "5,5",
    "10,0",
    "20,0",
  })
  public void testMoveUpPosition(int moveWidth, int expect) throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(10, 10);
    var rect = new Rectangle(20, 20);
    var scale = 100.0;
    var c = new CroppingImageModel(img, pos, rect, scale);
    c.moveUp(moveWidth);

    assertEquals(pos.getX(), 10);
    assertEquals(pos.getY(), expect);
  }
}

package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.io.File;
import javafx.scene.image.Image;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class CroppingImageModelTest {
  @ParameterizedTest
  @CsvSource({
    "50.0, 40.0, 40.0",
    "100.0, 20.0, 20.0",
  })
  public void testCroppedImageWidthAndHeightEqualsRectangle(
      double scale, double wantWidth, double wantHeight) throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(0, 0);
    var rect = new Rectangle(20, 20);
    var c = new CroppingImageModel(img, pos, rect, scale);
    var got = c.crop();

    assertEquals(wantWidth, got.getWidth());
    assertEquals(wantHeight, got.getHeight());
  }

  @ParameterizedTest
  @CsvSource({
    "up    , 0   , 10  , 10",
    "up    , 5   , 10  , 5",
    "up    , 10  , 10  , 0",
    "up    , 20  , 10  , 0",
    "right , 0   , 10  , 10",
    "right , 5   , 15  , 10",
    "right , 10  , 20  , 10",
    "right , 200 , 130 , 10",
    "down  , 0   , 10  , 10",
    "down  , 5   , 10  , 15",
    "down  , 10  , 10  , 20",
    "down  , 200 , 10  , 36",
    "left  , 0   , 10  , 10",
    "left  , 5   , 5   , 10",
    "left  , 10  , 0   , 10",
    "left  , 20  , 0   , 10",
  })
  public void testMovePosition(String act, int moveWidth, int wantX, int wantY) throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(10, 10);
    var rect = new Rectangle(20, 30);
    var scale = 100.0;
    var c = new CroppingImageModel(img, pos, rect, scale);

    switch (act) {
      case "up":
        c.moveUp(moveWidth);
        break;
      case "right":
        c.moveRight(moveWidth);
        break;
      case "down":
        c.moveDown(moveWidth);
        break;
      case "left":
        c.moveLeft(moveWidth);
        break;
    }

    assertEquals(wantX, pos.getX());
    assertEquals(wantY, pos.getY());
  }

  @ParameterizedTest
  @CsvSource({
    "0, 0, 0, 0",
    "40, 40, 30, 20",
    "1000, 1000, 130, 26",
  })
  public void testMoveByMouse(int x, int y, int wantX, int wantY) throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(20, 30);
    var rect = new Rectangle(20, 40);
    var scale = 100.0;
    var c = new CroppingImageModel(img, pos, rect, scale);
    c.moveByMouse(x, y);
    assertEquals(wantX, pos.getX());
    assertEquals(wantY, pos.getY());
  }

  @ParameterizedTest
  @CsvSource({
    "up, 0, 100.0",
    "up, 5, 105.0",
    "up, 1000, 200.0",
    "down, 0, 100.0",
    "down, 5, 95.0",
    "down, 1000, 50.0",
  })
  public void testScaling(String act, double n, double wantScale) throws Exception {
    var path = getClass().getResource("/sample1.png").getPath();
    var file = new File(path);
    var img = new Image(file.toURI().toString());
    var pos = new Position(10, 10);
    var rect = new Rectangle(20, 30);
    var scale = 100.0;
    var c = new CroppingImageModel(img, pos, rect, scale);

    switch (act) {
      case "up":
        c.scaleUp(n);
        break;
      case "down":
        c.scaleDown(n);
        break;
    }

    assertEquals(wantScale, c.scaleProperty().get());
  }
}

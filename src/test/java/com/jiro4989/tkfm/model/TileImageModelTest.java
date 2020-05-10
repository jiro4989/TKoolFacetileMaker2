package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jiro4989.tkfm.data.Rectangle;
import java.lang.reflect.*;
import java.util.*;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class TileImageModelTest {
  @Test
  public void testConstructor() throws Exception {
    var t = new TileImageModel(new Rectangle(20, 20));
    assertEquals(2, t.__images.size());
    assertEquals(4, t.__images.get(0).size());
    assertEquals(4, t.__images.get(1).size());
  }

  @ParameterizedTest
  @CsvSource({
    "0,-65536,-65536,-65536",
    "1,0,-65536,-65536",
    "2,0,-65536,-65536",
    "3,0,-65536,-65536",
    "4,0,-65536,-65536",
    "5,0,-65536,-65536",
    "6,0,-65536,-65536",
    "7,0,-65536,-65536",
    "8,0,0,0",
  })
  public void testBulkInsert(int index, int want1, int want2, int want3) throws Exception {
    var t = new TileImageModel(new Rectangle(20, 20));
    var images = new LinkedList<Image>();
    for (int i = 0; i < 8; i++) images.add(new Image("20x20.png"));

    switch (index) {
      case 0:
        t.bulkInsert(images);
        break;
      default:
        t.bulkInsert(images, index);
    }

    final var colCount = 4;
    var x = index % colCount;
    var y = index / colCount;

    var reader = t.__images.get(0).get(0).getPixelReader();
    var got = reader.getArgb(0, 0);
    assertEquals(want1, got);

    if (index < 8) {
      reader = t.__images.get(y).get(x).getPixelReader();
      got = reader.getArgb(0, 0);
      assertEquals(want2, got);
    }

    reader = t.__images.get(1).get(3).getPixelReader();
    got = reader.getArgb(0, 0);
    assertEquals(want3, got);
  }

  @Test
  public void testResetImage() throws Exception {
    var rect = new Rectangle(20, 20);
    var t = new TileImageModel(rect);
    var img = t.imageProperty().get();

    assertEquals(80, img.getWidth());
    assertEquals(40, img.getHeight());

    rect.setWidth(40);
    rect.setHeight(30);
    t.resetImage();

    img = t.imageProperty().get();
    assertEquals(160, img.getWidth());
    assertEquals(60, img.getHeight());
  }

  @Test
  public void testClearImage() throws Exception {
    var t = new TileImageModel(new Rectangle(20, 20));
    var images = new LinkedList<Image>();
    for (int i = 0; i < 8; i++) images.add(new Image("20x20.png"));
    t.bulkInsert(images);
    t.clear();

    final var colCount = 4;
    for (int i = 0; i < 8; i++) {
      var x = i % colCount;
      var y = i / colCount;
      var reader = t.__images.get(y).get(x).getPixelReader();
      var got = reader.getArgb(0, 0);
      assertEquals(0, got);
    }
  }

  @ParameterizedTest
  @CsvSource({
    "0,0,0", "10,10,0", "20,10,1", "70,10,3", "0,20,4", "20,20,5", "79,20,7", "79,39,7",
  })
  public void testSetImageByAxis(double x, double y, int wantIndex) {
    var t = new TileImageModel(new Rectangle(20, 20));
    var image = new Image("20x20.png");
    t.setImageByAxis(image, x, y);

    final var colCount = 4;
    var xx = wantIndex % colCount;
    var yy = wantIndex / colCount;
    var reader = t.__images.get(yy).get(xx).getPixelReader();
    var got = reader.getArgb(0, 0);
    assertEquals(-65536, got);
  }
}

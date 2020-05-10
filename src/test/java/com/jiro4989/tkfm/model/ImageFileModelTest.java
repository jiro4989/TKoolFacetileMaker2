package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class ImageFileModelTest {
  @Test
  public void testToString() throws Exception {
    var f = new ImageFileModel(new File("t.png"));
    assertEquals("t.png", f.toString());
  }

  @Test
  public void testReadImage() throws Exception {
    var path = getClass().getResource("/20x20.png").getPath();
    var file = new File(path);
    var f = new ImageFileModel(file);
    var got = f.readImage();
    var reader = got.getPixelReader();
    var argb = reader.getArgb(0, 0);
    assertEquals(-65536, argb);
  }
}

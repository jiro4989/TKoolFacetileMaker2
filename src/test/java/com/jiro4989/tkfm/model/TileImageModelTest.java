package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

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
    var t = new TileImageModel();
    Class<?> c = t.getClass();
    Field fs[] = c.getDeclaredFields();
    for (Field f : fs) {
      var name = f.getName();
      if ("images".equals(name)) {
        f.setAccessible(true);
        var obj = f.get(t);
        var images = (List<List<Image>>) obj;
        assertEquals(2, images.size());
        assertEquals(4, images.get(0).size());
        assertEquals(4, images.get(1).size());
      }
    }
  }
}

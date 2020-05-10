package com.jiro4989.tkfm.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class ImageUtilTest {
  @BeforeEach
  public void beforeEach() {
    File dir = new File("tmp");
    dir.mkdirs();
  }

  @AfterEach
  public void afterEach() {
    File dir = new File("tmp");
    for (var f : dir.listFiles()) f.delete();
    dir.delete();
  }

  @Test
  public void testWriteFile() throws IOException {
    var image = new Image("20x20.png");
    var path = "tmp" + File.separator + "out.png";
    var file = new File(path);
    assertFalse(file.exists());
    ImageUtil.writeFile(image, file);

    file = new File(path);
    assertTrue(file.exists());
  }
}

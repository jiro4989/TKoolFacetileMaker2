package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.Test;

public class PropertiesModelTest {
  @Test
  public void testLoadStoreWindow() {
    var p = new PropertiesModel.Window("test_window");
    p.setX(100.1);
    p.setY(200.2);
    p.setWidth(300.3);
    p.setHeight(400.4);
    p.store();

    p.load();
    assertEquals(100.1, p.getX());
    assertEquals(200.2, p.getY());
    assertEquals(300.3, p.getWidth());
    assertEquals(400.4, p.getHeight());
  }

  @Test
  public void testLoadStoreChosedFile() {
    var p = new PropertiesModel.ChoosedFile("test_choosed_file");
    // 存在するファイルなら何でも良い
    p.setOpenedFile(new File(".github/workflows/main.yml"));
    p.setSavedFile(new File("src/main/java/com/jiro4989/tkfm/Main.java"));
    p.store();

    p.load();
    assertTrue(p.getOpenedFile().isPresent());
    assertEquals("main.yml", p.getOpenedFile().get().getName());
    assertTrue(p.getSavedFile().isPresent());
    assertEquals("Main.java", p.getSavedFile().get().getName());
  }
}

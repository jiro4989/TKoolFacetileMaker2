package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class PropertiesModelTest {
  @Test
  public void testWindowConstructor() {
    var p = new PropertiesModel.Window();
    assertEquals(200.0, p.getX());
    assertEquals(200.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

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
  public void testLoadEmptyProperties() throws IOException {
    var dir = new File("config");
    dir.mkdirs();

    var filename = "config/test_window_empty.properties";
    var file = new File(filename);
    var fw = new FileWriter(file);
    var body = "x=\ny=\nwidth=\nheight=\n";
    fw.write(body);
    fw.close();

    var p = new PropertiesModel.Window("test_window_empty");
    p.load();
    assertEquals(200.0, p.getX());
    assertEquals(200.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

  @Test
  public void testLoadEmptyProperties2() throws IOException {
    var dir = new File("config");
    dir.mkdirs();

    var filename = "config/test_window_empty.properties";
    var file = new File(filename);
    var fw = new FileWriter(file);
    var body = "x=90.0\ny=\nwidth=\nheight=\n";
    fw.write(body);
    fw.close();

    var p = new PropertiesModel.Window("test_window_empty");
    p.load();
    assertEquals(90.0, p.getX());
    assertEquals(200.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

  @Test
  public void testLoadEmptyFile() throws IOException {
    var p = new PropertiesModel.Window("test_window_not_exists");
    p.load();
    assertEquals(200.0, p.getX());
    assertEquals(200.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

  @Test
  public void testChosedFileConstructor() {
    var p = new PropertiesModel.ChoosedFile();
    assertFalse(p.getOpenedFile().isPresent());
    assertFalse(p.getSavedFile().isPresent());
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

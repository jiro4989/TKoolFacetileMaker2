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
  public void testConstructor() {
    new PropertiesModel();
  }

  @Test
  public void testWindowConstructor() {
    var p = new PropertiesModel.Window();
    assertEquals(100.0, p.getX());
    assertEquals(100.0, p.getY());
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
    assertEquals(100.0, p.getX());
    assertEquals(100.0, p.getY());
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
    assertEquals(100.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

  @Test
  public void testLoadEmptyFile() throws IOException {
    var p = new PropertiesModel.Window("test_window_not_exists");
    p.load();
    assertEquals(100.0, p.getX());
    assertEquals(100.0, p.getY());
    assertEquals(1280.0, p.getWidth());
    assertEquals(760.0, p.getHeight());
  }

  @Test
  public void testChosedFileConstructor() {
    var p = new PropertiesModel.ChoosedFile();
    assertFalse(p.getOpenedFile().isPresent());
    assertFalse(p.getSavedFile().isPresent());
    p.load();
  }

  @Test
  public void testLoadStoreChosedFile() {
    var p = new PropertiesModel.ChoosedFile("test_choosed_file");
    // 存在するファイルなら何でも良い
    p.setOpenedFile(new File("README.adoc"));
    p.setSavedFile(new File("src/main/java/com/jiro4989/tkfm/Main.java"));
    p.store();

    p.load();
    assertTrue(p.getOpenedFile().isPresent());
    assertEquals("main.yml", p.getOpenedFile().get().getName());
    assertTrue(p.getSavedFile().isPresent());
    assertEquals("Main.java", p.getSavedFile().get().getName());
  }

  @Test
  public void testLoadNotExistsChoosedFile() {
    var p = new PropertiesModel.ChoosedFile("test_not_exists_choosed_file");
    p.load(); // エラーが出ないことを検証
    assertFalse(p.getOpenedFile().isPresent());
    assertFalse(p.getSavedFile().isPresent());
  }

  @Test
  public void testReadFileFromProperties() throws IOException {
    var dir = new File("config");
    dir.mkdirs();

    var filename = "config/test_choosed_file_no_file.properties";
    var file = new File(filename);
    var fw = new FileWriter(file);
    var body =
        "opened_file_dir=config\nopened_file_file=not_exists_file.png\nsaved_file_dir=sushi\nsaved_file_file=sushi.png";
    fw.write(body);
    fw.close();

    var p = new PropertiesModel.ChoosedFile("test_choosed_file_no_file");
    p.load();
    assertTrue(p.getOpenedFile().isPresent());
    assertFalse(p.getSavedFile().isPresent());
  }
}

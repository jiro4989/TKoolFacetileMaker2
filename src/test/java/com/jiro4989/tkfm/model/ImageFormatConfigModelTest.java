package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jiro4989.tkfm.data.Rectangle;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class ImageFormatConfigModelTest {
  @ParameterizedTest
  @CsvSource({
    "0, 144.0, 144.0",
    "1, 96.0, 96.0",
    "2, 30.0, 30.0",
  })
  public void testSelect(int selectIndex, double wantWidth, double wantHeight) throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    fmt.addAdditionalImageFormat(new ImageFormat("test", 3, 3, new Rectangle(30, 30)));
    fmt.select(selectIndex);

    var selected = fmt.getSelectedImageFormat();
    var rect = selected.getRectangle();
    assertEquals(wantWidth, rect.getWidth());
    assertEquals(wantHeight, rect.getHeight());
  }

  @ParameterizedTest
  @CsvSource({
    "image_format_3x3_100x100.xml, 2, 100.0, 100.0",
    "image_format_3x3_100x100_2.xml, 3, 200.0, 300.0",
  })
  public void testLoadXMLFile(String filepath, int selectIndex, double wantWidth, double wantHeight)
      throws Exception {
    var path = Paths.get(getClass().getResource("/" + filepath).getPath());
    var fmt = new ImageFormatConfigModel(false);
    fmt.loadXMLFile(path);
    fmt.select(selectIndex);

    var selected = fmt.getSelectedImageFormat();
    var rect = selected.getRectangle();
    assertEquals(wantWidth, rect.getWidth());
    assertEquals(wantHeight, rect.getHeight());
  }

  @Test
  public void testWriteXML() throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    List<ImageFormat> list = new ArrayList<>();
    list.add(new ImageFormat("3x3", 3, 3, new Rectangle(100, 100)));

    try (Writer w = new StringWriter()) {
      fmt.writeXML(w, list);
      var got = w.toString().trim();
      var path = Paths.get(getClass().getResource("/image_format_3x3_100x100.xml").getPath());
      var want = Files.readString(path).trim();
      assertEquals(want, got);
    } catch (Exception e) {
      throw e;
    }
  }

  @Test
  public void testWriteXML2Items() throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    List<ImageFormat> list = new ArrayList<>();
    list.add(new ImageFormat("3x3", 3, 3, new Rectangle(100, 100)));
    list.add(new ImageFormat("4x5", 4, 5, new Rectangle(200, 300)));

    try (Writer w = new StringWriter()) {
      fmt.writeXML(w, list);
      var got = w.toString().trim();
      var path = Paths.get(getClass().getResource("/image_format_3x3_100x100_2.xml").getPath());
      var want = Files.readString(path).trim();
      assertEquals(want, got);
    } catch (Exception e) {
      throw e;
    }
  }

  @ParameterizedTest
  @CsvSource({
    "true", "false",
  })
  public void testExistsDeletableImageFormats(boolean add) throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    if (add) fmt.addAdditionalImageFormat(new ImageFormat("3x3", 3, 3, new Rectangle(100, 100)));
    assertEquals(add, fmt.existsDeletableImageFormats());
  }

  @Test
  public void testGetAdditionalImageFormatNames() throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    fmt.addAdditionalImageFormat(new ImageFormat("3x3", 3, 3, new Rectangle(100, 100)));
    fmt.addAdditionalImageFormat(new ImageFormat("3x4", 3, 3, new Rectangle(100, 100)));
    var got = fmt.getAdditionalImageFormatNames();
    List<String> want = new ArrayList<>();
    want.add("1. 3x3");
    want.add("2. 3x4");
    assertEquals(want, got);
  }

  @ParameterizedTest
  @CsvSource({
    "0, 20.0, 30.0",
    "1, 100.0, 100.0",
  })
  public void testDeleteAdditionalImageFormat(int selectIndex, double wantWidth, double wantHeight)
      throws Exception {
    var fmt = new ImageFormatConfigModel(false);
    fmt.addAdditionalImageFormat(new ImageFormat("3x3", 3, 3, new Rectangle(100, 100)));
    fmt.addAdditionalImageFormat(new ImageFormat("3x4", 3, 3, new Rectangle(20, 30)));
    fmt.deleteAdditionalImageFormat(selectIndex);
    fmt.select(2);
    var got = fmt.getSelectedImageFormat();
    var gotR = got.getRectangle();
    assertEquals(wantWidth, gotR.getWidth());
    assertEquals(wantHeight, gotR.getHeight());
  }
}

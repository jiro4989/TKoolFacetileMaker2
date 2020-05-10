package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class ImageFilesModelTest {
  @Test
  public void testAddAndClear() {
    var ci = new CroppingImageModel();
    var i = new ImageFilesModel(ci);
    assertEquals(0, i.getFiles().size());

    var path = getClass().getResource("/sample1.png").getPath();
    i.add(new File(path));
    i.add(new File(path));
    assertEquals(2, i.getFiles().size());

    i.clear();
    assertEquals(0, i.getFiles().size());
  }

  @ParameterizedTest
  @CsvSource({
    "2,2,false,false",
    "1,1,false,false",
    "0,1,false,false",
    "0,0,true,false",
    "0,0,true,true",
    "-1,2,false,false",
  })
  public void testRemove(int index, int wantSize, boolean remove, boolean remove2) {
    var ci = new CroppingImageModel();
    var i = new ImageFilesModel(ci);

    var path = getClass().getResource("/sample1.png").getPath();
    i.add(new File(path));
    i.add(new File(path));
    i.remove(index);
    if (remove) i.remove(index);
    if (remove2) i.remove(index);
    assertEquals(wantSize, i.getFiles().size());
  }

  @ParameterizedTest
  @CsvSource({
    "-1", "0", "1", "2",
  })
  public void testSelect(int index) {
    var ci = new CroppingImageModel();
    var i = new ImageFilesModel(ci);

    var path = getClass().getResource("/sample1.png").getPath();
    i.add(new File(path));
    i.add(new File(path));
    i.select(index);
  }
}

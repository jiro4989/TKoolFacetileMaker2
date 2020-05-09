package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PropertiesModelTest {
  @Test
  public void testLoadStore() {
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
}

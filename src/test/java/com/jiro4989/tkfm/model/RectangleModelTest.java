package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

public class RectangleModelTest {
  @Test
  public void testConstructor() {
    var rect = new Rectangle(20, 40);
    assertEquals(20, rect.getWidth());
    assertEquals(40, rect.getHeight());
  }

  @Test
  public void testProperty() {
    var rect = new Rectangle(20, 40);
    assertEquals(20, rect.widthProperty().get());
    assertEquals(40, rect.heightProperty().get());

    rect.setWidth(100);
    rect.setHeight(200);
    assertEquals(100, rect.widthProperty().get());
    assertEquals(200, rect.heightProperty().get());
  }
}

package com.jiro4989.tkfm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class PositionModelTest {
  @Test
  public void testConstructor() {
    var pos = new PositionModel();
    assertEquals(0, pos.getX());
    assertEquals(0, pos.getY());
  }

  @Test
  public void testProperty() {
    var pos = new PositionModel(5, 7);
    assertEquals(5, pos.xProperty().get());
    assertEquals(7, pos.yProperty().get());

    pos.setX(10);
    pos.setY(20);
    assertEquals(10, pos.xProperty().get());
    assertEquals(20, pos.yProperty().get());
  }
}

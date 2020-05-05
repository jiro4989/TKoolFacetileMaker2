package com.jiro4989.tkfm.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Rectangle {
  private IntegerProperty width = new SimpleIntegerProperty(0);
  private IntegerProperty height = new SimpleIntegerProperty(0);

  public Rectangle() {
    this(0, 0);
  }

  public Rectangle(int w, int h) {
    setWidth(w);
    setHeight(h);
  }

  public IntegerProperty widthProperty() {
    return width;
  }

  public IntegerProperty heightProperty() {
    return height;
  }

  public int getWidth() {
    return width.get();
  }

  public int getHeight() {
    return height.get();
  }

  public void setWidth(int w) {
    this.width.set(w);
  }

  public void setHeight(int h) {
    this.height.set(h);
  }
}

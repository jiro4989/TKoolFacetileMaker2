package com.jiro4989.tkfm.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class RectangleModel {
  private final DoubleProperty width = new SimpleDoubleProperty(0.0);
  private final DoubleProperty height = new SimpleDoubleProperty(0.0);

  public RectangleModel(double w, double h) {
    setWidth(w);
    setHeight(h);
  }

  public DoubleProperty widthProperty() {
    return width;
  }

  public DoubleProperty heightProperty() {
    return height;
  }

  public double getWidth() {
    return width.get();
  }

  public double getHeight() {
    return height.get();
  }

  public void setWidth(double w) {
    this.width.set(w);
  }

  public void setHeight(double h) {
    this.height.set(h);
  }
}
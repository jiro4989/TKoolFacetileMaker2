package com.jiro4989.tkfm.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class PositionModel {
  private final DoubleProperty x = new SimpleDoubleProperty(0.0);
  private final DoubleProperty y = new SimpleDoubleProperty(0.0);

  public PositionModel() {
    this(0, 0);
  }

  public PositionModel(double x, double y) {
    setX(x);
    setY(y);
  }

  public DoubleProperty xProperty() {
    return x;
  }

  public DoubleProperty yProperty() {
    return y;
  }

  public double getX() {
    return x.get();
  }

  public double getY() {
    return y.get();
  }

  public void setX(double x) {
    this.x.set(x);
  }

  public void setY(double y) {
    this.y.set(y);
  }
}

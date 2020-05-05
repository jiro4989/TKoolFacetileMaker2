package com.jiro4989.tkfm.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Position {
  private IntegerProperty x = new SimpleIntegerProperty(0);
  private IntegerProperty y = new SimpleIntegerProperty(0);

  public Position(int x, int y) {
    this.x = new SimpleIntegerProperty(x);
    this.y = new SimpleIntegerProperty(y);
  }

  public IntegerProperty xProperty() {
    return x;
  }

  public IntegerProperty yProperty() {
    return y;
  }

  public int getX() {
    return x.get();
  }

  public int getY() {
    return y.get();
  }

  public void setX(int x) {
    this.x.set(x);
  }

  public void setY(int y) {
    this.y.set(y);
  }
}

package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import javafx.beans.property.*;

public class ImageFormat {
  private final String name;
  private final IntegerProperty row;
  private final IntegerProperty col;
  private final Rectangle rect;

  public ImageFormat(String name, int row, int col, Rectangle rect) {
    this.name = name;
    this.row = new SimpleIntegerProperty(row);
    this.col = new SimpleIntegerProperty(col);
    this.rect = rect;
  }

  public IntegerProperty rowProperty() {
    return row;
  }

  public IntegerProperty colProperty() {
    return col;
  }

  public String getName() {
    return name;
  }

  public Rectangle getRectangle() {
    return rect;
  }
}

package com.jiro4989.tkfm.model;

import javafx.beans.property.*;

/** 画像フォーマット。画像フォーマット名、行数、列数、1タイルあたりの矩形を管理する。 */
public class ImageFormatModel {
  /** フォーマットの名前 */
  private final String name;
  /** 行数 */
  private final IntegerProperty row;
  /** 列数 */
  private final IntegerProperty col;
  /** 1タイルあたりの矩形 */
  private final Rectangle rect;

  public ImageFormatModel(String name, int row, int col, Rectangle rect) {
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

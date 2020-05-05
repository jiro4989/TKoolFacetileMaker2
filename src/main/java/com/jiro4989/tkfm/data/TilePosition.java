package com.jiro4989.tkfm.data;

public class TilePosition {
  private int row = 0;
  private int col = 0;

  public TilePosition(int r, int c) {
    this.row = r;
    this.col = c;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }
}

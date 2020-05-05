package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class CroppingImageModel {
  private Optional<BufferedImage> image = Optional.ofNullable(null);
  private Position cropPos = new Position(0, 0);
  private Rectangle cropRect = new Rectangle(144, 144);
  private double scale = 100.0;

  public CroppingImageModel() {}

  public CroppingImageModel(BufferedImage image, Position pos, Rectangle rect, double scale) {
    this.image = Optional.ofNullable(image);
    this.cropPos = pos;
    this.cropRect = rect;
    this.scale = scale;
  }

  public BufferedImage crop() {
    return null;
  }

  public void moveUp(int n) {
    if (!image.isPresent()) {
      return;
    }

    var y = this.cropPos.getY();
    y -= n;
    if (y < 0) {
      y = 0;
    }
    var pos = new Position(this.cropPos.getX(), y);
    this.cropPos = pos;
  }

  public void moveRight(int n) {
    if (!image.isPresent()) {
      return;
    }

    var x = cropPos.getX();
    x += n;

    var bImg = image.get();
    var w = bImg.getWidth();
    var rectWidth = cropRect.getWidth();
    if (w < x + rectWidth) {
      x = w - rectWidth;
    }

    var pos = new Position(x, this.cropPos.getY());
    this.cropPos = pos;
  }

  public void moveDown(int n) {
    if (!image.isPresent()) {
      return;
    }

    var y = this.cropPos.getY();
    y += n;

    var bImg = image.get();
    var h = bImg.getHeight();
    var rectHeight = cropRect.getHeight();
    if (h < y + rectHeight) {
      y = h - rectHeight;
    }

    var pos = new Position(this.cropPos.getX(), y);
    this.cropPos = pos;
  }

  public void moveLeft(int n) {
    if (!image.isPresent()) {
      return;
    }

    var x = this.cropPos.getX();
    x -= n;
    if (x < 0) {
      x = 0;
    }
    var pos = new Position(x, this.cropPos.getY());
    this.cropPos = pos;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

  public Position getPosition() {
    return cropPos;
  }

  public Rectangle getRectangle() {
    return cropRect;
  }

  public void setPositionX(double x) {
    cropPos.setX(x);
  }

  public void setPositionY(double y) {
    cropPos.setY(y);
  }

  public void setRectangleX(double w) {
    cropRect.setWidth(w);
  }

  public void setRectangleY(double h) {
    cropRect.setHeight(h);
  }
}

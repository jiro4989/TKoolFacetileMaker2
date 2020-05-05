package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.awt.image.BufferedImage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CroppingImageModel {
  private ObjectProperty<BufferedImage> image = new SimpleObjectProperty<>(createEmptyImage());
  private Position cropPos = new Position(0, 0);
  private Rectangle cropRect = new Rectangle(144, 144);
  private double scale = 100.0;

  public CroppingImageModel() {}

  public CroppingImageModel(BufferedImage image, Position pos, Rectangle rect, double scale) {
    this.image.set(image);
    this.cropPos = pos;
    this.cropRect = rect;
    this.scale = scale;
  }

  public BufferedImage crop() {
    return null;
  }

  public void moveUp(double n) {
    var y = cropPos.getY();
    y -= n;
    if (y < 0) {
      y = 0;
    }
    cropPos.setY(y);
  }

  public void moveRight(double n) {
    var x = cropPos.getX();
    x += n;

    BufferedImage bImg = image.get();
    var w = bImg.getWidth();
    var rectWidth = cropRect.getWidth();
    if (w < x + rectWidth) {
      x = w - rectWidth;
    }

    cropPos.setX(x);
  }

  public void moveDown(double n) {
    var y = cropPos.getY();
    y += n;

    BufferedImage bImg = image.get();
    var h = bImg.getHeight();
    var rectHeight = cropRect.getHeight();
    if (h < y + rectHeight) {
      y = h - rectHeight;
    }

    cropPos.setY(y);
  }

  public void moveLeft(double n) {
    var x = this.cropPos.getX();
    x -= n;

    if (x < 0) {
      x = 0;
    }

    cropPos.setX(x);
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

  public void setImage(BufferedImage image) {
    this.image.set(image);
  }

  public void clearImage() {
    setImage(createEmptyImage());
  }

  private static BufferedImage createEmptyImage() {
    return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
  }
}

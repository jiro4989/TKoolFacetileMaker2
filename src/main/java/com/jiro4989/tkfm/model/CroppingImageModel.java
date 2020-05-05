package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class CroppingImageModel {
  private ObjectProperty<Image> image = new SimpleObjectProperty<>(createEmptyImage());
  private Position cropPos = new Position(0, 0);
  private Rectangle cropRect = new Rectangle(144, 144);
  private double scale = 100.0;

  public CroppingImageModel() {}

  public CroppingImageModel(Image image, Position pos, Rectangle rect, double scale) {
    this.image.set(image);
    this.cropPos = pos;
    this.cropRect = rect;
    this.scale = scale;
  }

  public Image crop() {
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

    Image bImg = image.get();
    double w = bImg.getWidth();
    var rectWidth = cropRect.getWidth();
    if (w < x + rectWidth) {
      x = w - rectWidth;
    }

    cropPos.setX(x);
  }

  public void moveDown(double n) {
    var y = cropPos.getY();
    y += n;

    Image bImg = image.get();
    double h = bImg.getHeight();
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

  public void setImage(Image image) {
    this.image.set(image);
  }

  public void clearImage() {
    setImage(createEmptyImage());
  }

  public ObjectProperty<Image> imageProperty() {
    return image;
  }

  private static Image createEmptyImage() {
    return new WritableImage(100, 100);
  }
}

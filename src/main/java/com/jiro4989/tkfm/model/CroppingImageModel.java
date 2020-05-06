package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class CroppingImageModel {
  private ObjectProperty<Image> image = new SimpleObjectProperty<>(createEmptyImage());
  private ObjectProperty<Image> croppedImage =
      new SimpleObjectProperty<>(new WritableImage(144, 144));
  private Position cropPos = new Position(0, 0);
  private Rectangle cropRect = new Rectangle(144, 144);
  private DoubleProperty scale = new SimpleDoubleProperty(100.0);

  public CroppingImageModel() {}

  public CroppingImageModel(Image image, Position pos, Rectangle rect, double scale) {
    this.image.set(image);
    this.cropPos = pos;
    this.cropRect = rect;
    this.scale.set(scale);
  }

  public Image crop() {
    double x = cropPos.getX();
    double y = cropPos.getY();
    double width = cropRect.getWidth();
    double height = cropRect.getHeight();
    double scale = this.scale.get() / 100;
    width /= scale;
    x /= scale;
    y /= scale;
    var pix = image.get().getPixelReader();
    return new WritableImage(pix, (int) x, (int) y, (int) width, (int) width);
  }

  public void move(double x, double y) {
    Image bImg = image.get();
    double w = bImg.getWidth();
    double h = bImg.getHeight();
    double rectWidth = cropRect.getWidth();
    double rectHeight = cropRect.getHeight();

    if (x < 0) {
      x = 0;
    } else if (w < x + rectWidth) {
      x = w - rectWidth;
    }

    if (y < 0) {
      y = 0;
    } else if (h < y + rectHeight) {
      y = h - rectHeight;
    }

    cropPos.setX(x);
    cropPos.setY(y);
    croppedImage.set(crop());
  }

  public void moveUp(double n) {
    double x = cropPos.getX();
    double y = cropPos.getY() - n;
    move(x, y);
  }

  public void moveRight(double n) {
    double x = cropPos.getX() + n;
    double y = cropPos.getY();
    move(x, y);
  }

  public void moveDown(double n) {
    double x = cropPos.getX();
    double y = cropPos.getY() + n;
    move(x, y);
  }

  public void moveLeft(double n) {
    double x = cropPos.getX() - n;
    double y = cropPos.getY();
    move(x, y);
  }

  /** Centering */
  public void moveByMouse(double x, double y) {
    double w = cropRect.getWidth();
    double h = cropRect.getHeight();
    x = x - w / 2;
    y = y - h / 2;
    move(x, y);
  }

  public void setScale(double scale) {
    this.scale.set(scale);
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

  public ObjectProperty<Image> croppedImageProperty() {
    return croppedImage;
  }

  public DoubleProperty scaleProperty() {
    return scale;
  }

  private static Image createEmptyImage() {
    return new WritableImage(100, 100);
  }
}

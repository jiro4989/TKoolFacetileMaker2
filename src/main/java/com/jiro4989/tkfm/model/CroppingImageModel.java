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
  private DoubleProperty imageWidth = new SimpleDoubleProperty(288.0);
  private DoubleProperty imageHeight = new SimpleDoubleProperty(288.0);
  private DoubleProperty scale = new SimpleDoubleProperty(100.0);

  public CroppingImageModel() {}

  public CroppingImageModel(Image image, Position pos, Rectangle rect, double scale) {
    this.image.set(image);
    this.cropPos = pos;
    this.cropRect = rect;
    this.scale.set(scale);
  }

  public Image crop() {
    double scale = this.scale.get() / 100;
    double x = cropPos.getX() / scale;
    double y = cropPos.getY() / scale;
    double width = cropRect.getWidth() / scale;
    double height = cropRect.getHeight() / scale;
    var pix = image.get().getPixelReader();
    return new WritableImage(pix, (int) x, (int) y, (int) width, (int) height);
  }

  private void move(double x, double y) {
    Image bImg = image.get();
    double s = scale.get() / 100;
    double w = bImg.getWidth();
    double h = bImg.getHeight();
    double rectWidth = cropRect.getWidth();
    double rectHeight = cropRect.getHeight();

    if (x < 0) {
      x = 0;
    } else if (w * s - rectWidth < x) {
      x = w * s - rectWidth;
    }

    if (y < 0) {
      y = 0;
    } else if (h * s - rectHeight < y) {
      y = h * s - rectHeight;
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

  public void clearImage() {
    setImage(createEmptyImage());
  }

  // property /////////////////////////////////////////////////////////////////

  public ObjectProperty<Image> imageProperty() {
    return image;
  }

  public ObjectProperty<Image> croppedImageProperty() {
    return croppedImage;
  }

  public DoubleProperty imageWidthProperty() {
    return imageWidth;
  }

  public DoubleProperty imageHeightProperty() {
    return imageHeight;
  }

  public DoubleProperty scaleProperty() {
    return scale;
  }

  // getter ///////////////////////////////////////////////////////////////////

  public Position getPosition() {
    return cropPos;
  }

  public Rectangle getRectangle() {
    return cropRect;
  }

  // setter ///////////////////////////////////////////////////////////////////

  public void setImage(Image image) {
    this.image.set(image);
    this.imageWidth.set(image.getWidth());
    this.imageHeight.set(image.getHeight());
    croppedImage.set(crop());
  }

  public void setScale(double scale) {
    this.scale.set(scale);
    croppedImage.set(crop());
  }

  // private method ////////////////////////////////////////////////////////////

  private static Image createEmptyImage() {
    return new WritableImage(100, 100);
  }
}

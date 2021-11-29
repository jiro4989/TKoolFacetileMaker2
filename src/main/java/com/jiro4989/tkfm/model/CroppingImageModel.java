package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Position;
import com.jiro4989.tkfm.data.Rectangle;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
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
    var img = image.get();
    var w = img.getWidth();
    var h = img.getHeight();
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (w <= 0 || h <= 0) {
      // 起こり得ないはず
      return croppedImage.get();
    }
    if (w < x + width || h < y + height) {
      return croppedImage.get();
    }
    var pix = img.getPixelReader();
    return new WritableImage(pix, (int) x, (int) y, (int) width, (int) height);
  }

  public Image cropByBufferedImage() {
    double scale = this.scale.get() / 100;
    var x = (int) cropPos.getX();
    var y = (int) cropPos.getY();
    var width = (int) cropRect.getWidth();
    var height = (int) cropRect.getHeight();

    BufferedImage bImg = SwingFXUtils.fromFXImage(image.get(), null);
    BufferedImage scaledImg = scaledImage(bImg, scale);
    var w = scaledImg.getWidth();
    var h = scaledImg.getHeight();
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (w < x + width) width = (int) (width - ((x + width) - w));
    if (h < y + height) height = (int) (height - ((y + height) - h));
    BufferedImage subImg = scaledImg.getSubimage(x, y, width, height);

    BufferedImage dstImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g = (Graphics2D) dstImg.getGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.drawImage(subImg, 0, 0, null);

    g.dispose();

    WritableImage wImg = SwingFXUtils.toFXImage(dstImg, null);
    return wImg;
  }

  public void move(double x, double y) {
    Image bImg = image.get();
    double s = scale.get() / 100;
    double w = bImg.getWidth();
    double h = bImg.getHeight();
    double rectWidth = cropRect.getWidth();
    double rectHeight = cropRect.getHeight();

    if (w * s - rectWidth < x) x = w * s - rectWidth;
    if (h * s - rectHeight < y) y = h * s - rectHeight;
    if (x < 0) x = 0;
    if (y < 0) y = 0;

    cropPos.setX(x);
    cropPos.setY(y);
    croppedImage.set(crop());
  }

  public void move() {
    double x = cropPos.getX();
    double y = cropPos.getY();
    move(x, y);
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

  public void scaleUp(double n) {
    double s = scale.get();
    double scale = s + n;
    setScale(scale);
  }

  public void scaleDown(double n) {
    double s = scale.get();
    double scale = s - n;
    setScale(scale);
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
    final double MIN_SCALE = 50.0;
    final double MAX_SCALE = 200.0;

    if (scale < MIN_SCALE) {
      scale = MIN_SCALE;
    } else if (MAX_SCALE < scale) {
      scale = MAX_SCALE;
    }

    this.scale.set(scale);
    move();
  }

  // private method ////////////////////////////////////////////////////////////

  private static Image createEmptyImage() {
    return new WritableImage(100, 100);
  }

  /** 拡大した画像を返す。 */
  private static BufferedImage scaledImage(BufferedImage image, double scale) {
    double width = image.getWidth();
    width *= scale;
    double height = image.getHeight();
    height *= scale;
    BufferedImage newImage =
        new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = newImage.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.scale(scale, scale);
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return newImage;
  }
}

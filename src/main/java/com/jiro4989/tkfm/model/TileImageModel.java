package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import java.util.*;
import javafx.beans.property.*;
import javafx.scene.image.*;

public class TileImageModel {
  private int rowCount = 2;
  private int colCount = 4;
  private final Rectangle rect;

  private final List<List<Image>> images = new LinkedList<>();
  private final ObjectProperty<Image> image;

  public TileImageModel(Rectangle rect) {
    this.rect = rect;

    var img = outputTileImage();
    this.image = new SimpleObjectProperty<>(img);
    resetImages();
  }

  public void remove(int x, int y) {
    var img = tileImage();
    images.get(y).set(x, img);
    draw();
  }

  public void clear() {
    for (int y = 0; y < rowCount; y++) {
      for (int x = 0; x < colCount; x++) {
        var img = tileImage();
        images.get(y).set(x, img);
      }
    }
    draw();
  }

  public void bulkInsert(List<Image> images) {
    bulkInsert(images, 0);
  }

  public void bulkInsert(List<Image> images, int startIndex) {
    int size = images.size();
    for (int i = startIndex; i < size; i++) {
      var x = i % colCount;
      var y = i / colCount;
      var img = images.get(i - startIndex);
      setImage(img, x, y);
    }
  }

  public void resetImage() {
    image.set(outputTileImage());
    resetImages();
  }

  public void setImageByAxis(Image img, double mx, double my) {
    var i = image.get();
    double w = i.getWidth();
    double h = i.getHeight();
    var x = (int) (mx / (w / colCount));
    var y = (int) (my / (h / rowCount));
    setImage(img, x, y);
  }

  // property /////////////////////////////////////////////////////////////////

  public ObjectProperty<Image> imageProperty() {
    return image;
  }

  // setter ///////////////////////////////////////////////////////////////////

  public void setImage(Image img, int x, int y) {
    images.get(y).set(x, img);
    draw();
  }

  // private methods //////////////////////////////////////////////////////////

  private void draw() {
    var rawImg = image.get();
    if (rawImg instanceof WritableImage) {
      var img = (WritableImage) rawImg;
      var writer = img.getPixelWriter();
      for (int y = 0; y < rowCount; y++) {
        for (int x = 0; x < colCount; x++) {
          var image = images.get(y).get(x);
          var w = (int) image.getWidth();
          var h = (int) image.getHeight();
          var x2 = x * w;
          var y2 = y * h;
          var reader = image.getPixelReader();
          var fmt = PixelFormat.getIntArgbInstance();
          var buf = new int[w * h];
          var offset = 0;
          var stride = w;
          reader.getPixels(0, 0, w, h, fmt, buf, offset, stride);
          writer.setPixels(x2, y2, w, h, fmt, buf, offset, stride);
        }
      }
    }
  }

  private Image tileImage() {
    var w = (int) rect.getWidth();
    var h = (int) rect.getHeight();
    return new WritableImage(w, h);
  }

  private Image outputTileImage() {
    int w = (int) rect.getWidth();
    int h = (int) rect.getHeight();
    return new WritableImage(colCount * w, rowCount * h);
  }

  private void resetImages() {
    images.clear();
    for (int i = 0; i < rowCount; i++) {
      List<Image> row = new LinkedList<>();
      for (int j = 0; j < colCount; j++) {
        row.add(tileImage());
      }
      images.add(row);
    }
  }
}

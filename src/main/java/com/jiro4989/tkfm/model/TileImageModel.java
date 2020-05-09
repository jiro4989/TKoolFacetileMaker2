package com.jiro4989.tkfm.model;

import java.util.*;
import javafx.beans.property.*;
import javafx.scene.image.*;

public class TileImageModel {
  private int rowCount = 2;
  private int colCount = 4;
  private final int size = 144;

  private final List<List<Image>> images = new LinkedList<>();
  private final ObjectProperty<Image> image =
      new SimpleObjectProperty<>(new WritableImage(colCount * size, rowCount * size));

  public TileImageModel() {
    for (int i = 0; i < rowCount; i++) {
      List<Image> row = new LinkedList<>();
      for (int j = 0; j < colCount; j++) {
        row.add(new WritableImage(size, size));
      }
      images.add(row);
    }
  }

  public void remove(int x, int y) {
    var img = new WritableImage(size, size);
    images.get(y).set(x, img);
    draw();
  }

  public void clear() {
    for (int y = 0; y < rowCount; y++) {
      for (int x = 0; x < colCount; x++) {
        var img = new WritableImage(size, size);
        images.get(y).set(x, img);
      }
    }
    draw();
  }

  public void bulkInsert(List<Image> images) {
    int i = 0;
    int size = images.size();
    for (int y = 0; y < rowCount; y++) {
      for (int x = 0; x < colCount; x++) {
        if (size <= i) {
          return;
        }

        var img = images.get(i);
        setImage(img, x, y);
        i++;
      }
    }
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
}

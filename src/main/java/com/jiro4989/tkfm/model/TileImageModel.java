package com.jiro4989.tkfm.model;

import java.util.*;
import javafx.beans.property.*;
import javafx.scene.image.*;

/** リストの画像データをタイル状に並べた1枚の画像ファイルとして出力するロジックを管理する。 */
public class TileImageModel {
  /** 画像フォーマット */
  private final ImageFormat imageFormat;

  /** タイル画像のリスト */
  final List<List<Image>> __images = new LinkedList<>();

  /** JavaFX用の画像プロパティ */
  private final ObjectProperty<Image> image;

  public TileImageModel(ImageFormatConfigModel model) {
    this.imageFormat = model.getSelectedImageFormat();

    var img = outputTileImage();
    this.image = new SimpleObjectProperty<>(img);
    resetImages();
  }

  /*
  作るだけ作ったけれど今は使っていない
  public void remove(int x, int y) {
    var img = tileImage();
    __images.get(y).set(x, img);
    draw();
  }
  */

  public void clear() {
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
    for (int y = 0; y < rowCount; y++) {
      for (int x = 0; x < colCount; x++) {
        var img = tileImage();
        __images.get(y).set(x, img);
      }
    }
    draw();
  }

  public void bulkInsert(List<Image> images) {
    bulkInsert(images, 0);
  }

  public void bulkInsert(List<Image> images, int startIndex) {
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
    int size = images.size();
    for (int i = startIndex; i < startIndex + size; i++) {
      if (rowCount * colCount <= i) break;
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
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
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
    __images.get(y).set(x, img);
    draw();
  }

  // private methods //////////////////////////////////////////////////////////

  private void draw() {
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
    var rawImg = image.get();
    if (rawImg instanceof WritableImage) {
      var img = (WritableImage) rawImg;
      var writer = img.getPixelWriter();
      for (int y = 0; y < rowCount; y++) {
        for (int x = 0; x < colCount; x++) {
          var image = __images.get(y).get(x);
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
    var w = (int) imageFormat.getRectangle().getWidth();
    var h = (int) imageFormat.getRectangle().getHeight();
    return new WritableImage(w, h);
  }

  private Image outputTileImage() {
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
    var w = (int) imageFormat.getRectangle().getWidth();
    var h = (int) imageFormat.getRectangle().getHeight();
    return new WritableImage(colCount * w, rowCount * h);
  }

  private void resetImages() {
    var rowCount = imageFormat.rowProperty().get();
    var colCount = imageFormat.colProperty().get();
    __images.clear();
    for (int i = 0; i < rowCount; i++) {
      List<Image> row = new LinkedList<>();
      for (int j = 0; j < colCount; j++) {
        row.add(tileImage());
      }
      __images.add(row);
    }
  }
}

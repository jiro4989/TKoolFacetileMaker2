package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/** 画像のトリミングサイズ、列数、行数の設定を保持する。 */
public class ImageFormatConfigModel {
  /** 規定済み画像フォーマット */
  private final List<ImageFormat> imageFormats;
  /** 選択中の画像フォーマット */
  private final ImageFormat selectedImageFormat;

  public ImageFormatConfigModel() {
    this.imageFormats = new ArrayList<>();
    this.imageFormats.add(new ImageFormat("RPGツクールMV・MZ", 2, 4, new Rectangle(144, 144)));
    this.imageFormats.add(new ImageFormat("RPGツクールVXACE", 2, 4, new Rectangle(96, 96)));
    this.selectedImageFormat = this.imageFormats.get(0);
  }

  public class ImageFormat {
    private final String name;
    private final IntegerProperty row;
    private final IntegerProperty col;
    private final Rectangle rect;

    /** インスタンスの生成はこのモデルクラスでしかできない。 */
    private ImageFormat(String name, int row, int col, Rectangle rect) {
      this.name = name;
      this.row = new SimpleIntegerProperty(row);
      this.col = new SimpleIntegerProperty(col);
      this.rect = rect;
    }

    public IntegerProperty rowProperty() {
      return row;
    }

    public IntegerProperty colProperty() {
      return col;
    }

    String getName() {
      return name;
    }

    public Rectangle getRectangle() {
      return rect;
    }
  }

  /**
   * 画像フォーマット一覧の中からインデックスでフォーマットを選択して切り替える。
   *
   * @param index 画像フォーマットのインデックス
   */
  public void select(int index) {
    var max = imageFormats.size();
    if (index < 0) return;
    if (max <= index) return;
    var fmt = imageFormats.get(index);
    selectedImageFormat.rowProperty().set(fmt.rowProperty().get());
    selectedImageFormat.colProperty().set(fmt.colProperty().get());
    selectedImageFormat
        .getRectangle()
        .widthProperty()
        .set(fmt.getRectangle().widthProperty().get());
    selectedImageFormat
        .getRectangle()
        .widthProperty()
        .set(fmt.getRectangle().heightProperty().get());
  }

  public ImageFormat getSelectedImageFormat() {
    return selectedImageFormat;
  }

  /**
   * 画像フォーマットの名前一覧を返却する。
   *
   * @return フォーマット名の一覧
   */
  public List<String> getNames() {
    return imageFormats.stream().map(e -> e.getName()).toList();
  }
}

package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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

    public String getName() {
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

  /**
   * streamを読み込んで画像フォーマット一覧に追加する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param inputStream
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public void readXML(InputStream inputStream)
      throws ParserConfigurationException, IOException, SAXException {
    var factory = DocumentBuilderFactory.newInstance();
    var builder = factory.newDocumentBuilder();
    var document = builder.parse(inputStream);
    var root = document.getDocumentElement();
    var fmts = root.getElementsByTagName("imageFormat");
    for (var i = 0; i < fmts.getLength(); i++) {
      var element = (Element) fmts.item(i);
      var name = element.getAttribute("name");
      var row = Integer.parseInt(element.getAttribute("row"));
      var col = Integer.parseInt(element.getAttribute("col"));
      var tileWidth = Integer.parseInt(element.getAttribute("tileWidth"));
      var tileHeight = Integer.parseInt(element.getAttribute("tileHeight"));
      var rect = new Rectangle(tileWidth, tileHeight);
      var fmt = new ImageFormat(name, row, col, rect);
      imageFormats.add(fmt);
    }
  }

  /**
   * 画像フォーマット一覧をstreamに書き込む。書き込む画像フォーマットはimageFormatsの先頭2つを除外したもののみ。
   * 先頭2つはRPGツクールMV、VXACEの2つで、組み込みサポートのため書き込む必要がない。 このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param outputStream
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   */
  public void writeXML(OutputStream outputStream)
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
    var factory = DocumentBuilderFactory.newInstance();
    var builder = factory.newDocumentBuilder();
    var document = builder.newDocument();
    var root = document.createElement("imageFormats");
    document.appendChild(root);
    imageFormats.stream()
        .skip(2)
        .forEach(
            fmt -> {
              var item = document.createElement("imageFormat");
              var rect = fmt.getRectangle();
              item.setAttribute("name", "" + fmt.getName());
              item.setAttribute("row", "" + fmt.rowProperty().get());
              item.setAttribute("col", "" + fmt.colProperty().get());
              item.setAttribute("tileWidth", "" + rect.widthProperty().get());
              item.setAttribute("tileHeight", "" + rect.heightProperty().get());
              root.appendChild(item);
            });
    var transformerFactory = TransformerFactory.newInstance();
    var transformer = transformerFactory.newTransformer();
    var domSource = new DOMSource(document);
    var streamResult = new StreamResult(outputStream);
    transformer.transform(domSource, streamResult);
  }

  public List<ImageFormat> getImageFormats() {
    return imageFormats;
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

package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  /** 設定ファイルの配置先 */
  private static final Path CONFIG_FILE_PATH = Paths.get(".", "config", "image_format.xml");

  /**
   * 画像フォーマットを初期設定してインスタンスを生成する。 組み込みでRPGツクールMV・MZと、RPGツクールVXACEの規格をサポートする。
   * ユーザ定義の設定ファイルが存在した場合は設定ファイルを読み込んで追加する。
   *
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public ImageFormatConfigModel() throws ParserConfigurationException, IOException, SAXException {
    this.imageFormats = new ArrayList<>();
    this.imageFormats.add(new ImageFormat("RPGツクールMV・MZ", 2, 4, new Rectangle(144, 144)));
    this.imageFormats.add(new ImageFormat("RPGツクールVXACE", 2, 4, new Rectangle(96, 96)));
    this.selectedImageFormat = this.imageFormats.get(0);
    loadXMLFile(CONFIG_FILE_PATH);
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
        .heightProperty()
        .set(fmt.getRectangle().heightProperty().get());
  }

  /**
   * 指定パスのXMLファイルを読み込み、画像フォーマットリストに追加する。ファイルが存在しなかった場合は何もしない。例外が発生しても確実に開いたファイルを閉じる。
   *
   * @param path
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public void loadXMLFile(Path path)
      throws ParserConfigurationException, IOException, SAXException {
    if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
      return;
    }

    // 例外を投げる前に確実にcloseしておきたいため
    try (InputStream is = new FileInputStream(path.toFile())) {
      loadXML(is);
    } catch (ParserConfigurationException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    } catch (SAXException e) {
      throw e;
    }
  }

  /**
   * streamを読み込んで画像フォーマットリストに追加する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param inputStream
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public void loadXML(InputStream inputStream)
      throws ParserConfigurationException, IOException, SAXException {
    var formats = readXML(inputStream);
    imageFormats.addAll(formats);
  }

  /**
   * streamを読み込んで画像フォーマットリストを返却する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param inputStream
   * @return 画像フォーマットオブジェクトのリスト
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  private List<ImageFormat> readXML(InputStream inputStream)
      throws ParserConfigurationException, IOException, SAXException {
    var factory = DocumentBuilderFactory.newInstance();
    var builder = factory.newDocumentBuilder();
    var document = builder.parse(inputStream);
    var root = document.getDocumentElement();
    var fmts = root.getElementsByTagName("imageFormat");
    List<ImageFormat> result = new ArrayList<>();
    for (var i = 0; i < fmts.getLength(); i++) {
      var element = (Element) fmts.item(i);
      var name = element.getAttribute("name");
      var row = Integer.parseInt(element.getAttribute("row"));
      var col = Integer.parseInt(element.getAttribute("col"));
      var tileWidth = Integer.parseInt(element.getAttribute("tileWidth"));
      var tileHeight = Integer.parseInt(element.getAttribute("tileHeight"));
      var rect = new Rectangle(tileWidth, tileHeight);
      var fmt = new ImageFormat(name, row, col, rect);
      result.add(fmt);
    }
    return result;
  }

  /**
   * 画像フォーマット一覧をstreamに書き込む。書き込む画像フォーマットはimageFormatsの先頭2つを除外したもののみ。
   * 先頭2つはRPGツクールMV、VXACEの2つで、組み込みサポートのため書き込む必要がない。 このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param writer
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   */
  public void writeXML(Writer writer)
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
    writeXML(writer, imageFormats.stream().skip(2).toList());
  }

  /**
   * 引数の画像フォーマット一覧をstreamに書き込む。
   *
   * @param writer
   * @param formats 画像フォーマットのリスト
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   */
  private void writeXML(Writer writer, List<ImageFormat> formats)
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
    var factory = DocumentBuilderFactory.newInstance();
    var builder = factory.newDocumentBuilder();
    var document = builder.newDocument();
    var root = document.createElement("imageFormats");
    document.appendChild(root);
    formats.stream()
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
    var streamResult = new StreamResult(writer);
    transformer.transform(domSource, streamResult);
  }

  public List<ImageFormat> getImageFormats() {
    return imageFormats;
  }

  public ImageFormat getSelectedImageFormat() {
    return selectedImageFormat;
  }
}

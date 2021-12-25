package com.jiro4989.tkfm.model;

import com.jiro4989.tkfm.data.Rectangle;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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
  /** ユーザ定義の画像フォーマット */
  private final List<ImageFormat> additionalImageFormats;
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
    this(true);
  }

  /**
   * 画像フォーマットを初期設定してインスタンスを生成する。 組み込みでRPGツクールMV・MZと、RPGツクールVXACEの規格をサポートする。
   * ユーザ定義の設定ファイルが存在した場合は設定ファイルを読み込んで追加する。loadXMLがtrueの場合は規定の設定ファイルを読み込む。
   * loadXMLがfalseの場合は設定ファイルを読み込まない。falseにするケースは主にユニットテストの場合のみを想定している。
   *
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public ImageFormatConfigModel(boolean loadXML)
      throws ParserConfigurationException, IOException, SAXException {
    this.imageFormats = new ArrayList<>();
    this.imageFormats.add(new ImageFormat("RPGツクールMV・MZ", 2, 4, new Rectangle(144, 144)));
    this.imageFormats.add(new ImageFormat("RPGツクールVXACE", 2, 4, new Rectangle(96, 96)));
    this.additionalImageFormats = new ArrayList<>();
    this.selectedImageFormat = new ImageFormat("RPGツクールMV・MZ", 2, 4, new Rectangle(144, 144));
    if (loadXML) loadXMLFile(CONFIG_FILE_PATH);
  }

  /**
   * 組み込み画像フォーマットとユーザ定義の画像フォーマットを合わせたListの中からインデックスでフォーマットを選択して切り替える。
   * Listの範囲外を指定した場合はエラーにはしないで無視する。
   *
   * @param index 画像フォーマットのインデックス
   */
  public void select(int index) {
    var total = createTotalImageFormats();
    var max = total.size();
    if (index < 0) return;
    if (max <= index) return;
    var fmt = total.get(index);
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
    additionalImageFormats.addAll(formats);
  }

  /**
   * streamを読み込んで画像フォーマットリストとして返却する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   * 主にユニットテスト時に使う想定。
   *
   * @param inputStream
   * @return 画像フォーマットオブジェクトのリスト
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public List<ImageFormat> readXML(InputStream inputStream)
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
   * 組み込みのPathのファイルに設定を書き込む。配置先ディレクトリが存在しなかったら作成する。
   *
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   */
  public void writeXMLFile()
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException,
          IOException {
    writeXMLFile(CONFIG_FILE_PATH);
  }

  /**
   * 指定のPathのファイルに画像フォーマット一覧を書き込む。配置先ディレクトリが存在しなかったら作成する。
   * 組み込み画像フォーマットは保存する意味が無いため、ユーザ定義の画像フォーマットのみ書き込む。
   *
   * @param path
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   */
  public void writeXMLFile(Path path)
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException,
          IOException {
    Path dir = path.getParent();
    if (Files.notExists(dir, LinkOption.NOFOLLOW_LINKS)) {
      Files.createDirectories(dir);
    }

    // 例外を投げる前に確実にcloseしておきたいため
    try (Writer w = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
      writeXML(w, additionalImageFormats);
    } catch (ParserConfigurationException e) {
      throw e;
    } catch (TransformerConfigurationException e) {
      throw e;
    } catch (TransformerException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    }
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
  public void writeXML(Writer writer, List<ImageFormat> formats)
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
              item.setAttribute("tileWidth", "" + (int) rect.widthProperty().get());
              item.setAttribute("tileHeight", "" + (int) rect.heightProperty().get());
              root.appendChild(item);
            });
    var transformerFactory = TransformerFactory.newInstance();
    var transformer = transformerFactory.newTransformer();
    var domSource = new DOMSource(document);
    var streamResult = new StreamResult(writer);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(domSource, streamResult);
  }

  /**
   * ユーザ定義の画像フォーマットリストに追加する。
   * @param fmt 画像フォーマット
   */
  public void addAdditionalImageFormat(ImageFormat fmt) {
    additionalImageFormats.add(fmt);
  }

  /**
   * 削除可能な画像フォーマットが存在する場合にtrueを返す。
   * これはユーザ定義の画像フォーマットが存在することと同義である。
   * @return 削除可能な画像フォーマットが存在するか否か
   */
  public boolean existsDeletableImageFormats() {
    return 0 < additionalImageFormats.size();
  }

  /**
   * ユーザ定義の画像フォーマットの名前のリストを返却する。
   * @return ユーザ定義の画像フォーマットの名前のリスト
   */
  public List<String> getAdditionalImageFormatNames() {
    var i = 1;
    List<String> result = new ArrayList<>();
    for (var fmt : additionalImageFormats) {
      result.add(i + ". " + fmt.getName());
      i++;
    }
    return result;
  }

  /**
   * ユーザ定義の画像フォーマットリストの要素をインデックス指定で削除する。
   * @param index
   */
  public void deleteAdditionalImageFormat(int index) {
    additionalImageFormats.remove(index);
  }

  /**
   * 規定済み画像フォーマットと、ユーザ定義の画像フォーマットを合わせたListを新規に生成して返却する。
   *
   * @return 全部の画像フォーマット
   */
  public List<ImageFormat> createTotalImageFormats() {
    List<ImageFormat> totalFormats = new ArrayList<>();
    totalFormats.addAll(imageFormats);
    totalFormats.addAll(additionalImageFormats);
    return totalFormats;
  }

  // Getter //

  /**
   * 選択中の画像フォーマットを返却する。
   * @return 選択中の画像フォーマット
   */
  public ImageFormat getSelectedImageFormat() {
    return selectedImageFormat;
  }

}

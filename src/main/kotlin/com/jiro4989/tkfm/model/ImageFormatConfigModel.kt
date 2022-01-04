package com.jiro4989.tkfm.model

import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.Writer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Element
import org.xml.sax.SAXException

private const val RPG_MAKER_MV_IMAGE_FORMAT_TILE_WIDTH = 144.0

private const val RPG_MAKER_VXACE_IMAGE_FORMAT_TILE_WIDTH = 96.0

private const val RPG_MAKER_IMAGE_FORMAT_ROW_COUNT = 2

private const val RPG_MAKER_IMAGE_FORMAT_COL_COUNT = 4

/** 設定ファイルの配置先 */
private val configFilePath: Path = Paths.get(".", "config", "image_format.xml")

private fun createRPGMakerMVImageFormat() =
    ImageFormatModel(
        "RPGツクールMV・MZ",
        RPG_MAKER_IMAGE_FORMAT_ROW_COUNT,
        RPG_MAKER_IMAGE_FORMAT_COL_COUNT,
        RectangleModel(RPG_MAKER_MV_IMAGE_FORMAT_TILE_WIDTH, RPG_MAKER_MV_IMAGE_FORMAT_TILE_WIDTH))

private fun createRPGMakerVXACEImageFormat() =
    ImageFormatModel(
        "RPGツクールVXACE",
        RPG_MAKER_IMAGE_FORMAT_ROW_COUNT,
        RPG_MAKER_IMAGE_FORMAT_COL_COUNT,
        RectangleModel(
            RPG_MAKER_VXACE_IMAGE_FORMAT_TILE_WIDTH, RPG_MAKER_VXACE_IMAGE_FORMAT_TILE_WIDTH))

/** 画像のトリミングサイズ、列数、行数の設定を保持する。 */
data class ImageFormatConfigModel(
    /** 規定済み画像フォーマット */
    private val imageFormats: List<ImageFormatModel> =
        listOf(createRPGMakerMVImageFormat(), createRPGMakerVXACEImageFormat()),
    /** ユーザ定義の画像フォーマット */
    private val additionalImageFormats: MutableList<ImageFormatModel> = mutableListOf(),
    private val loadXML: Boolean = true,
    /** 選択中の画像フォーマット */
    val selectedImageFormat: ImageFormatModel = createRPGMakerMVImageFormat()
) {
  init {
    if (loadXML) loadXMLFile(configFilePath)
  }

  /**
   * 組み込み画像フォーマットとユーザ定義の画像フォーマットを合わせたListの中からインデックスでフォーマットを選択して切り替える。 Listの範囲外を指定した場合はエラーにはしないで無視する。
   *
   * @param index 画像フォーマットのインデックス
   */
  fun select(index: Int) {
    val total = createTotalImageFormats()
    val max = total.size
    if (index < 0) return
    if (max <= index) return
    val fmt = total.get(index)
    selectedImageFormat.row = fmt.row
    selectedImageFormat.col = fmt.col
    selectedImageFormat.rectangle.widthProperty.set(fmt.rectangle.width)
    selectedImageFormat.rectangle.heightProperty.set(fmt.rectangle.height)
  }

  /**
   * 指定パスのXMLファイルを読み込み、画像フォーマットリストに追加する。ファイルが存在しなかった場合は何もしない。例外が発生しても確実に開いたファイルを閉じる。
   *
   * @param path
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  fun loadXMLFile(path: Path) {
    if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
      return
    }

    // 例外を投げる前に確実にcloseしておきたいため
    FileInputStream(path.toFile()).use { stream: InputStream -> loadXML(stream) }
  }

  /**
   * streamを読み込んで画像フォーマットリストに追加する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。
   *
   * @param inputStream
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  fun loadXML(inputStream: InputStream) {
    val formats = readXML(inputStream)
    additionalImageFormats.addAll(formats)
  }

  /**
   * streamを読み込んで画像フォーマットリストとして返却する。このメソッド内ではstreamを閉じないため、メソッド呼び出し元でstreamを閉じること。 主にユニットテスト時に使う想定。
   *
   * @param inputStream
   * @return 画像フォーマットオブジェクトのリスト
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  fun readXML(inputStream: InputStream): List<ImageFormatModel> {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document = builder.parse(inputStream)
    val root = document.getDocumentElement()
    val fmts = root.getElementsByTagName("imageFormat")
    val result =
        (0 until fmts.getLength()).map { i ->
          val element = fmts.item(i) as Element
          val name = element.getAttribute("name")
          val row = element.getAttribute("row").toInt()
          val col = element.getAttribute("col").toInt()
          val tileWidth = element.getAttribute("tileWidth").toInt().toDouble()
          val tileHeight = element.getAttribute("tileHeight").toInt().toDouble()
          val rect = RectangleModel(tileWidth, tileHeight)
          val fmt = ImageFormatModel(name, row, col, rect)
          fmt
        }
    return result
  }

  /**
   * 組み込みのPathのファイルに設定を書き込む。配置先ディレクトリが存在しなかったら作成する。
   *
   * @throws ParserConfigurationException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   */
  fun writeXMLFile() {
    writeXMLFile(configFilePath)
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
  fun writeXMLFile(path: Path) {
    val dir = path.getParent()
    if (Files.notExists(dir, LinkOption.NOFOLLOW_LINKS)) {
      Files.createDirectories(dir)
    }

    // 例外を投げる前に確実にcloseしておきたいため
    FileWriter(path.toFile(), StandardCharsets.UTF_8).use { w: Writer ->
      writeXML(w, additionalImageFormats)
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
  fun writeXML(writer: Writer, formats: List<ImageFormatModel>) {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document = builder.newDocument()
    val root = document.createElement("imageFormats")
    document.appendChild(root)
    formats.forEach { fmt ->
      val item = document.createElement("imageFormat")
      val rect = fmt.rectangle
      item.setAttribute("name", fmt.name)
      item.setAttribute("row", fmt.row.toString())
      item.setAttribute("col", fmt.col.toString())
      item.setAttribute("tileWidth", "" + rect.widthProperty.get().toInt().toString())
      item.setAttribute("tileHeight", "" + rect.heightProperty.get().toInt().toString())
      root.appendChild(item)
    }
    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()
    val domSource = DOMSource(document)
    val streamResult = StreamResult(writer)
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.transform(domSource, streamResult)
  }

  /**
   * ユーザ定義の画像フォーマットリストに追加する。
   *
   * @param fmt 画像フォーマット
   */
  fun addAdditionalImageFormat(fmt: ImageFormatModel) {
    additionalImageFormats.add(fmt)
  }

  /**
   * 削除可能な画像フォーマットが存在する場合にtrueを返す。 これはユーザ定義の画像フォーマットが存在することと同義である。
   *
   * @return 削除可能な画像フォーマットが存在するか否か
   */
  fun existsDeletableImageFormats() = 0 < additionalImageFormats.size

  /**
   * ユーザ定義の画像フォーマットの名前のリストを返却する。
   *
   * @return ユーザ定義の画像フォーマットの名前のリスト
   */
  fun getAdditionalImageFormatNames(): List<String> {
    val result = additionalImageFormats.mapIndexed { i, v -> "${i+1}. ${v.name}" }
    return result
  }

  /**
   * ユーザ定義の画像フォーマットリストの要素をインデックス指定で削除する。
   *
   * @param index
   */
  fun deleteAdditionalImageFormat(index: Int) {
    additionalImageFormats.removeAt(index)
  }

  /**
   * 規定済み画像フォーマットと、ユーザ定義の画像フォーマットを合わせたListを新規に生成して返却する。
   *
   * @return 全部の画像フォーマット
   */
  fun createTotalImageFormats(): List<ImageFormatModel> {
    val result: MutableList<ImageFormatModel> = mutableListOf()
    result += imageFormats
    result += additionalImageFormats
    return result
  }
}

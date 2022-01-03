package com.jiro4989.tkfm.controller

import com.jiro4989.tkfm.CropImageStage
import com.jiro4989.tkfm.ImageFormatStage
import com.jiro4989.tkfm.model.ChoosedFilePropertiesModel
import com.jiro4989.tkfm.model.CroppingImageModel
import com.jiro4989.tkfm.model.ImageFileModel
import com.jiro4989.tkfm.model.ImageFilesModel
import com.jiro4989.tkfm.model.ImageFormatConfigModel
import com.jiro4989.tkfm.model.ImageFormatModel
import com.jiro4989.tkfm.model.TileImageModel
import com.jiro4989.tkfm.util.showAndWaitCommonExceptionDialog
import com.jiro4989.tkfm.util.showAndWaitExceptionDialog
import com.jiro4989.tkfm.util.writeFile
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ChoiceDialog
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.Slider
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.RowConstraints
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import org.xml.sax.SAXException

@Suppress("MagicNumber")
private fun createComboItems() = FXCollections.observableArrayList(1, 5, 10, 25, 50)

// Scale用のスライダーはは百分率で値を保持するため、拡縮演算をする際は100で割って小数に変換する必要がある。
// これはその小数への変換用の定数
private const val TO_DECIMAL_DIVIDE_NUMBER = 100

// @FXMLアノテーション経由で呼び出されるメソッドが指摘されるのを無視する
@Suppress("UnusedPrivateMember")
class MainViewController : Initializable {
  // UI parts /////////////////////////////////////////////////////////////////

  // Menu
  @FXML private lateinit var imageFormatMenu: Menu
  private val group = ToggleGroup()

  // List view
  @FXML private lateinit var fileListView: ListView<ImageFileModel>
  @FXML private lateinit var bulkInsertButton: Button
  @FXML private lateinit var clearButton: Button
  @FXML private lateinit var removeButton: Button
  @FXML private lateinit var clearOutputButton: Button

  // Crop view
  @FXML private lateinit var cropImageGridPane: GridPane
  @FXML private lateinit var cropImageView: ImageView
  @FXML private lateinit var cropXLabel: Label
  @FXML private lateinit var cropYLabel: Label
  @FXML private lateinit var cropScaleLabel: Label
  @FXML private lateinit var cropScaleSlider: Slider
  @FXML private lateinit var cropAxisComboBox: ComboBox<Int>
  @FXML private lateinit var focusShadowPaneTop: Pane
  @FXML private lateinit var focusShadowPaneLeft: Pane
  @FXML private lateinit var focusShadowPaneRight: Pane
  @FXML private lateinit var focusShadowPaneBottom: Pane

  private val cropAxisItems: ObservableList<Int> = createComboItems()
  @FXML private lateinit var cropScaleComboBox: ComboBox<Int>
  private val cropScaleItems: ObservableList<Int> = createComboItems()

  // Output view
  @FXML private lateinit var outputGridPane: GridPane
  @FXML private lateinit var outputImageView: ImageView

  // models ///////////////////////////////////////////////////////////////////

  private lateinit var imageFormat: ImageFormatConfigModel
  private lateinit var imageFiles: ImageFilesModel
  private lateinit var cropImage: CroppingImageModel
  private lateinit var tileImage: TileImageModel
  private val prop = ChoosedFilePropertiesModel()

  override fun initialize(location: URL?, resources: ResourceBundle?) {
    // initialize models
    try {
      imageFormat = ImageFormatConfigModel()
    } catch (e: ParserConfigurationException) {
      e.printStackTrace()
      showAndWaitCommonExceptionDialog("ParserConfigurationException")
      Platform.exit()
    } catch (e: IOException) {
      e.printStackTrace()

      val exception = "IOException"
      val msg =
          """画像フォーマットファイルの読み込みに失敗しました。
以下の観点で確認してください。

- configフォルダが存在するか
- 特別なフォルダ (システムフォルダなど)で実行していないか
"""
      showAndWaitExceptionDialog(exception, msg)
      Platform.exit()
    } catch (e: SAXException) {
      e.printStackTrace()

      val exception = "SAXException"
      val msg =
          """画像フォーマットファイルの読込に失敗しました。
config/image_format.xmlファイルを手動で書き換えるなどして、
不正なXMLファイルになっている可能性があります。
当該ファイルを削除してアプリケーションを再起動してみてください
"""
      showAndWaitExceptionDialog(exception, msg)
      Platform.exit()
    }

    val selectedImageFormat = imageFormat.selectedImageFormat
    val rect = selectedImageFormat.rectangle

    cropImage = CroppingImageModel(croppingRectangle = rect)
    imageFiles = ImageFilesModel(cropImage)
    tileImage = TileImageModel(imageFormat)

    // bindigns
    val pos = cropImage.croppingPosition
    val rowCount = selectedImageFormat.rowProperty
    val colCount = selectedImageFormat.colProperty

    // 行数、列数、タイル幅が変更されたら出力画像ビューをリセットする
    rowCount.addListener { _ -> resetOutputGridPane() }
    colCount.addListener { _ -> resetOutputGridPane() }
    rect.widthProperty.addListener { _ -> resetOutputGridPane() }
    rect.heightProperty.addListener { _ -> resetOutputGridPane() }

    // フォーカス用の影レイヤ
    Bindings.bindBidirectional(
        focusShadowPaneTop.layoutXProperty(), cropImage.shadowTopLayerXProperty)
    Bindings.bindBidirectional(
        focusShadowPaneTop.layoutYProperty(), cropImage.shadowTopLayerYProperty)
    Bindings.bindBidirectional(
        focusShadowPaneTop.prefWidthProperty(), cropImage.shadowTopLayerWidthProperty)
    Bindings.bindBidirectional(
        focusShadowPaneTop.prefHeightProperty(), cropImage.shadowTopLayerHeightProperty)

    Bindings.bindBidirectional(
        focusShadowPaneRight.layoutXProperty(), cropImage.shadowRightLayerXProperty)
    Bindings.bindBidirectional(
        focusShadowPaneRight.layoutYProperty(), cropImage.shadowRightLayerYProperty)
    Bindings.bindBidirectional(
        focusShadowPaneRight.prefWidthProperty(), cropImage.shadowRightLayerWidthProperty)
    Bindings.bindBidirectional(
        focusShadowPaneRight.prefHeightProperty(), cropImage.shadowRightLayerHeightProperty)

    Bindings.bindBidirectional(
        focusShadowPaneLeft.layoutXProperty(), cropImage.shadowLeftLayerXProperty)
    Bindings.bindBidirectional(
        focusShadowPaneLeft.layoutYProperty(), cropImage.shadowLeftLayerYProperty)
    Bindings.bindBidirectional(
        focusShadowPaneLeft.prefWidthProperty(), cropImage.shadowLeftLayerWidthProperty)
    Bindings.bindBidirectional(
        focusShadowPaneLeft.prefHeightProperty(), cropImage.shadowLeftLayerHeightProperty)

    Bindings.bindBidirectional(
        focusShadowPaneBottom.layoutXProperty(), cropImage.shadowBottomLayerXProperty)
    Bindings.bindBidirectional(
        focusShadowPaneBottom.layoutYProperty(), cropImage.shadowBottomLayerYProperty)
    Bindings.bindBidirectional(
        focusShadowPaneBottom.prefWidthProperty(), cropImage.shadowBottomLayerWidthProperty)
    Bindings.bindBidirectional(
        focusShadowPaneBottom.prefHeightProperty(), cropImage.shadowBottomLayerHeightProperty)

    // scaleは100分率で値を保持するため、乗算する場合は少数に変換する必要がある
    cropImageGridPane.prefWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty,
                Bindings.divide(cropScaleSlider.valueProperty(), TO_DECIMAL_DIVIDE_NUMBER)))
    cropImageGridPane.prefHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty,
                Bindings.divide(cropScaleSlider.valueProperty(), TO_DECIMAL_DIVIDE_NUMBER)))

    Bindings.bindBidirectional(cropImageView.imageProperty(), cropImage.imageProperty)
    cropImageView.fitWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty,
                Bindings.divide(cropScaleSlider.valueProperty(), TO_DECIMAL_DIVIDE_NUMBER)))
    cropImageView.fitHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty,
                Bindings.divide(cropScaleSlider.valueProperty(), TO_DECIMAL_DIVIDE_NUMBER)))

    val cropXConv: StringConverter<Number> = NumberStringConverter()
    Bindings.bindBidirectional(cropXLabel.textProperty(), pos.xProperty, cropXConv)
    val cropYConv: StringConverter<Number> = NumberStringConverter()
    Bindings.bindBidirectional(cropYLabel.textProperty(), pos.yProperty, cropYConv)
    val cropScaleConv: StringConverter<Number> = NumberStringConverter()
    Bindings.bindBidirectional(
        cropScaleLabel.textProperty(), cropImage.scaleProperty, cropScaleConv)
    Bindings.bindBidirectional(cropScaleSlider.valueProperty(), cropImage.scaleProperty)
    Bindings.bindBidirectional(outputImageView.imageProperty(), tileImage.imageProperty())
    outputGridPane.prefWidthProperty().bind(Bindings.multiply(rect.widthProperty, colCount))
    outputGridPane.prefHeightProperty().bind(Bindings.multiply(rect.heightProperty, rowCount))
    outputImageView.fitWidthProperty().bind(Bindings.multiply(rect.widthProperty, colCount))
    outputImageView.fitHeightProperty().bind(Bindings.multiply(rect.heightProperty, rowCount))

    // configurations
    fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
    fileListView.getSelectionModel().selectedItemProperty().addListener { _ -> changeSelection() }
    fileListView.setItems(imageFiles.files)
    cropAxisComboBox.setItems(cropAxisItems)
    cropAxisComboBox.getSelectionModel().select(1 as Int?)
    cropScaleComboBox.setItems(cropScaleItems)
    cropScaleComboBox.getSelectionModel().select(1 as Int?)
    cropScaleSlider.valueProperty().addListener { _, _, newValue ->
      cropImage.setScale(newValue.toDouble())
    }

    // properties
    prop.load()

    resetImageFormatMenu(false)
    resetOutputGridPane()
  }

  private fun getInitialDir(file: File): File {
    return if (file.isDirectory) file else if (file.isFile) file.parentFile else File(".")
  }

  /** 取り込むファイルを選択する。 */
  @FXML
  private fun openFile() {
    val fc = FileChooser()
    fc.title = "ファイルを開く"
    fc.extensionFilters += ExtensionFilter("Image Files", "*.png")

    prop.openedFile?.let { fc.initialDirectory = getInitialDir(it) }

    val stage = Stage(StageStyle.UTILITY)
    val files: List<File>? = fc.showOpenMultipleDialog(stage)
    files?.let {
      it.forEach {
        imageFiles.add(it)
        prop.openedFile = it
      }
    }
  }

  /** ファイルを保存する。 ファイル名が存在しなかった場合は別名で保存が呼び出される。 */
  @FXML
  private fun saveFile() {
    prop.savedFile?.let {
      if (it.isFile()) {
        try {
          var img = outputImageView.getImage()
          writeFile(img, it)
        } catch (e: IOException) {
          // TODO
          e.printStackTrace()
        }
      } else {
        saveAsFile()
      }
    }
  }

  /** 別名で保存する。 */
  @FXML
  private fun saveAsFile() {
    val fc = FileChooser()
    fc.title = "名前をつけて保存"
    fc.extensionFilters += ExtensionFilter("Image Files", "*.png")

    prop.savedFile?.let { fc.initialDirectory = getInitialDir(it) }

    val stage = Stage(StageStyle.UTILITY)
    val saveFile: File? = fc.showSaveDialog(stage)
    saveFile?.let {
      try {
        val img = outputImageView.getImage()
        writeFile(img, it)
        prop.savedFile = it
      } catch (e: IOException) {
        // TODO
        e.printStackTrace()
      }
    }
  }

  /** ドラッグオーバーでファイルを受け取る */
  @FXML
  private fun fileListViewOnDragOver(event: DragEvent) {
    val board = event.getDragboard()
    if (board.hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY)
    }
  }

  /** ドロップでリストに格納 */
  @FXML
  private fun fileListViewOnDragDropped(event: DragEvent) {
    val board = event.getDragboard()
    if (board.hasFiles()) {
      board.files.forEach { imageFiles.add(it) }
      event.setDropCompleted(true)
      event.acceptTransferModes(TransferMode.COPY)
    } else {
      event.setDropCompleted(false)
    }
  }

  /** リストビューの選択が変更された場合に呼び出される。 */
  private fun changeSelection() {
    if (!fileListView.selectionModel.isEmpty) {
      val i = fileListView.selectionModel.selectedIndex
      imageFiles.select(i)
    }
  }

  private fun getSelectedImages(): List<Image> {
    return fileListView.selectionModel.selectedIndices.map {
      imageFiles.select(it)
      cropImage.cropByBufferedImage()
    }
  }

  @FXML
  private fun bulkInsertButtonOnClicked() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images)
  }

  @FXML
  private fun bulkInsert1() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 0)
  }

  @FXML
  private fun bulkInsert2() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 1)
  }

  @FXML
  private fun bulkInsert3() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 2)
  }

  @FXML
  private fun bulkInsert4() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 3)
  }

  @FXML
  private fun bulkInsert5() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 4)
  }

  @FXML
  private fun bulkInsert6() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 5)
  }

  @FXML
  private fun bulkInsert7() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 6)
  }

  @FXML
  private fun bulkInsert8() {
    val images = getSelectedImages()
    @Suppress("MagicNumber")
    tileImage.bulkInsert(images, 7)
  }

  @FXML
  private fun clearButtonOnClicked() {
    imageFiles.clear()
  }

  @FXML
  private fun removeButtonOnClicked() {
    val i = fileListView.selectionModel.selectedIndex
    imageFiles.remove(i)
  }

  @FXML
  private fun clearOutputButtonOnClicked() {
    tileImage.clear()
  }

  // TODO: Bad method name
  @FXML
  private fun focusGridPaneOnMouseDragged(event: MouseEvent) {
    val x = event.getX()
    val y = event.getY()
    cropImage.moveByMouse(x, y)
  }

  private fun getAxis() = cropAxisComboBox.selectionModel.selectedItem.toDouble()

  @FXML
  private fun moveUpCropPosition() {
    val n = getAxis()
    cropImage.moveUp(n)
  }

  @FXML
  private fun moveRightCropPosition() {
    val n = getAxis()
    cropImage.moveRight(n)
  }

  @FXML
  private fun moveDownCropPosition() {
    val n = getAxis()
    cropImage.moveDown(n)
  }

  @FXML
  private fun moveLeftCropPosition() {
    val n = getAxis()
    cropImage.moveLeft(n)
  }

  private fun getScale() = cropScaleComboBox.selectionModel.selectedItem.toDouble()

  @FXML
  private fun scaleUp() {
    val n = getScale()
    cropImage.scaleUp(n)
  }

  @FXML
  private fun scaleDown() {
    val n = getScale()
    cropImage.scaleDown(n)
  }

  @FXML
  private fun setTileImageOnClick(event: MouseEvent) {
    var x = event.getX()
    var y = event.getY()
    var img = cropImage.cropByBufferedImage()
    tileImage.setImageByAxis(img, x, y)
  }

  @FXML
  private fun quit() {
    Platform.exit()
  }

  public fun storeProperties() {
    prop.store()
  }

  @FXML
  private fun setCropSizeWithDialog() {
    val ci =
        cropImage.let {
          val pos = it.croppingPosition
          val stage = CropImageStage(pos.x, pos.y, it.scaleProperty.value)
          stage.showAndWait()
          stage
        }

    if (!ci.getOK()) {
      return
    }

    val x = ci.getParameterX()
    val y = ci.getParameterY()
    val scale = ci.getParameterScale()

    cropImage.move(x, y)
    cropImage.setScale(scale)
  }

  /** 出力画像タイルをタイルの列数、行数、矩形サイズに応じた形に更新する。 */
  private fun resetOutputGridPane() {
    // 子供のLabelを全部削除
    outputGridPane.getChildren().clear()
    // 格子を削除
    outputGridPane.getRowConstraints().clear()
    outputGridPane.getColumnConstraints().clear()

    val selectedImageFormat = imageFormat.selectedImageFormat
    val row = selectedImageFormat.rowProperty.get()
    val col = selectedImageFormat.colProperty.get()
    val width = selectedImageFormat.rectangle.width
    val height = selectedImageFormat.rectangle.height

    // 格子を設定
    outputGridPane.rowConstraints += (0 until row).map { RowConstraints(height) }
    outputGridPane.columnConstraints += (0 until col).map { ColumnConstraints(width) }

    // Labelを配置
    (0 until row).forEach { y ->
      (0 until col).forEach { x ->
        val num = (1 + x + y * col).toString()
        val label = Label(num)
        outputGridPane.add(label, x, y)
      }
    }

    tileImage.resetImage()
  }

  /** 画像フォーマットに基づいて選択可能な画像フォーマットメニューをリセットする */
  private fun resetImageFormatMenu(selectLast: Boolean) {
    imageFormatMenu.items.clear()

    val fmts = imageFormat.createTotalImageFormats()
    val selectIndex = if (selectLast) fmts.size - 1 else 0
    imageFormatMenu.items +=
        (0 until fmts.size).map { i ->
          val fmt = fmts.get(i)
          val item = RadioMenuItem(fmt.name)
          item.toggleGroup = group
          item.setSelected(i == selectIndex)
          item.setOnAction { _ -> imageFormat.select(i) }
          item
        }

    imageFormatMenu.items += SeparatorMenuItem()

    val addButton = MenuItem("画像フォーマットを追加")
    addButton.setOnAction { _ -> addNewImageFormat() }
    imageFormatMenu.items += addButton

    val deleteButton = MenuItem("画像フォーマットを削除")
    deleteButton.setOnAction { _ -> deleteImageFormat() }
    deleteButton.setDisable(!imageFormat.existsDeletableImageFormats())
    imageFormatMenu.items += deleteButton

    imageFormat.select(selectIndex)
  }

  private fun addNewImageFormat() {
    val window = getWindow()
    val stage = ImageFormatStage(window.x, window.y)
    stage.showAndWait()

    if (!stage.getOK()) {
      return
    }

    val fmt = stage.getImageFormat()
    imageFormat.addAdditionalImageFormat(fmt)
    resetImageFormatMenu(true)
    writeImageFormat()
  }

  private fun deleteImageFormat() {
    val deletables = imageFormat.getAdditionalImageFormatNames()
    val defaultDeletable = deletables.get(0)
    val dialog = ChoiceDialog<String>(defaultDeletable, deletables)
    dialog.setHeaderText("削除する画像フォーマットを選択してください")
    dialog.showAndWait().ifPresent { selected ->
      val index = deletables.indexOf(selected)
      imageFormat.deleteAdditionalImageFormat(index)
      resetImageFormatMenu(false)
      writeImageFormat()
    }
  }

  private fun writeImageFormat() {
    try {
      imageFormat.writeXMLFile()
    } catch (e: ParserConfigurationException) {
      e.printStackTrace()
      showAndWaitCommonExceptionDialog("ParserConfigurationException")
    } catch (e: TransformerConfigurationException) {
      e.printStackTrace()
      showAndWaitCommonExceptionDialog("TransformerConfigurationException")
    } catch (e: TransformerException) {
      e.printStackTrace()
      showAndWaitCommonExceptionDialog("TransformerException")
    } catch (e: IOException) {
      e.printStackTrace()
      val exception = "IOException"
      var msg =
          """画像フォーマットファイルの保存に失敗しました。
以下の観点で確認してください。

- configフォルダが存在するか
- 特別なフォルダ (システムフォルダなど)で実行していないか
- 設定ファイルが壊れていないか
"""
      showAndWaitExceptionDialog(exception, msg)
    }
  }

  private fun getWindow() = removeButton.scene.window
}

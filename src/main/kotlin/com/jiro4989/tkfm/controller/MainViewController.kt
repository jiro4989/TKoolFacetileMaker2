package com.jiro4989.tkfm.controller

import com.jiro4989.tkfm.CropImageStage
import com.jiro4989.tkfm.ImageFormatStage
import com.jiro4989.tkfm.model.*
import com.jiro4989.tkfm.util.DialogUtil
import com.jiro4989.tkfm.util.ImageUtil
import java.net.URL
import java.io.File
import java.io.IOException
import java.util.Optional
import java.util.stream.*
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.input.DragEvent
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.*
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

class MainViewController : Initializable {
  // UI parts /////////////////////////////////////////////////////////////////

  // Menu
  @FXML lateinit private var imageFormatMenu: Menu
  private val group = ToggleGroup()

  // List view
  @FXML lateinit private var fileListView: ListView<ImageFileModel>
  @FXML lateinit private var bulkInsertButton: Button
  @FXML lateinit private var clearButton: Button
  @FXML lateinit private var removeButton: Button
  @FXML lateinit private var clearOutputButton: Button

  // Crop view
  @FXML lateinit private var cropImageGridPane: GridPane
  @FXML lateinit private var cropImageView: ImageView
  @FXML lateinit private var croppedGridPane: GridPane
  @FXML lateinit private var croppedImageView: ImageView
  @FXML lateinit private var focusGridPane: GridPane
  @FXML lateinit private var cropXLabel: Label
  @FXML lateinit private var cropYLabel: Label
  @FXML lateinit private var cropScaleLabel: Label
  @FXML lateinit private var cropScaleSlider: Slider
  @FXML lateinit private var cropAxisComboBox: ComboBox<Int>

  private val cropAxisItems: ObservableList<Int> = FXCollections.observableArrayList(1, 5, 10, 25, 50)
  @FXML lateinit private var cropScaleComboBox: ComboBox<Int>
  private val cropScaleItems: ObservableList<Int> = FXCollections.observableArrayList(1, 5, 10, 25, 50)

  // Output view
  @FXML lateinit private var outputGridPane: GridPane
  @FXML lateinit private var outputImageView: ImageView

  // models ///////////////////////////////////////////////////////////////////

  lateinit private var imageFormat: ImageFormatConfigModel
  lateinit private var imageFiles: ImageFilesModel
  lateinit private var cropImage: CroppingImageModel
  lateinit private var tileImage: TileImageModel
  private val prop = PropertiesModel.ChoosedFile()

  override fun initialize(location: URL?, resources: ResourceBundle?) {
    // initialize models
    try {
      imageFormat = ImageFormatConfigModel()
    } catch ( e: ParserConfigurationException) {
      e.printStackTrace()
      DialogUtil.showAndWaitCommonExceptionDialog("ParserConfigurationException")
      Platform.exit()
    } catch ( e: IOException) {
      e.printStackTrace()

      val exception = "IOException"
      val msg = """画像フォーマットファイルの読み込みに失敗しました。
以下の観点で確認してください。

- configフォルダが存在するか
- 特別なフォルダ (システムフォルダなど)で実行していないか
"""
      DialogUtil.showAndWaitExceptionDialog(exception, msg)
      Platform.exit()
    } catch ( e: SAXException) {
      e.printStackTrace()

      val exception = "SAXException"
      val msg = """画像フォーマットファイルの読込に失敗しました。
config/image_format.xmlファイルを手動で書き換えるなどして、
不正なXMLファイルになっている可能性があります。
当該ファイルを削除してアプリケーションを再起動してみてください
"""
      DialogUtil.showAndWaitExceptionDialog(exception, msg)
      Platform.exit()
    }

    val selectedImageFormat = imageFormat.getSelectedImageFormat()
    val rect = selectedImageFormat.getRectangle()

    cropImage = CroppingImageModel(rect)
    imageFiles = ImageFilesModel(cropImage)
    tileImage = TileImageModel(imageFormat)

    // bindigns
    val pos = cropImage.getPosition()
    val rowCount = selectedImageFormat.rowProperty()
    val colCount = selectedImageFormat.colProperty()

    // 行数、列数、タイル幅が変更されたら出力画像ビューをリセットする
    rowCount.addListener { _ -> resetOutputGridPane() }
    colCount.addListener { _ -> resetOutputGridPane() }
    rect.widthProperty().addListener { _ -> resetOutputGridPane() }
    rect.heightProperty().addListener { _ -> resetOutputGridPane() }

    cropImageGridPane
        .prefWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)))
    cropImageGridPane
        .prefHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)))

    Bindings.bindBidirectional(cropImageView.imageProperty(), cropImage.imageProperty())
    cropImageView
        .fitWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)))
    cropImageView
        .fitHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)))

    Bindings.bindBidirectional(croppedGridPane.prefWidthProperty(), rect.widthProperty())
    Bindings.bindBidirectional(croppedGridPane.prefHeightProperty(), rect.heightProperty())
    Bindings.bindBidirectional(croppedImageView.imageProperty(), cropImage.croppedImageProperty())
    Bindings.bindBidirectional(croppedImageView.fitWidthProperty(), rect.widthProperty())
    Bindings.bindBidirectional(croppedImageView.fitHeightProperty(), rect.heightProperty())
    Bindings.bindBidirectional(focusGridPane.layoutXProperty(), pos.xProperty())
    Bindings.bindBidirectional(focusGridPane.layoutYProperty(), pos.yProperty())
    Bindings.bindBidirectional(focusGridPane.prefWidthProperty(), rect.widthProperty())
    Bindings.bindBidirectional(focusGridPane.prefHeightProperty(), rect.heightProperty())
    val cropXConv: StringConverter<Number> = NumberStringConverter()
    Bindings.bindBidirectional(cropXLabel.textProperty(), pos.xProperty(), cropXConv)
    val cropYConv: StringConverter<Number> = NumberStringConverter()
    Bindings.bindBidirectional(cropYLabel.textProperty(), pos.yProperty(), cropYConv)
    val cropScaleConv: StringConverter<Number>  = NumberStringConverter()
    Bindings.bindBidirectional(
        cropScaleLabel.textProperty(), cropImage.scaleProperty(), cropScaleConv)
    Bindings.bindBidirectional(cropScaleSlider.valueProperty(), cropImage.scaleProperty())
    Bindings.bindBidirectional(outputImageView.imageProperty(), tileImage.imageProperty())
    outputGridPane.prefWidthProperty().bind(Bindings.multiply(rect.widthProperty(), colCount))
    outputGridPane.prefHeightProperty().bind(Bindings.multiply(rect.heightProperty(), rowCount))
    outputImageView.fitWidthProperty().bind(Bindings.multiply(rect.widthProperty(), colCount))
    outputImageView.fitHeightProperty().bind(Bindings.multiply(rect.heightProperty(), rowCount))

    // configurations
    fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE)
    fileListView.getSelectionModel().selectedItemProperty().addListener { _ -> changeSelection() }
    fileListView.setItems(imageFiles.getFiles())
    cropAxisComboBox.setItems(cropAxisItems)
    cropAxisComboBox.getSelectionModel().select(1 as Int?)
    cropScaleComboBox.setItems(cropScaleItems)
    cropScaleComboBox.getSelectionModel().select(1 as Int?)
    cropScaleSlider.valueProperty().addListener { _ -> cropImage.move() }

    // properties
    prop.load()

    resetImageFormatMenu(false)
    resetOutputGridPane()
  }

  /** 取り込むファイルを選択する。 */
  @FXML
  private fun openFile() {
    val fc = FileChooser()
    fc.title = "ファイルを開く"
    fc.extensionFilters += ExtensionFilter("Image Files", "*.png")

    prop.getOpenedFile()
        .ifPresent { f ->
              var dir = File(".")
              if (f.isDirectory()) {
                dir = f
              } else if (f.isFile()) {
                dir = f.getParentFile()
              }
              fc.initialDirectory = dir
            }

    val stage = Stage(StageStyle.UTILITY)
    val files: List<File>? = fc.showOpenMultipleDialog(stage)
    files?.let {
      it.forEach { file ->
        imageFiles.add(file)
        prop.setOpenedFile(file)
      }
    }
  }

  /** ファイルを保存する。 ファイル名が存在しなかった場合は別名で保存が呼び出される。 */
  @FXML
  private fun saveFile() {
    prop.savedFile.ifPresent { file ->
      if (file.isFile()) {
        try {
          var img = outputImageView.getImage()
          ImageUtil.writeFile(img, file)
        } catch ( e: IOException) {
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

    prop.savedFile
        .ifPresent{ f ->
              var dir = File(".")
              if (f.isDirectory()) {
                dir = f
              } else if (f.isFile()) {
                dir = f.getParentFile()
              }
              fc.setInitialDirectory(dir)
            }

    val stage = Stage(StageStyle.UTILITY)
    val saveFile: File? = fc.showSaveDialog(stage)
    saveFile?.let { file ->
      try {
        var img = outputImageView.getImage()
        ImageUtil.writeFile(img, file)
        prop.setSavedFile(file)
      } catch ( e: IOException) {
        // TODO
        e.printStackTrace()
      }
    }
  }

  /** ドラッグオーバーでファイルを受け取る */
  @FXML
  private fun fileListViewOnDragOver( event: DragEvent) {
    val board = event.getDragboard()
    if (board.hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY)
    }
  }

  /** ドロップでリストに格納 */
  @FXML
  private fun fileListViewOnDragDropped( event: DragEvent) {
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
    tileImage.bulkInsert(images, 0)
  }

  @FXML
  private fun bulkInsert2() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 1)
  }

  @FXML
  private fun bulkInsert3() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 2)
  }

  @FXML
  private fun bulkInsert4() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 3)
  }

  @FXML
  private fun bulkInsert5() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 4)
  }

  @FXML
  private fun bulkInsert6() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 5)
  }

  @FXML
  private fun bulkInsert7() {
    val images = getSelectedImages()
    tileImage.bulkInsert(images, 6)
  }

  @FXML
  private fun bulkInsert8() {
    val images = getSelectedImages()
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
  private fun focusGridPaneOnMouseDragged( event: MouseEvent) {
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
  private fun setTileImageOnClick( event: MouseEvent) {
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
    val ci = cropImage.let {
      val pos = it.position
      val stage = CropImageStage(pos.x, pos.y, it.scaleProperty().value)
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

    val selectedImageFormat = imageFormat.getSelectedImageFormat()
    val row = selectedImageFormat.rowProperty().get()
    val col = selectedImageFormat.colProperty().get()
    val width = selectedImageFormat.getRectangle().widthProperty().get()
    val height = selectedImageFormat.getRectangle().heightProperty().get()

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
    imageFormatMenu.items += (0 until fmts.size).map { i ->
      val fmt = fmts.get(i)
      val item = RadioMenuItem(fmt.name)
      item.toggleGroup = group
      item.setSelected(i == selectIndex)
      item.setOnAction{ _ -> imageFormat.select(i) }
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
    val stage = ImageFormatStage()
    stage.showAndWait()

    if (!stage.ok) {
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
    dialog
        .showAndWait()
        .ifPresent{ selected ->
              val index = deletables.indexOf(selected)
              imageFormat.deleteAdditionalImageFormat(index)
              resetImageFormatMenu(false)
              writeImageFormat()
            }
  }

  private fun writeImageFormat() {
    try {
      imageFormat.writeXMLFile()
    } catch ( e : ParserConfigurationException) {
      e.printStackTrace()
      DialogUtil.showAndWaitCommonExceptionDialog("ParserConfigurationException")
    } catch ( e : TransformerConfigurationException) {
      e.printStackTrace()
      DialogUtil.showAndWaitCommonExceptionDialog("TransformerConfigurationException")
    } catch ( e: TransformerException) {
      e.printStackTrace()
      DialogUtil.showAndWaitCommonExceptionDialog("TransformerException")
    } catch ( e : IOException) {
      e.printStackTrace()
      val exception = "IOException"
      var msg = """画像フォーマットファイルの保存に失敗しました。
以下の観点で確認してください。

- configフォルダが存在するか
- 特別なフォルダ (システムフォルダなど)で実行していないか
- 設定ファイルが壊れていないか
"""
      DialogUtil.showAndWaitExceptionDialog(exception, msg)
    }
  }
}
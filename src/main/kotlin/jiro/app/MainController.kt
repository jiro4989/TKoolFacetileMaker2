package jiro.app

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import javafx.util.Callback
import jiro.app.dao.load
import jiro.app.data.Point
import jiro.app.model.*
import java.io.File

// 設定ファイルから読み取ったデータ
private var configModel: ConfigModel = load(File("./res/config.xml"))
lateinit var tkoolVersion: VersionModel

class MainController {

    @FXML
    private lateinit var openMenuItem: MenuItem
    @FXML
    private lateinit var saveMenuItem: MenuItem
    @FXML
    private lateinit var saveAsMenuItem: MenuItem
    @FXML
    private lateinit var numberingSaveMenuItem: MenuItem
    @FXML
    private lateinit var numberingSaveAsMenuItem: MenuItem
    @FXML
    private lateinit var optionsMenuItem: MenuItem
    @FXML
    private lateinit var closeMenuItem: MenuItem
    @FXML
    private lateinit var insertMenuItem: MenuItem
    @FXML
    private lateinit var clearMenuItem: MenuItem
    @FXML
    private lateinit var listDeleteMenuItem: MenuItem
    @FXML
    private lateinit var listClearMenuItem: MenuItem
    @FXML
    private lateinit var upMenuItem: MenuItem
    @FXML
    private lateinit var leftMenuItem: MenuItem
    @FXML
    private lateinit var downMenuItem: MenuItem
    @FXML
    private lateinit var rightMenuItem: MenuItem
    @FXML
    private lateinit var zoomOutMenuItem: MenuItem
    @FXML
    private lateinit var zoomInMenuItem: MenuItem
    @FXML
    private lateinit var insertMenuItem1: MenuItem
    @FXML
    private lateinit var insertMenuItem2: MenuItem
    @FXML
    private lateinit var insertMenuItem3: MenuItem
    @FXML
    private lateinit var insertMenuItem4: MenuItem
    @FXML
    private lateinit var insertMenuItem5: MenuItem
    @FXML
    private lateinit var insertMenuItem6: MenuItem
    @FXML
    private lateinit var insertMenuItem7: MenuItem
    @FXML
    private lateinit var insertMenuItem8: MenuItem
    @FXML
    private lateinit var group: ToggleGroup
    @FXML
    private lateinit var mvRadioMenuItem: MenuItem
    @FXML
    private lateinit var vxaceRadioMenuItem: MenuItem
    @FXML
    private lateinit var versionInfoItem: MenuItem
    @FXML
    private lateinit var fileListPane: TitledPane
    @FXML
    private lateinit var imageViewerPane: TitledPane
    @FXML
    private lateinit var outputViewerPane: TitledPane
    @FXML
    private lateinit var clickModeGroup: ToggleGroup

    // 画像ファイルを保持するクラス
    @FXML
    private lateinit var imageFileListView: ListView<File>
    private lateinit var imageFiles: FileListModel

    // トリミング位置を設定するクラス
    @FXML
    private lateinit var selectedImageView: ImageView
    private lateinit var selectedImage: TrimPosManageModel

    @FXML
    private lateinit var zoomRateSlider: Slider

    @FXML
    private lateinit var trimPosXLabel: Label
    @FXML
    private lateinit var trimPosYLabel: Label
    @FXML
    private lateinit var moveWidthComboBox: ComboBox<Double>

    @FXML
    private lateinit var leftShadowRectangle: Rectangle
    @FXML
    private lateinit var topShadowRectangle: Rectangle
    @FXML
    private lateinit var rightShadowRectangle: Rectangle
    @FXML
    private lateinit var bottomShadowRectangle: Rectangle
    @FXML
    private lateinit var overLayerRectangle: Rectangle
    @FXML
    private lateinit var shadowCanvas: Canvas

    // 保存する画像のプレビューを描画するクラス
    @FXML
    private lateinit var outImageTabPane: TabPane
    private lateinit var outImageAnchorPanes: List<OutImageAnchorPane>

    //@FXML
    //private lateinit var outImageView: ImageView
    //private lateinit var outImages: OutImagePreviewModel

    //// 保存する画像に重ねるクリックイベントと外観を制御するクラス
    //@FXML
    //private lateinit var overLayerCanvas: Canvas

    private lateinit var mainService: MainService

    @FXML
    private fun initialize() {
        // 外部から読み出される設定値を更新
        tkoolVersion = configModel.versions[2]

        // ツクールのバージョン分追加する
        outImageAnchorPanes = configModel.versions.map { OutImageAnchorPane(it) }
        val tabs = configModel.versions.mapIndexed { index, item ->
            val tab = Tab(item.name)
            tab.content = outImageAnchorPanes[index]
            tab.isClosable = false
            tab
        }
        outImageTabPane.tabs.setAll(tabs)

        imageFiles = FileListModel(imageFileListView)
        selectedImage = TrimPosManageModel(selectedImageView, moveWidthComboBox, zoomRateSlider, shadowCanvas, trimPosXLabel, trimPosYLabel)

        imageFileListView.items = imageFiles.files
        imageFileListView.selectionModel.selectionMode = SelectionMode.MULTIPLE
        imageFileListView.selectionModel.selectedItemProperty().addListener { e ->
            val selectedItem = imageFileListView.selectionModel.selectedItem
            selectedItem?.absolutePath?.let { selectedImage.setImage(it) }
        }
        imageFileListView.cellFactory = Callback<ListView<File>, ListCell<File>> {
            object : ListCell<File>() {
                override fun updateItem(item: File?, empty: Boolean) {
                    super.updateItem(item, empty)
                    item?.name?.let { text = item.name }
                }
            }
        }
    }

    @FXML
    private fun openMenuItemOnAction(actionEvent: ActionEvent) {
        val files = mainService.open()
        imageFiles.add(files)
    }

    @FXML
    private fun imageFileListViewOnDragOver(dragEvent: DragEvent) {
        val board = dragEvent.dragboard
        if (board.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY)
        }
    }

    @FXML
    private fun imageFileListViewOnDragDropped(dragEvent: DragEvent) {
        val board = dragEvent.dragboard
        if (board.hasFiles()) {
            imageFiles.add(board.files)
            dragEvent.isDropCompleted = true
            dragEvent.acceptTransferModes(TransferMode.COPY)
            return
        }
        dragEvent.isDropCompleted = false
    }

    /**
     * ImageVIew上でマウスドラッグするとトリミング位置が変動する。
     */
    fun shadowCanvasOnMouseDragged(mouseEvent: MouseEvent) {
        val x = mouseEvent.x
        val y = mouseEvent.y
        selectedImage.setTrimPointOnMouseDragged(Point(x, y))
    }

    /**
     * 画像を上書き保存する。
     */
    fun saveMenuItemOnAction(actionEvent: ActionEvent) {
        mainService.save()
    }

    /**
     * 画像を別名保存する。
     */
    fun saveAsMenuItemOnAction() {
        mainService.saveAs()
    }

    /**
     * 画像をタイルペインに追加する。
     */
    fun addImagesButtonOnAction(actionEvent: ActionEvent) {
        val files = imageFiles.getSelectedItems()
        val trimmedImages = selectedImage.getTrimmedImages(files)
        //outImages.setImages(images = trimmedImages)
    }

    /**
     * タイルペインの画像を初期化する。
     */
    fun clearImagesButtonOnAction(actionEvent: ActionEvent) {
        //outImages.clear()
    }

    /**
     * 画像をセットするステートに切り替えるイベント
     */
    fun setModeRadioButtonOnAction(actionEvent: ActionEvent) {
        //outImages.changeClickModeToSetImage()
    }

    /**
     * 画像を削除するステートに切り替えるイベント
     */
    fun deleteModeRadioButtonOnAction(actionEvent: ActionEvent) {
        //outImages.changeClickModeToDeleteImage()
    }

    /**
     * 選択した画像の位置を交換するステートに切り替えるイベント。
     */
    fun swapModeRadioButtonOnAction(actionEvent: ActionEvent) {
        //outImages.changeClickModeToSwapImage()
    }

    /**
     * 選択した画像を左右反転するステートに切り替えるイベント
     */
    fun flipModeRadioButtonOnAction(actionEvent: ActionEvent) {
        //outImages.changeClickModeToFlipImage()
    }

    fun deleteListButtonOnAction(actionEvent: ActionEvent) {
        imageFiles.deleteSelectedItems()
    }

    fun clearListButtonOnAction(actionEvent: ActionEvent) {
        imageFiles.clear()
    }

    fun upFileButtonOnAction(actionEvent: ActionEvent) {
        imageFiles.swapUp()
    }

    fun downFileButtonOnAction(actionEvent: ActionEvent) {
        imageFiles.swapDown()
    }

    fun overLayerCanvasOnMouseClicked(mouseEvent: MouseEvent) {
        outImageViewOnMouseClicked(mouseEvent)
    }

    fun outImageViewOnMouseClicked(mouseEvent: MouseEvent) {
        val selectedItems = imageFiles.getSelectedItems()
        val images = selectedImage.getTrimmedImages(selectedItems)
        //outImages.onMouseClicked(mouseEvent, images)
    }

    /**
     * トリミング画像の拡大率の制御
     */
    fun zoomRateSliderOnMouseDragged(mouseEvent: MouseEvent) {
        selectedImage.updateZoomRate()
    }

    /**
     * トリミング画像の拡大率の制御
     */
    fun zoomRateSliderOnScroll(scrollEvent: ScrollEvent) {

    }

    /**
     * トリミング位置の移動
     */
    fun moveLeftButtonOnAction(actionEvent: ActionEvent) {
        selectedImage.moveLeftTrimPos()
    }

    /**
     * トリミング位置の移動
     */
    fun moveUpButtonOnAction(actionEvent: ActionEvent) {
        selectedImage.moveUpTrimPos()
    }

    /**
     * トリミング位置の移動
     */
    fun moveDownftButtonOnAction(actionEvent: ActionEvent) {
        selectedImage.moveDownTrimPos()
    }

    /**
     * トリミング位置の移動
     */
    fun moveRightButtonOnAction(actionEvent: ActionEvent) {
        selectedImage.moveRightTrimPos()
    }

    /**
     * ツクールのバージョンを変更。
     * トリミング範囲や画像の規格を更新する
     */
    private fun updateTkoolVersion() {

    }

}


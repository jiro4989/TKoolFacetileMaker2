package com.jiro4989.tkfm;

import com.jiro4989.tkfm.data.CropSize;
import com.jiro4989.tkfm.model.*;
import com.jiro4989.tkfm.util.ImageUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController {
  private Main main;

  // List view
  @FXML private ListView<ImageFileModel> fileListView;
  @FXML private Button bulkInsertButton;
  @FXML private Button clearButton;
  @FXML private Button removeButton;
  @FXML private Button clearOutputButton;

  // Crop view
  @FXML private GridPane cropImageGridPane;
  @FXML private ImageView cropImageView;
  @FXML private GridPane croppedGridPane;
  @FXML private ImageView croppedImageView;
  @FXML private GridPane focusGridPane;
  @FXML private Label cropXLabel;
  @FXML private Label cropYLabel;
  @FXML private Label cropScaleLabel;
  @FXML private Slider cropScaleSlider;
  @FXML private ComboBox<Integer> cropAxisComboBox;
  private ObservableList<Integer> cropAxisItems =
      FXCollections.observableArrayList(1, 5, 10, 25, 50);

  @FXML private ComboBox<Integer> cropScaleComboBox;
  private ObservableList<Integer> cropScaleItems =
      FXCollections.observableArrayList(1, 5, 10, 25, 50);

  // Output view
  @FXML private ImageView outputImageView;

  // **************************************************
  // ファイル
  // **************************************************
  @FXML private MenuItem optionsMenuItem;
  @FXML private MenuItem closeMenuItem;

  // **************************************************
  // ツクールバージョン
  // **************************************************
  @FXML private ToggleGroup group;

  private ImageFilesModel imageFiles;
  private CroppingImageModel cropImage;
  private TileImageModel tileImage;

  @FXML
  private void initialize() {
    // TODO
    closeMenuItem.setOnAction(e -> makePropertiesFile());

    cropAxisComboBox.setItems(cropAxisItems);
    cropAxisComboBox.getSelectionModel().select(1);
    cropScaleComboBox.setItems(cropScaleItems);
    cropScaleComboBox.getSelectionModel().select(1);

    // initialize models
    cropImage = new CroppingImageModel();
    imageFiles = new ImageFilesModel(cropImage);
    tileImage = new TileImageModel(cropImage.getRectangle());

    // bindigns
    var pos = cropImage.getPosition();
    var rect = cropImage.getRectangle();
    // Bindings.bindBidirectional(cropXLabel.textProperty(), pos.xProperty());

    cropImageGridPane
        .prefWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)));
    cropImageGridPane
        .prefHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)));

    Bindings.bindBidirectional(cropImageView.imageProperty(), cropImage.imageProperty());
    cropImageView
        .fitWidthProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageWidthProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)));
    cropImageView
        .fitHeightProperty()
        .bind(
            Bindings.multiply(
                cropImage.imageHeightProperty(),
                Bindings.divide(cropScaleSlider.valueProperty(), 100)));

    Bindings.bindBidirectional(croppedGridPane.prefWidthProperty(), rect.widthProperty());
    Bindings.bindBidirectional(croppedGridPane.prefHeightProperty(), rect.heightProperty());
    Bindings.bindBidirectional(croppedImageView.imageProperty(), cropImage.croppedImageProperty());
    Bindings.bindBidirectional(croppedImageView.fitWidthProperty(), rect.widthProperty());
    Bindings.bindBidirectional(croppedImageView.fitHeightProperty(), rect.heightProperty());
    Bindings.bindBidirectional(focusGridPane.layoutXProperty(), pos.xProperty());
    Bindings.bindBidirectional(focusGridPane.layoutYProperty(), pos.yProperty());
    Bindings.bindBidirectional(focusGridPane.prefWidthProperty(), rect.widthProperty());
    Bindings.bindBidirectional(focusGridPane.prefHeightProperty(), rect.heightProperty());
    Bindings.bindBidirectional(cropScaleSlider.valueProperty(), cropImage.scaleProperty());
    Bindings.bindBidirectional(outputImageView.imageProperty(), tileImage.imageProperty());
    outputImageView.fitWidthProperty().bind(Bindings.multiply(rect.widthProperty(), 4));
    outputImageView.fitHeightProperty().bind(Bindings.multiply(rect.heightProperty(), 2));

    // configurations
    fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fileListView.getSelectionModel().selectedItemProperty().addListener(e -> changeSelection());
    fileListView.setItems(imageFiles.getFiles());
  }

  /** 取り込むファイルを選択する。 */
  @FXML
  private void openFile() {
    Stage stage = new Stage(StageStyle.UTILITY);
    FileChooser fc = new FileChooser();
    fc.setTitle("ファイルを開く");
    fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));

    File dir = new File(".");
    dir = dir.exists() ? dir : new File(".");
    fc.setInitialDirectory(dir);

    List<File> files = fc.showOpenMultipleDialog(stage);
    Optional<List<File>> filesOpt = Optional.ofNullable(files);
    filesOpt.ifPresent(
        list -> {
          list.stream().forEach(f -> imageFiles.add(f));
        });
  }

  /** ファイルを保存する。 ファイル名が存在しなかった場合は別名で保存が呼び出される。 */
  @FXML
  private void saveFile() {}

  /** 別名で保存する。 */
  @FXML
  private void saveAsFile() {
    FileChooser fc = new FileChooser();
    fc.setTitle("名前をつけて保存");
    fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));

    File dir = new File(".");
    dir = dir.exists() ? dir : new File(".");
    fc.setInitialDirectory(dir);

    Stage stage = new Stage(StageStyle.UTILITY);
    var savedFileOpt = Optional.ofNullable(fc.showSaveDialog(stage));
    savedFileOpt.ifPresent(
        file -> {
          try {
            var img = outputImageView.getImage();
            ImageUtil.writeFile(img, file);
          } catch (IOException e) {
            // TODO
            e.printStackTrace();
          }
        });
  }

  /**
   * ImageViewerクラスにリストの選択中のファイルパスを送る。
   *
   * @param filePath
   */
  public void sendFileName(String filePath) {
    // imageViewerBorderPaneController.setImage(filePath);
  }

  public void setMain(Main aMain) {
    main = aMain;
  }

  public void clearImageView() {
    // imageViewerBorderPaneController.clearImageView();
  }

  /** プロパティファイルを書き出す。 呼び出し元はMainクラスで、ウィンドウを閉じるときに呼び出される。 */
  public void makePropertiesFile() {
    // String[] values = new String[KEYS.length];
    // values[0] = String.valueOf(options.getSeparatorSwitch());
    // values[1] = options.getSeparator().name();
    // values[2] = options.getNumbering().name();
    // values[3] = String.valueOf(options.getFontSize());
    // IntStream.range(0, KEYS.length).forEach(i -> prop.setValue(KEYS[i], values[i]));
    // prop.write();

    main.closeAction();
  }

  /** ドラッグオーバーでファイルを受け取る */
  @FXML
  private void fileListViewOnDragOver(DragEvent event) {
    Dragboard board = event.getDragboard();
    if (board.hasFiles()) {
      event.acceptTransferModes(TransferMode.COPY);
    }
  }

  /** ドロップでリストに格納 */
  @FXML
  private void fileListViewOnDragDropped(DragEvent event) {
    Dragboard board = event.getDragboard();
    if (board.hasFiles()) {
      List<File> list = board.getFiles();
      list.stream().forEach(f -> imageFiles.add(f));
      event.setDropCompleted(true);
      event.acceptTransferModes(TransferMode.COPY);
    } else {
      event.setDropCompleted(false);
    }
  }

  /** リストビューの選択が変更された場合に呼び出される。 */
  private void changeSelection() {
    if (!fileListView.getSelectionModel().isEmpty()) {
      int i = fileListView.getSelectionModel().getSelectedIndex();
      imageFiles.select(i);
    }
  }

  private List<Image> getSelectedImages() {
    return fileListView
        .getSelectionModel()
        .getSelectedIndices()
        .stream()
        .map(
            i -> {
              imageFiles.select(i);
              return cropImage.cropByBufferedImage();
            })
        .collect(Collectors.toList());
  }

  @FXML
  private void bulkInsertButtonOnClicked() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images);
  }

  @FXML
  private void bulkInsert1() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 0);
  }

  @FXML
  private void bulkInsert2() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 1);
  }

  @FXML
  private void bulkInsert3() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 2);
  }

  @FXML
  private void bulkInsert4() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 3);
  }

  @FXML
  private void bulkInsert5() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 4);
  }

  @FXML
  private void bulkInsert6() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 5);
  }

  @FXML
  private void bulkInsert7() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 6);
  }

  @FXML
  private void bulkInsert8() {
    var images = getSelectedImages();
    tileImage.bulkInsert(images, 7);
  }

  @FXML
  private void clearButtonOnClicked() {
    imageFiles.clear();
  }

  @FXML
  private void removeButtonOnClicked() {
    var i = fileListView.getSelectionModel().getSelectedIndex();
    imageFiles.remove(i);
  }

  @FXML
  private void clearOutputButtonOnClicked() {
    tileImage.clear();
  }

  // TODO: Bad method name
  @FXML
  private void focusGridPaneOnMouseDragged(MouseEvent event) {
    double x = event.getX();
    double y = event.getY();
    cropImage.moveByMouse(x, y);
  }

  @FXML
  private void moveUpCropPosition() {
    double n = cropAxisComboBox.getSelectionModel().getSelectedItem();
    cropImage.moveUp(n);
  }

  @FXML
  private void moveRightCropPosition() {
    double n = cropAxisComboBox.getSelectionModel().getSelectedItem();
    cropImage.moveRight(n);
  }

  @FXML
  private void moveDownCropPosition() {
    double n = cropAxisComboBox.getSelectionModel().getSelectedItem();
    cropImage.moveDown(n);
  }

  @FXML
  private void moveLeftCropPosition() {
    double n = cropAxisComboBox.getSelectionModel().getSelectedItem();
    cropImage.moveLeft(n);
  }

  @FXML
  private void scaleUp() {
    double n = cropScaleComboBox.getSelectionModel().getSelectedItem();
    cropImage.scaleUp(n);
  }

  @FXML
  private void scaleDown() {
    double n = cropScaleComboBox.getSelectionModel().getSelectedItem();
    cropImage.scaleDown(n);
  }

  @FXML
  private void setCropSizeTkoolMV() {
    var rect = cropImage.getRectangle();
    rect.setWidth(CropSize.TKOOL_MV_WIDHT);
    rect.setHeight(CropSize.TKOOL_MV_HEIGHT);
    tileImage.resetImage();
  }

  @FXML
  private void setCropSizeTkoolVXACE() {
    var rect = cropImage.getRectangle();
    rect.setWidth(CropSize.TKOOL_VXACE_WIDHT);
    rect.setHeight(CropSize.TKOOL_VXACE_HEIGHT);
    tileImage.resetImage();
  }

  @FXML
  private void setTileImageOnClick(MouseEvent event) {
    var x = event.getX();
    var y = event.getY();
    var img = cropImage.cropByBufferedImage();
    tileImage.setImageByAxis(img, x, y);
  }
}

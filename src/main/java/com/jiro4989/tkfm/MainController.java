package com.jiro4989.tkfm;

import com.jiro4989.tkfm.model.*;
import com.jiro4989.tkfm.util.DialogUtil;
import com.jiro4989.tkfm.util.ImageUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.*;
import javafx.application.Platform;
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
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class MainController {
  // UI parts /////////////////////////////////////////////////////////////////

  // Menu
  @FXML private Menu imageFormatMenu;
  private ToggleGroup group = new ToggleGroup();

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
  @FXML private GridPane outputGridPane;
  @FXML private ImageView outputImageView;

  // models ///////////////////////////////////////////////////////////////////

  private ImageFormatConfigModel imageFormat;
  private ImageFilesModel imageFiles;
  private CroppingImageModel cropImage;
  private TileImageModel tileImage;
  private PropertiesModel.ChoosedFile prop = new PropertiesModel.ChoosedFile();

  @FXML
  private void initialize() {
    // initialize models
    try {
      imageFormat = new ImageFormatConfigModel();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      DialogUtil.showAndWaitCommonExceptionDialog("ParserConfigurationException");
      Platform.exit();
    } catch (IOException e) {
      e.printStackTrace();

      var exception = "IOException";
      var msg =
          "画像フォーマットファイルの読み込みに失敗しました。\n"
              + "以下の観点で確認してください。\n\n"
              + "- configフォルダが存在するか\n"
              + "- 特別なフォルダ (システムフォルダなど)で実行していないか";
      DialogUtil.showAndWaitExceptionDialog(exception, msg);
      Platform.exit();
    } catch (SAXException e) {
      e.printStackTrace();

      var exception = "SAXException";
      var msg =
          "画像フォーマットファイルの読込に失敗しました。\n"
              + "config/image_format.xmlファイルを手動で書き換えるなどして、"
              + "不正なXMLファイルになっている可能性があります。"
              + "当該ファイルを削除してアプリケーションを再起動してみてください";
      DialogUtil.showAndWaitExceptionDialog(exception, msg);
      Platform.exit();
    }

    var selectedImageFormat = imageFormat.getSelectedImageFormat();
    var rect = selectedImageFormat.getRectangle();

    cropImage = new CroppingImageModel(rect);
    imageFiles = new ImageFilesModel(cropImage);
    tileImage = new TileImageModel(imageFormat);

    // bindigns
    var pos = cropImage.getPosition();
    var rowCount = selectedImageFormat.rowProperty();
    var colCount = selectedImageFormat.colProperty();

    // 行数、列数、タイル幅が変更されたら出力画像ビューをリセットする
    rowCount.addListener(e -> resetOutputGridPane());
    colCount.addListener(e -> resetOutputGridPane());
    rect.widthProperty().addListener(e -> resetOutputGridPane());
    rect.heightProperty().addListener(e -> resetOutputGridPane());

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
    StringConverter<Number> cropXConv = new NumberStringConverter();
    Bindings.bindBidirectional(cropXLabel.textProperty(), pos.xProperty(), cropXConv);
    StringConverter<Number> cropYConv = new NumberStringConverter();
    Bindings.bindBidirectional(cropYLabel.textProperty(), pos.yProperty(), cropYConv);
    StringConverter<Number> cropScaleConv = new NumberStringConverter();
    Bindings.bindBidirectional(
        cropScaleLabel.textProperty(), cropImage.scaleProperty(), cropScaleConv);
    Bindings.bindBidirectional(cropScaleSlider.valueProperty(), cropImage.scaleProperty());
    Bindings.bindBidirectional(outputImageView.imageProperty(), tileImage.imageProperty());
    outputGridPane.prefWidthProperty().bind(Bindings.multiply(rect.widthProperty(), colCount));
    outputGridPane.prefHeightProperty().bind(Bindings.multiply(rect.heightProperty(), rowCount));
    outputImageView.fitWidthProperty().bind(Bindings.multiply(rect.widthProperty(), colCount));
    outputImageView.fitHeightProperty().bind(Bindings.multiply(rect.heightProperty(), rowCount));

    // configurations
    fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fileListView.getSelectionModel().selectedItemProperty().addListener(e -> changeSelection());
    fileListView.setItems(imageFiles.getFiles());
    cropAxisComboBox.setItems(cropAxisItems);
    cropAxisComboBox.getSelectionModel().select(1);
    cropScaleComboBox.setItems(cropScaleItems);
    cropScaleComboBox.getSelectionModel().select(1);
    cropScaleSlider.valueProperty().addListener(e -> cropImage.move());

    // properties
    prop.load();

    resetImageFormatMenu(false);
    resetOutputGridPane();
  }

  /** 取り込むファイルを選択する。 */
  @FXML
  private void openFile() {
    FileChooser fc = new FileChooser();
    fc.setTitle("ファイルを開く");
    fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));

    prop.getOpenedFile()
        .ifPresent(
            f -> {
              File dir = new File(".");
              if (f.isDirectory()) {
                dir = f;
              } else if (f.isFile()) {
                dir = f.getParentFile();
              }
              fc.setInitialDirectory(dir);
            });

    Stage stage = new Stage(StageStyle.UTILITY);
    List<File> files = fc.showOpenMultipleDialog(stage);
    Optional<List<File>> filesOpt = Optional.ofNullable(files);
    filesOpt.ifPresent(
        list -> {
          list.stream()
              .forEach(
                  file -> {
                    imageFiles.add(file);
                    prop.setOpenedFile(file);
                  });
        });
  }

  /** ファイルを保存する。 ファイル名が存在しなかった場合は別名で保存が呼び出される。 */
  @FXML
  private void saveFile() {
    prop.getSavedFile()
        .ifPresent(
            file -> {
              if (file.isFile()) {
                try {
                  var img = outputImageView.getImage();
                  ImageUtil.writeFile(img, file);
                } catch (IOException e) {
                  // TODO
                  e.printStackTrace();
                }
                return;
              }

              saveAsFile();
            });
  }

  /** 別名で保存する。 */
  @FXML
  private void saveAsFile() {
    FileChooser fc = new FileChooser();
    fc.setTitle("名前をつけて保存");
    fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));

    prop.getSavedFile()
        .ifPresent(
            f -> {
              File dir = new File(".");
              if (f.isDirectory()) {
                dir = f;
              } else if (f.isFile()) {
                dir = f.getParentFile();
              }
              fc.setInitialDirectory(dir);
            });

    Stage stage = new Stage(StageStyle.UTILITY);
    var savedFileOpt = Optional.ofNullable(fc.showSaveDialog(stage));
    savedFileOpt.ifPresent(
        file -> {
          try {
            var img = outputImageView.getImage();
            ImageUtil.writeFile(img, file);
            prop.setSavedFile(file);
          } catch (IOException e) {
            // TODO
            e.printStackTrace();
          }
        });
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
    return fileListView.getSelectionModel().getSelectedIndices().stream()
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
  private void setTileImageOnClick(MouseEvent event) {
    var x = event.getX();
    var y = event.getY();
    var img = cropImage.cropByBufferedImage();
    tileImage.setImageByAxis(img, x, y);
  }

  @FXML
  private void quit() {
    Platform.exit();
  }

  public void storeProperties() {
    prop.store();
  }

  @FXML
  private void setCropSizeWithDialog() {
    var position = cropImage.getPosition();
    var x = position.getX();
    var y = position.getY();
    var scale = cropImage.scaleProperty().getValue();

    CropImage ci = new CropImage(x, y, scale);
    ci.showAndWait();

    if (!ci.getOK()) {
      return;
    }

    x = ci.getParameterX();
    y = ci.getParameterY();
    scale = ci.getParameterScale();

    cropImage.move(x, y);
    cropImage.setScale(scale);
  }

  /** 出力画像タイルをタイルの列数、行数、矩形サイズに応じた形に更新する。 */
  private void resetOutputGridPane() {
    // 子供のLabelを全部削除
    outputGridPane.getChildren().clear();
    // 格子を削除
    outputGridPane.getRowConstraints().clear();
    outputGridPane.getColumnConstraints().clear();

    var selectedImageFormat = imageFormat.getSelectedImageFormat();
    var row = selectedImageFormat.rowProperty().get();
    var col = selectedImageFormat.colProperty().get();
    var width = selectedImageFormat.getRectangle().widthProperty().get();
    var height = selectedImageFormat.getRectangle().heightProperty().get();

    // 格子を設定
    for (var i = 0; i < row; i++) {
      RowConstraints c = new RowConstraints(height);
      outputGridPane.getRowConstraints().add(c);
    }
    for (var i = 0; i < col; i++) {
      ColumnConstraints c = new ColumnConstraints(width);
      outputGridPane.getColumnConstraints().add(c);
    }

    // Labelを配置
    for (var y = 0; y < row; y++) {
      for (var x = 0; x < col; x++) {
        var num = "" + (1 + x + y * col);
        var label = new Label(num);
        outputGridPane.add(label, x, y);
      }
    }

    tileImage.resetImage();
  }

  /** 画像フォーマットに基づいて選択可能な画像フォーマットメニューをリセットする */
  private void resetImageFormatMenu(boolean selectLast) {
    imageFormatMenu.getItems().clear();

    var fmts = imageFormat.createTotalImageFormats();
    var selectIndex = selectLast ? fmts.size() - 1 : 0;
    for (var i = 0; i < fmts.size(); i++) {
      // final変数じゃないとsetOnActionの無名関数に渡せないため
      final var index = i;

      var fmt = fmts.get(index);
      var item = new RadioMenuItem(fmt.getName());
      item.setToggleGroup(group);
      item.setSelected(index == selectIndex);
      item.setOnAction(e -> imageFormat.select(index));
      imageFormatMenu.getItems().add(item);
    }
    var sep = new SeparatorMenuItem();
    imageFormatMenu.getItems().add(sep);

    var addButton = new MenuItem("画像フォーマットを追加");
    addButton.setOnAction(e -> addNewImageFormat());
    imageFormatMenu.getItems().add(addButton);

    var deleteButton = new MenuItem("画像フォーマットを削除");
    deleteButton.setOnAction(e -> deleteImageFormat());
    deleteButton.setDisable(!imageFormat.existsDeletableImageFormats());
    imageFormatMenu.getItems().add(deleteButton);

    imageFormat.select(selectIndex);
  }

  private void addNewImageFormat() {
    var stage = new ImageFormatStage();
    stage.showAndWait();

    if (!stage.getOK()) {
      return;
    }

    var fmt = stage.getImageFormat();
    imageFormat.addAdditionalImageFormat(fmt);
    resetImageFormatMenu(true);
    writeImageFormat();
  }

  private void deleteImageFormat() {
    var deletables = imageFormat.getAdditionalImageFormatNames();
    var defaultDeletable = deletables.get(0);
    var dialog = new ChoiceDialog<>(defaultDeletable, deletables);
    dialog
        .showAndWait()
        .ifPresent(
            selected -> {
              var index = deletables.indexOf(selected);
              imageFormat.deleteAdditionalImageFormat(index);
              resetImageFormatMenu(false);
              writeImageFormat();
            });
  }

  private void writeImageFormat() {
    try {
      imageFormat.writeXMLFile();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      DialogUtil.showAndWaitCommonExceptionDialog("ParserConfigurationException");
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
      DialogUtil.showAndWaitCommonExceptionDialog("TransformerConfigurationException");
    } catch (TransformerException e) {
      e.printStackTrace();
      DialogUtil.showAndWaitCommonExceptionDialog("TransformerException");
    } catch (IOException e) {
      e.printStackTrace();
      var exception = "IOException";
      var msg =
          "画像フォーマットファイルの保存に失敗しました。\n"
              + "以下の観点で確認してください。\n\n"
              + "- configフォルダが存在するか\n"
              + "- 特別なフォルダ (システムフォルダなど)で実行していないか\n"
              + "- 設定ファイルが壊れていないか";
      DialogUtil.showAndWaitExceptionDialog(exception, msg);
    }
  }
}

package com.jiro4989.tkfm;

import com.jiro4989.tkfm.model.*;
import com.jiro4989.tkfm.options.Numberings;
import com.jiro4989.tkfm.options.Options;
import com.jiro4989.tkfm.options.OptionsStage;
import com.jiro4989.tkfm.options.Separators;
import com.jiro4989.tkfm.version.VersionStage;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.*;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
  private Options options;

  private TKoolVersion version = TKoolVersion.MV;

  private Optional<File> savedFileOpt = Optional.empty();
  private String openedFileName = "";
  private String openedDirPath = "";
  private String savedFileName = "";
  private String savedDirPath = "";
  private final String OUTPUT_DIR = "." + File.separator + "output" + File.separator;
  private String numberingFileName = "";
  private String numberingInitialFileName = "";
  private String initialSavedFileName = "";

  private static final String[] KEYS = {
    "separator_switch",
    "separator",
    "numberings",
    "font_size",
    "tkool_version",
    "opened_file_name",
    "opened_dir_path",
    "saved_file_name",
    "saved_dir_path",
    "numbering_file_name"
  };
  private static final String[] INITIAL_VALUES = {
    "false", "UNDER_SCORE", "NUMBERING01", "12", "MV", "", ".", "", ".", "MyActor.png"
  };
  private PropertiesHandler prop = new PropertiesHandler("options", KEYS, INITIAL_VALUES);

  @FXML private ListView<ImageFileModel> fileListView;
  @FXML private Button bulkInsertButton;
  @FXML private Button clearButton;
  @FXML private Button removeButton;
  @FXML private Button clearOutputButton;
  @FXML private GridPane cropImageGridPane;
  @FXML private ImageView cropImageView;
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
  @FXML private ImageView outputImageView;

  // **************************************************
  // ファイル
  // **************************************************
  @FXML private MenuItem openMenuItem;
  @FXML private MenuItem saveMenuItem;
  @FXML private MenuItem saveAsMenuItem;
  @FXML private MenuItem numberingSaveMenuItem;
  @FXML private MenuItem numberingSaveAsMenuItem;
  @FXML private MenuItem optionsMenuItem;
  @FXML private MenuItem closeMenuItem;

  // **************************************************
  // イメージビュー
  // **************************************************
  @FXML private MenuItem upMenuItem;
  @FXML private MenuItem leftMenuItem;
  @FXML private MenuItem downMenuItem;
  @FXML private MenuItem rightMenuItem;
  @FXML private MenuItem zoomOutMenuItem;
  @FXML private MenuItem zoomInMenuItem;

  // **************************************************
  // 出力画像ビュー
  // **************************************************
  @FXML private MenuItem insertMenuItem1;
  @FXML private MenuItem insertMenuItem2;
  @FXML private MenuItem insertMenuItem3;
  @FXML private MenuItem insertMenuItem4;
  @FXML private MenuItem insertMenuItem5;
  @FXML private MenuItem insertMenuItem6;
  @FXML private MenuItem insertMenuItem7;
  @FXML private MenuItem insertMenuItem8;

  // **************************************************
  // ツクールバージョン
  // **************************************************
  @FXML private ToggleGroup group;
  @FXML private MenuItem mvRadioMenuItem;
  @FXML private MenuItem vxaceRadioMenuItem;

  // **************************************************
  // ヘルプ
  // **************************************************
  @FXML private MenuItem versionInfoItem;

  private ImageFilesModel imageFiles;
  private CroppingImageModel cropImage;
  private TileImageModel tileImage;

  @FXML
  private void initialize() {
    // TODO
    openMenuItem.setOnAction(e -> openFile());
    saveMenuItem.setOnAction(e -> saveFile());
    saveAsMenuItem.setOnAction(e -> saveAsFile());
    numberingSaveMenuItem.setOnAction(e -> numberingSaveFile());
    numberingSaveAsMenuItem.setOnAction(e -> numberingSaveAsFile());
    optionsMenuItem.setOnAction(e -> openOptionsWindow());
    closeMenuItem.setOnAction(e -> makePropertiesFile());

    // upMenuItem.setOnAction(e -> imageViewerBorderPaneController.moveUp());
    // leftMenuItem.setOnAction(e -> imageViewerBorderPaneController.moveLeft());
    // downMenuItem.setOnAction(e -> imageViewerBorderPaneController.moveDown());
    // rightMenuItem.setOnAction(e -> imageViewerBorderPaneController.moveRight());
    // zoomInMenuItem.setOnAction(e -> imageViewerBorderPaneController.zoomIn());
    // zoomOutMenuItem.setOnAction(e -> imageViewerBorderPaneController.zoomOut());

    // insertMenuItem1.setOnAction(e -> fileListHBoxController.insertImages(0));
    // insertMenuItem2.setOnAction(e -> fileListHBoxController.insertImages(1));
    // insertMenuItem3.setOnAction(e -> fileListHBoxController.insertImages(2));
    // insertMenuItem4.setOnAction(e -> fileListHBoxController.insertImages(3));
    // insertMenuItem5.setOnAction(e -> fileListHBoxController.insertImages(4));
    // insertMenuItem6.setOnAction(e -> fileListHBoxController.insertImages(5));
    // insertMenuItem7.setOnAction(e -> fileListHBoxController.insertImages(6));
    // insertMenuItem8.setOnAction(e -> fileListHBoxController.insertImages(7));

    versionInfoItem.setOnAction(e -> openVersionWindow());

    mvRadioMenuItem.setOnAction(e -> changeTKoolVersion(TKoolVersion.MV));
    vxaceRadioMenuItem.setOnAction(e -> changeTKoolVersion(TKoolVersion.VXACE));

    // initialize models
    cropImage = new CroppingImageModel();
    imageFiles = new ImageFilesModel(cropImage);
    tileImage = new TileImageModel();

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

    Bindings.bindBidirectional(croppedImageView.imageProperty(), cropImage.croppedImageProperty());
    Bindings.bindBidirectional(focusGridPane.layoutXProperty(), pos.xProperty());
    Bindings.bindBidirectional(focusGridPane.layoutYProperty(), pos.yProperty());
    Bindings.bindBidirectional(focusGridPane.prefWidthProperty(), rect.widthProperty());
    Bindings.bindBidirectional(focusGridPane.prefHeightProperty(), rect.heightProperty());
    Bindings.bindBidirectional(cropScaleSlider.valueProperty(), cropImage.scaleProperty());
    Bindings.bindBidirectional(outputImageView.imageProperty(), tileImage.imageProperty());

    prop.load();
    options =
        new Options(
            Boolean.valueOf(prop.getValue(KEYS[0])),
            Separators.getMatchedConstant(prop.getValue(KEYS[1])),
            Numberings.getMatchedConstant(prop.getValue(KEYS[2])),
            Integer.valueOf(prop.getValue(KEYS[3])));
    version = TKoolVersion.getMatchedConstant(prop.getValue(KEYS[4]));
    int index = version.ordinal();
    Toggle toggle = group.getToggles().get(index);
    group.selectToggle(toggle);

    changeTKoolVersion(version);
    openedFileName = prop.getValue(KEYS[5]);
    openedDirPath = prop.getValue(KEYS[6]);
    savedFileName = prop.getValue(KEYS[7]);
    savedDirPath = prop.getValue(KEYS[8]);
    savedFileOpt = Optional.ofNullable(new File(savedDirPath + File.separator + savedFileName));
    numberingFileName = prop.getValue(KEYS[9]);

    File dir = new File(OUTPUT_DIR);
    dir.mkdirs();

    // configurations
    fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    fileListView.getSelectionModel().selectedItemProperty().addListener(e -> changeSelection());
    fileListView.setItems(imageFiles.getFiles());

    // slider.setOnScroll(e -> changeZoomRateWithScroll(e));
    // slider.valueProperty().addListener(e -> updateImage());
    //
    // axisComboBox.setItems(axisItems);
    // zoomRateComboBox.setItems(zoomRateItems);
    // axisComboBox.getSelectionModel().select(1);
    // zoomRateComboBox.getSelectionModel().select(1);
  }

  /**
   * ツクールのバージョンを変更し、各種パネルのサイズを変更する。
   *
   * @param aVersion
   */
  private void changeTKoolVersion(TKoolVersion aVersion) {
    version = aVersion;
    double width = (double) version.getWidth();
    // imageViewerBorderPaneController.changeVersion(width);
  }

  /** オプション設定画面を開く。 */
  private void openOptionsWindow() {
    OptionsStage optionsStage = new OptionsStage(options);
    optionsStage.showAndWait();
    options = new Options(optionsStage.getControlelr().getOptions());
    optionsStage = null;

    main.changeFontSize(options.getFontSize());
  }

  /** バージョン画面を開く。 */
  private void openVersionWindow() {
    VersionStage stage = new VersionStage();
    stage.showAndWait();
  }

  /** 取り込むファイルを選択する。 */
  private void openFile() {
    Stage stage = new Stage(StageStyle.UTILITY);
    FileChooser fc = new FileChooser();
    fc.setTitle("ファイルを開く");
    fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
    File dir = new File(openedDirPath);
    dir = dir.exists() ? dir : new File(".");
    fc.setInitialDirectory(dir);

    List<File> files = fc.showOpenMultipleDialog(stage);
    Optional<List<File>> filesOpt = Optional.ofNullable(files);
    // filesOpt.ifPresent(fileListHBoxController::addFiles);
    filesOpt.ifPresent(
        list -> {
          File file = list.get(0);
          openedFileName = file.getName();
          openedDirPath = file.getParent();
        });
  }

  /** ファイルを保存する。 ファイル名が存在しなかった場合は別名で保存が呼び出される。 */
  private void saveFile() {
    if ("".equals(initialSavedFileName)) {
      saveAsFile();
      return;
    }
  }

  /** 別名で保存する。 */
  private void saveAsFile() {
    // List<MyImageView> images = getPanelImages();
    // BufferedImage image = MyImageView.makeTKoolFacetileImage(images, version.getWidth());
    //
    // FileChooser fc = new FileChooser();
    // fc.setTitle("名前をつけて保存");
    // fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
    // File dir = new File(savedDirPath);
    // dir = dir.exists() ? dir : new File(".");
    // fc.setInitialDirectory(dir);
    // fc.setInitialFileName(savedFileName);
    //
    // Stage stage = new Stage(StageStyle.UTILITY);
    // savedFileOpt = Optional.ofNullable(fc.showSaveDialog(stage));
    // savedFileOpt.ifPresent(
    //     file -> {
    //       savedFileName = file.getName();
    //       initialSavedFileName = file.getName();
    //       savedDirPath = file.getParent();
    //       try {
    //         ImageIO.write(image, "png", file);
    //       } catch (IOException e) {
    //         e.printStackTrace();
    //       }
    //     });
  }

  /** ナンバリングしてファイルを保存する。 ファイル名が存在しなかった場合は定義するためのダイアログを呼び出す。 */
  private void numberingSaveFile() {
    if ("".equals(numberingInitialFileName)) {
      numberingSaveAsFile();
      return;
    }
    numberingSave();
  }

  /** ナンバリングして保存する際にファイル名を指定して保存する。 ダイアログを表示してファイル名の入力を強制する。 */
  private void numberingSaveAsFile() {
    TextInputDialog dialog = new TextInputDialog(numberingFileName);
    dialog.setTitle("ナンバリング別名保存");
    dialog.setHeaderText("保存するファイル名を入力してください。");
    dialog.setContentText("ファイル名：");
    Optional<String> name = dialog.showAndWait();
    name.ifPresent(
        f -> {
          numberingFileName = f;
          numberingFileName =
              numberingFileName.endsWith(".png") ? numberingFileName : numberingFileName + ".png";
          numberingInitialFileName = numberingFileName;
          numberingSave();
        });
  }

  /** ファイル末尾にナンバリングを付与して画像を出力する処理。 */
  private void numberingSave() {
    // List<MyImageView> images = getPanelImages();
    // BufferedImage image = MyImageView.makeTKoolFacetileImage(images, version.getWidth());
    //
    // File file = new File(OUTPUT_DIR + numberingFileName);
    // int index = 1;
    // file = options.makeFormatedFile(file, index);
    // while (file.exists() && index <= 100) {
    //   index++;
    //   file = options.makeFormatedFile(file, index);
    // }
    //
    // if (index <= 100) {
    //   try {
    //     ImageIO.write(image, "png", file);
    //   } catch (IOException e) {
    //     e.printStackTrace();
    //   }
    //   return;
    // }
    //
    // Alert alert = new Alert(AlertType.ERROR);
    // alert.setTitle("エラー");
    // alert.getDialogPane().setHeaderText("ファイルのナンバリングが100を超えました。");
    // alert.getDialogPane().setContentText("ファイルを整理してから再度実行してください。");
    // alert.showAndWait();
  }

  /**
   * ImageViewerクラスにリストの選択中のファイルパスを送る。
   *
   * @param filePath
   */
  public void sendFileName(String filePath) {
    // imageViewerBorderPaneController.setImage(filePath);
  }

  public TKoolVersion getTKoolVersion() {
    return version;
  }

  public Options getOptions() {
    return options;
  }

  public int getFontSize() {
    return Integer.valueOf(prop.getValue(KEYS[3]));
  }

  public void setMain(Main aMain) {
    main = aMain;
  }

  public void clearImageView() {
    // imageViewerBorderPaneController.clearImageView();
  }

  /** プロパティファイルを書き出す。 呼び出し元はMainクラスで、ウィンドウを閉じるときに呼び出される。 */
  public void makePropertiesFile() {
    String[] values = new String[KEYS.length];
    values[0] = String.valueOf(options.getSeparatorSwitch());
    values[1] = options.getSeparator().name();
    values[2] = options.getNumbering().name();
    values[3] = String.valueOf(options.getFontSize());
    values[4] = version.name();
    values[5] = openedFileName;
    values[6] = openedDirPath;
    values[7] = savedFileName;
    values[8] = savedDirPath;
    values[9] = numberingFileName;
    IntStream.range(0, KEYS.length).forEach(i -> prop.setValue(KEYS[i], values[i]));
    prop.write();

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

  @FXML
  private void bulkInsertButtonOnClicked() {
    var images =
        fileListView
            .getSelectionModel()
            .getSelectedIndices()
            .stream()
            .map(
                i -> {
                  imageFiles.select(i);
                  return cropImage.cropByBufferedImage();
                })
            .collect(Collectors.toList());
    tileImage.bulkInsert(images);
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
}

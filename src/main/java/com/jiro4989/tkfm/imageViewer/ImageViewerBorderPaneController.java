package com.jiro4989.tkfm.imageViewer;

import com.jiro4989.tkfm.MainController;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;

public class ImageViewerBorderPaneController {
  private MainController mainController;
  private Optional<Image> imageOpt = Optional.empty();

  @FXML private ImageView imageView;
  @FXML private GridPane imageViewGridPane;
  @FXML private GridPane focusGridPane;

  @FXML private GridPane trimmingGridPane;
  @FXML private ImageView trimmingImageView;

  @FXML private Slider slider;

  private ObservableList<Integer> axisItems = FXCollections.observableArrayList(1, 5, 10, 25, 50);
  @FXML private ComboBox<Integer> axisComboBox;
  private ObservableList<Integer> zoomRateItems =
      FXCollections.observableArrayList(1, 5, 10, 25, 50);
  @FXML private ComboBox<Integer> zoomRateComboBox;
  @FXML private Label xLabel;
  @FXML private Label yLabel;
  @FXML private Label rateLabel;

  @FXML
  private void initialize() {
    focusGridPane.setOnMouseDragged(e -> moveFocusGridPane(e));
    slider.setOnMouseClicked(e -> changeZoomRate(slider.getValue()));
    slider.setOnMouseDragged(e -> changeZoomRate(slider.getValue()));
    slider.setOnScroll(e -> changeZoomRateWithScroll(e));
    slider.valueProperty().addListener(e -> updateImage());

    axisComboBox.setItems(axisItems);
    zoomRateComboBox.setItems(zoomRateItems);
    axisComboBox.getSelectionModel().select(1);
    zoomRateComboBox.getSelectionModel().select(1);
  }

  public void setMainController(MainController aMainController) {
    mainController = aMainController;
  }

  /**
   * フォーカスをマウスドラッグで動かす。
   *
   * @param event マウスイベント
   */
  private void moveFocusGridPane(MouseEvent event) {
    imageOpt.ifPresent(
        image -> {
          double x = focusGridPane.getLayoutX();
          double y = focusGridPane.getLayoutY();
          double mouseX = x + event.getX();
          double mouseY = y + event.getY();
          int width = mainController.getTKoolVersion().getWidth();
          x = mouseX - width / 2;
          y = mouseY - width / 2;

          double rate = getRate();
          rate /= 100;
          setFocusPosition(x, y, rate, image.getWidth(), image.getHeight(), width);
          updateTrimmingImageView();
        });
  }

  /**
   * フォーカスの移動可能域に制限をかけながら位置移動処理を行う。
   *
   * @param x
   * @param y
   * @param rate
   * @param imageWidth
   * @param imageHeight
   * @param width
   */
  private void setFocusPosition(
      double x, double y, double rate, double imageWidth, double imageHeight, int width) {
    x = Math.max(0, x);
    y = Math.max(0, y);
    x = Math.min(imageWidth * rate - width, x);
    y = Math.min(imageHeight * rate - width, y);
    x = Math.floor(x);
    y = Math.floor(y);

    focusGridPane.setLayoutX(x);
    focusGridPane.setLayoutY(y);
    xLabel.setText("" + x);
    yLabel.setText("" + y);
  }

  /** ズーム倍率を変更する。 */
  private void changeZoomRate(double rate) {
    imageOpt
        .filter(i -> mainController.getTKoolVersion().getWidth() < i.getWidth() * rate / 100)
        .ifPresent(
            i -> {
              slider.setValue(rate);
              rateLabel.setText("" + Math.floor(rate));
              updateImage();
            });
  }

  /**
   * マウススクロールで拡大率を変更する。
   *
   * @param e
   */
  private void changeZoomRateWithScroll(ScrollEvent e) {
    double deltaY = e.getDeltaY();
    double plus = zoomRateComboBox.getSelectionModel().getSelectedItem();
    double currentRate = getRate();

    double newRate = 0 < deltaY ? currentRate + plus : currentRate - plus;
    newRate = Math.max(0, newRate);
    newRate = Math.min(200, newRate);
    changeZoomRate(newRate);
  }

  /** 画像を更新する。 */
  private void updateImage() {
    imageOpt.ifPresent(
        image -> {
          double rate = getRate();
          rate /= 100;
          double imageWidth = image.getWidth();
          double imageHeight = image.getHeight();
          int width = mainController.getTKoolVersion().getWidth();

          if (width < imageWidth * rate) {
            imageViewGridPane.setPrefSize(imageWidth * rate, imageHeight * rate);
            imageView.setFitWidth(imageWidth * rate);
            imageView.setFitHeight(imageHeight * rate);

            double x = focusGridPane.getLayoutX();
            double y = focusGridPane.getLayoutY();
            setFocusPosition(x, y, rate, imageWidth, imageHeight, width);
            updateTrimmingImageView();
          }
        });
  }

  /** トリミング画像のプレビューを更新する。 */
  private void updateTrimmingImageView() {
    imageOpt.ifPresent(
        image -> {
          double x = focusGridPane.getLayoutX();
          if (0 < x) {
            trimmingImageView.setImage(getTrimmingImage(image));
          }
        });
  }

  /** トリミングした画像を返す */
  private WritableImage getTrimmingImage(Image aImage) {
    double x = focusGridPane.getLayoutX();
    double y = focusGridPane.getLayoutY();
    double width = mainController.getTKoolVersion().getWidth();
    double rate = getRate();
    rate /= 100;
    width /= rate;
    x /= rate;
    y /= rate;
    PixelReader pix = aImage.getPixelReader();
    return new WritableImage(pix, (int) x, (int) y, (int) width, (int) width);
  }

  /**
   * トリミングイメージを返す。
   *
   * @return
   */
  public Image getTrimmingImage() {
    return trimmingImageView.getImage();
  }

  /**
   * ファイルパスからファイルを取得し、イメージをセットする。
   *
   * @param filePath
   */
  public void setImage(String filePath) {
    imageOpt = Optional.ofNullable(new Image("file:" + filePath));
    imageOpt.ifPresent(
        image -> {
          double width = image.getWidth();
          double height = image.getHeight();
          imageViewGridPane.setPrefSize(width, height);
          imageView.setImage(image);
          updateImage();
        });
  }

  public void clearImageView() {
    imageOpt = Optional.ofNullable(null);
    imageView.setImage(null);
    trimmingImageView.setImage(null);
  }

  private void move(double x, double y) {
    imageOpt.ifPresent(
        image -> {
          double rate = getRate();
          rate /= 100;
          double imageWidth = image.getWidth();
          double imageHeight = image.getHeight();
          int width = mainController.getTKoolVersion().getWidth();
          setFocusPosition(x, y, rate, imageWidth, imageHeight, width);
          updateTrimmingImageView();
        });
  }

  public void moveUp() {
    double x = getX();
    double currentY = getY();
    double plus = axisComboBox.getSelectionModel().getSelectedItem();
    double y = currentY - plus;
    move(x, y);
  }

  public void moveLeft() {
    double currentX = getX();
    double plus = axisComboBox.getSelectionModel().getSelectedItem();
    double x = currentX - plus;
    double y = getY();
    move(x, y);
  }

  public void moveDown() {
    double x = getX();
    double currentY = getY();
    double plus = axisComboBox.getSelectionModel().getSelectedItem();
    double y = currentY + plus;
    move(x, y);
  }

  public void moveRight() {
    double currentX = getX();
    double plus = axisComboBox.getSelectionModel().getSelectedItem();
    double x = currentX + plus;
    double y = getY();
    move(x, y);
  }

  public double getX() {
    return Double.valueOf(xLabel.getText());
  }

  public double getY() {
    return Double.valueOf(yLabel.getText());
  }

  public double getRate() {
    return Double.valueOf(rateLabel.getText());
  }

  public void changeVersion(double width) {
    focusGridPane.setPrefSize(width, width);
    trimmingGridPane.setPrefSize(width, width);
    trimmingImageView.setFitWidth(width);
    trimmingImageView.setFitHeight(width);
    updateTrimmingImageView();
  }

  public void zoomIn() {
    double rate = getRate();
    double plus = zoomRateComboBox.getSelectionModel().getSelectedItem();
    changeZoomRate(rate + plus);
  }

  public void zoomOut() {
    double rate = getRate();
    double plus = zoomRateComboBox.getSelectionModel().getSelectedItem();
    changeZoomRate(rate - plus);
  }
}

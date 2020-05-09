package com.jiro4989.tkfm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * プログラムを実行する最初のクラス。
 *
 * @author jiro
 * @version 2.1
 */
public class Main extends Application {
  public static final String TITLE = "TKoolFacetileMaker2";
  private MainController controller;
  private BorderPane root;
  private Stage stage;

  private static final String[] KEYS = {"x", "y", "width", "height"};
  private static final String[] INITIAL_VALUES = {"90", "26", "1280", "880"};
  private PropertiesHandler prop = new PropertiesHandler("window_options", KEYS, INITIAL_VALUES);

  @Override
  public void start(Stage primaryStage) {
    prop.load();
    stage = primaryStage;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
      root = (BorderPane) loader.load();
      controller = (MainController) loader.getController();
      controller.setMain(this);
      Scene scene = new Scene(root, 1280, 880);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage
          .getIcons()
          .add(new Image(getClass().getResource("resources/logo.png").toExternalForm()));
      primaryStage.setTitle(TITLE);
      primaryStage.setOnCloseRequest(r -> controller.makePropertiesFile());
      setStageOptions();
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("--------------------------------------------");
    System.out.println("application_name: TKoolFacetileMaker2");
    System.out.println("version: " + Version.version);
    System.out.println("commit_hash: " + Version.commitHash);
    System.out.println("document: README.txt");
    System.out.println("author: 次郎 (jiro)");
    System.out.println("contact: https://twitter.com/jiro_saburomaru");
    System.out.println("--------------------------------------------");
    launch(args);
  }

  /** ウインドウ位置やウインドウサイズを変更する。 */
  void setStageOptions() {
    stage.setX(Double.valueOf(prop.getValue("x")));
    stage.setY(Double.valueOf(prop.getValue("y")));
    stage.setWidth(Double.valueOf(prop.getValue("width")));
    stage.setHeight(Double.valueOf(prop.getValue("height")));
  }

  /** プログラム停止時にプロパティファイルを生成しexitする。 */
  void closeAction() {
    // String[] values = new String[KEYS.length];
    // values[0] = String.valueOf(stage.getX());
    // values[1] = String.valueOf(stage.getY());
    // values[2] = String.valueOf(stage.getWidth());
    // values[3] = String.valueOf(stage.getHeight());

    // IntStream.range(0, KEYS.length).forEach(i -> prop.setValue(KEYS[i], values[i]));
    // prop.write();

    Platform.exit();
  }
}

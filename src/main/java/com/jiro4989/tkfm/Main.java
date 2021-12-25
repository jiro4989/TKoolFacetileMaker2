package com.jiro4989.tkfm;

import com.jiro4989.tkfm.model.PropertiesModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
  public static final String TITLE = "TKoolFacetileMaker2";
  private MainController controller;
  private BorderPane root;
  private Stage stage;

  private PropertiesModel.Window prop = new PropertiesModel.Window();

  @Override
  public void start(Stage primaryStage) {
    prop.load();
    stage = primaryStage;
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));
      root = (BorderPane) loader.load();
      controller = (MainController) loader.getController();

      Scene scene = new Scene(root, 1280, 880);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage
          .getIcons()
          .add(new Image(getClass().getResource("resources/logo.png").toExternalForm()));

      primaryStage.setTitle(TITLE);
      primaryStage.setX(prop.getX());
      primaryStage.setY(prop.getY());
      primaryStage.setWidth(prop.getWidth());
      primaryStage.setHeight(prop.getHeight());

      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("--------------------------------------------");
    System.out.println("application_name: " + TITLE);
    System.out.println("version: " + Version.version);
    System.out.println("commit_hash: " + Version.commitHash);
    System.out.println("document: README.txt");
    System.out.println("author: 次郎 (jiro)");
    System.out.println("contact: https://twitter.com/jiro_saburomaru");
    System.out.println("--------------------------------------------");
    launch(args);
  }

  @Override
  public void stop() {
    controller.storeProperties();
    prop.setX(stage.getX());
    prop.setY(stage.getY());
    prop.setWidth(stage.getWidth());
    prop.setHeight(stage.getHeight());
    prop.store();
  }
}

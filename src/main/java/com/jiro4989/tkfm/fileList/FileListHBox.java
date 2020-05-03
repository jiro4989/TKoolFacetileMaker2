package com.jiro4989.tkfm.fileList;

import com.jiro4989.tkfm.MainController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class FileListHBox extends HBox {
  private MainController mainController;
  private FileListHBoxController controller;

  public FileListHBox(MainController aMainController) {
    super();
    mainController = aMainController;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FileListHBox.fxml"));
      HBox root = (HBox) loader.load();
      controller = (FileListHBoxController) loader.getController();
      controller.setMainController(mainController);
      getChildren().add(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public FileListHBoxController getController() {
    return controller;
  }
}

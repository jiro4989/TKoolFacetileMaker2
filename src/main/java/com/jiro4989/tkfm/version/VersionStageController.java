package com.jiro4989.tkfm.version;

import com.jiro4989.tkfm.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Window;

public class VersionStageController {
  @FXML private Button closeButton;
  @FXML private Button copyButton;
  @FXML private ImageView image;
  @FXML private Label versionLabel;

  @FXML
  public void initialize() {
    image.setImage(new Image(getClass().getResourceAsStream(VersionStage.LOGO_PATH)));
    versionLabel.setText(Main.TITLE);
  }

  @FXML
  private void close() {
    getWindow().hide();
  }

  private Window getWindow() {
    return closeButton.getScene().getWindow();
  }

  @FXML
  private void copyButtonOnClicked(ActionEvent e) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString("http://ashelter.blog.fc2.com/");
    clipboard.setContent(content);
  }
}

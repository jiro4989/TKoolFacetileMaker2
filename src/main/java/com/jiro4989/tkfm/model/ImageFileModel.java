package com.jiro4989.tkfm.model;

import java.io.File;
import javafx.scene.image.Image;

public class ImageFileModel {
  private File file;

  public ImageFileModel(File file) {
    this.file = file;
  }

  public Image readImage() {
    return new Image(file.toURI().toString());
  }

  @Override
  public String toString() {
    return file.getName();
  }
}

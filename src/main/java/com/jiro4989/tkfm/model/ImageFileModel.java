package com.jiro4989.tkfm.model;

import java.io.File;
import javafx.scene.image.Image;

public class ImageFileModel {
  private File file;
  private boolean isSelected = false;

  public ImageFileModel(File file) {
    this.file = file;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isImageFile() {
    return false;
  }

  public Image readImage() {
    return new Image(file.toURI().toString());
  }

  @Override
  public String toString() {
    return file.getName();
  }
}

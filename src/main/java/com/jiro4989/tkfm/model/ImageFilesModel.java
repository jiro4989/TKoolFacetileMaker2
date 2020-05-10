package com.jiro4989.tkfm.model;

import java.io.File;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ImageFilesModel {
  private ObservableList<ImageFileModel> files =
      FXCollections.observableArrayList(new LinkedList<>());
  private final CroppingImageModel croppingImage;

  public ImageFilesModel(CroppingImageModel ci) {
    croppingImage = ci;
  }

  public void add(ImageFileModel file) {
    files.add(file);
    select(0);
  }

  public void add(File file) {
    add(new ImageFileModel(file));
  }

  public void remove(int i) {
    if (i < 0 || files.size() <= i) return;
    files.remove(i);
    select(i);
  }

  public void clear() {
    files.clear();
    croppingImage.clearImage();
  }

  public void select(int i) {
    int min = 0;
    int max = files.size();
    if (i < min || max <= i) {
      return;
    }

    var file = files.get(i);
    var img = file.readImage();
    croppingImage.setImage(img);
  }

  public ObservableList<ImageFileModel> getFiles() {
    return files;
  }
}

package com.jiro4989.tkfm.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ImageFilesModel {
  private List<ImageFileModel> files = new LinkedList<>();
  private int selectedIndex = 0;
  private final CroppingImageModel croppingImage;

  public ImageFilesModel(CroppingImageModel ci) {
    croppingImage = ci;
  }

  public void add(ImageFileModel file) throws IOException {
    files.add(file);
    select(selectedIndex);
  }

  public void remove(int i) throws IOException {
    files.remove(i);
    select(selectedIndex);
  }

  public void clear() {
    files.clear();
    croppingImage.setImage(null);
  }

  public void select(int i) throws IOException {
    var min = 0;
    var max = files.size();
    if (i < min || max <= i) {
      return;
    }

    selectedIndex = i;
    var file = files.get(selectedIndex);
    var img = file.readImage();
    croppingImage.setImage(img);
  }

  public void selectFirst() throws IOException {
    select(0);
  }

  public void selectNext() throws IOException {
    select(selectedIndex + 1);
  }

  public void selectPrev() throws IOException {
    select(selectedIndex - 1);
  }
}

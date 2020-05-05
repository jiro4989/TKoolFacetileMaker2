package com.jiro4989.tkfm.model;

import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageFilesModel {
  private List<ImageFileModel> files = new LinkedList<>();

  public void add(ImageFileModel file) {
    files.add(file);
  }

  public void remove(int i) {
    files.remove(i);
  }

  public void clear() {
    files.clear();
  }

  public void select(int i) {

  }

  public void selectFirst() {
    select(0);
  }

  public void selectNext() {

  }

  public void selectPrev() {

  }
}

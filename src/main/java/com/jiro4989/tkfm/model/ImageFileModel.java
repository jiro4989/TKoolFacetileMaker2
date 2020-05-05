package com.jiro4989.tkfm.model;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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

  public BufferedImage readImage() throws IOException {
    return ImageIO.read(file);
  }
}

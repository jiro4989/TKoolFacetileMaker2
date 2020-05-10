package com.jiro4989.tkfm.model;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class PropertiesModel {
  private static final String CONFIG_DIR = "config";

  public interface PropertiesInterface {
    public void load();

    public void store();
  }

  public static class Window implements PropertiesInterface {
    private Properties prop = new Properties();
    private File file = configFile("window");
    private double x = 100;
    private double y = 100;
    private double width = 1280;
    private double height = 760;

    public Window() {}

    public Window(String filename) {
      file = configFile(filename);
    }

    @Override
    public void load() {
      if (!file.exists()) {
        return;
      }

      try (InputStream is = new FileInputStream(file)) {
        prop.load(new InputStreamReader(is, "UTF-8"));

        var xStr = prop.getProperty("x");
        if (!"".equals(xStr)) {
          x = Double.valueOf(xStr);
        }

        var yStr = prop.getProperty("y");
        if (!"".equals(yStr)) {
          y = Double.valueOf(yStr);
        }

        var wStr = prop.getProperty("width");
        if (!"".equals(wStr)) {
          width = Double.valueOf(wStr);
        }

        var hStr = prop.getProperty("height");
        if (!"".equals(hStr)) {
          height = Double.valueOf(hStr);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void store() {
      var dir = file.getParentFile();
      dir.mkdirs();

      prop.setProperty("x", "" + x);
      prop.setProperty("y", "" + y);
      prop.setProperty("width", "" + width);
      prop.setProperty("height", "" + height);

      try (FileOutputStream fos = new FileOutputStream(file)) {
        prop.store(new OutputStreamWriter(fos, "UTF-8"), null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // getter /////////////////////////////////////////////////////////////

    public double getX() {
      return x;
    }

    public double getY() {
      return y;
    }

    public double getWidth() {
      return width;
    }

    public double getHeight() {
      return height;
    }

    // setter /////////////////////////////////////////////////////////////

    public void setX(double x) {
      this.x = x;
    }

    public void setY(double y) {
      this.y = y;
    }

    public void setWidth(double width) {
      this.width = width;
    }

    public void setHeight(double height) {
      this.height = height;
    }
  }

  public static class ChoosedFile implements PropertiesInterface {
    private Properties prop = new Properties();
    private File file = configFile("choosed_file");
    private Optional<File> openedFile = Optional.ofNullable(null);
    private Optional<File> savedFile = Optional.ofNullable(null);

    public ChoosedFile() {}

    public ChoosedFile(String filename) {
      file = configFile(filename);
    }

    @Override
    public void load() {
      if (!file.exists()) {
        return;
      }

      try (InputStream is = new FileInputStream(file)) {
        prop.load(new InputStreamReader(is, "UTF-8"));

        var of = readFileFromProperties(prop, "opened_file_dir", "opened_file_file");
        openedFile = Optional.ofNullable(of);

        var sf = readFileFromProperties(prop, "saved_file_dir", "saved_file_file");
        savedFile = Optional.ofNullable(sf);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void store() {
      var dir = file.getParentFile();
      dir.mkdirs();

      openedFile.ifPresent(
          f -> {
            prop.setProperty("opened_file_dir", "" + f.getParentFile().getAbsolutePath());
            prop.setProperty("opened_file_file", "" + f.getName());
          });

      savedFile.ifPresent(
          f -> {
            prop.setProperty("saved_file_dir", "" + f.getParentFile().getAbsolutePath());
            prop.setProperty("saved_file_file", "" + f.getName());
          });

      try (FileOutputStream fos = new FileOutputStream(file)) {
        prop.store(new OutputStreamWriter(fos, "UTF-8"), null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // getter /////////////////////////////////////////////////////////////////

    public Optional<File> getOpenedFile() {
      return openedFile;
    }

    public Optional<File> getSavedFile() {
      return savedFile;
    }

    // setter /////////////////////////////////////////////////////////////////

    public void setOpenedFile(File file) {
      openedFile = Optional.ofNullable(file);
    }

    public void setSavedFile(File file) {
      savedFile = Optional.ofNullable(file);
    }

    // private methods ////////////////////////////////////////////////////////

    private static File readFileFromProperties(Properties prop, String dirKey, String fileKey) {
      var dir = prop.getProperty(dirKey);
      var file = prop.getProperty(fileKey);
      var path = dir + File.separator + file;
      var of = new File(path);
      if (of.exists()) {
        return of;
      } else if (of.getParentFile().exists()) {
        return of.getParentFile();
      }
      return null;
    }
  }

  private static File configFile(String filename) {
    return new File(CONFIG_DIR + File.separator + filename + ".properties");
  }
}

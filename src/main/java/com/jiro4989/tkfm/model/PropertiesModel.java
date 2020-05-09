package com.jiro4989.tkfm.model;

import java.io.*;
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
    private double x = 0;
    private double y = 0;
    private double width = 1280;
    private double height = 880;

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

  private static File configFile(String filename) {
    return new File(CONFIG_DIR + File.separator + filename + ".properties");
  }
}

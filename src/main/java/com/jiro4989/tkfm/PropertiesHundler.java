package com.jiro4989.tkfm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * プロパティファイルのユーティリティクラス。
 * ファイル名のみをコンストラクタに渡すと、
 * プロパティファイルを管理するためのディレクトリのパスを付与したファイルを保持する。
 * @author jiro
 * @version 3.0
 */
public class PropertiesHundler {
  private Properties prop = new Properties();
  private final File propertiesFile;
  private static final String INITIAL_IO_DIR_PATH = "." + File.separator + "properties";

  /**
   * 参照するファイルを渡すコンストラクタ
   * @param aFile プロパティファイル
   */
  public PropertiesHundler(File aFile) {
    propertiesFile = aFile;
  }

  /**
   * ファイル名のみを指定する場合のコンストラクタ。
   * プロパティファイルの保存先は"./properties"
   * @param aFileName 保存するファイル名(拡張子は不要)
   */
  public PropertiesHundler(String aFileName) {
    this(aFileName, INITIAL_IO_DIR_PATH);
  }

  /**
   * ファイル名とプロパティファイルの保存先のパスを指定する場合のコンストラクタ。
   * @param aFileName 保存するファイル名(拡張子は不要)
   * @param anIoDirPath ファイル保存先のディレクトリの相対パス
   */
  public PropertiesHundler(String aFileName, String anIoDirPath) {
    propertiesFile = new File(anIoDirPath + File.separator + aFileName + ".properties");
  }

  /**
   * プロパティファイルをロードする。
   */
  public void load() {
    if (propertiesFile.exists()) {
      try (InputStream is = new FileInputStream(propertiesFile)) {
        prop.load(new InputStreamReader(is, "UTF-8"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * プロパティファイルを出力する。
   */
  public void write() {
    try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
      prop.store(new OutputStreamWriter(fos, "UTF-8"), null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * プロパティファイルが存在するかどうかを調べる。
   * @return 存在する or 存在しない
   */
  public boolean exists() {
    return propertiesFile.exists();
  }

  /**
   * ディレクトリを生成する。
   * @return 成功 or 失敗
   */
  public boolean mkdirs() {
    return propertiesFile.getParentFile().mkdirs();
  }

  /**
   * 保持しているファイルを返す。
   * @return プロパティファイル
   */
  public File getFile() {
    return propertiesFile;
  }

  /**
   * ファイルから値を取得する。
   * @param key キー
   * @return 値
   */
  public String getValue(String key) {
    return prop.getProperty(key);
  }

  /**
   * ファイルに保存するキーと値を設定する。
   * @param key キー
   * @param value 値
   */
  public void setValue(String key, String value) {
    prop.setProperty(key, value);
  }

  @Override
  public String toString() {
    return String.format("Properties: %s, File: %s.", prop, propertiesFile);
  }
}

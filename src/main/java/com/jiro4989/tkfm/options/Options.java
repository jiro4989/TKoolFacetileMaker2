package com.jiro4989.tkfm.options;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * オプション設定画面で管理するプロパティを一括に管理するクラス。
 *
 * @author jiro
 */
public class Options {
  private boolean separatorSwitch;
  private Separators separators;
  private Numberings numberings;
  private int fontSize;

  public Options() {
    this(false, Separators.UNDER_SCORE, Numberings.NUMBERING01, 12);
  }

  public Options(
      boolean aSeparatorSwitch, Separators aSeparator, Numberings aNumberings, int aFontSize) {
    separatorSwitch = aSeparatorSwitch;
    separators = aSeparator;
    numberings = aNumberings;
    fontSize = aFontSize;
  }

  /**
   * 引数のフィールド値をコピーする。
   *
   * @param options
   */
  public Options(Options options) {
    separatorSwitch = options.separatorSwitch;
    separators = options.separators;
    numberings = options.numberings;
    fontSize = options.fontSize;
  }

  public boolean getSeparatorSwitch() {
    return separatorSwitch;
  }

  public Separators getSeparator() {
    return separators;
  }

  public Numberings getNumbering() {
    return numberings;
  }

  public int getFontSize() {
    return fontSize;
  }

  /**
   * Optionsインスタンスが保持するプロパティを元に、 ファイルにインデックスを付与して返す。
   *
   * @param fileName 対象ファイルの名前
   * @param index 末尾に付与する番号
   * @return 整形後のファイル
   */
  public File makeFormatedFile(String fileName, int index) {
    return makeFormatedFile(new File(fileName), index);
  }

  /**
   * Optionsインスタンスが保持するプロパティを元に、 ファイルにインデックスを付与して返す。
   *
   * @param file 対象ファイル
   * @param index 末尾に付与する番号
   * @return 整形後のファイル
   */
  public File makeFormatedFile(File file, int index) {
    String fileName = file.getPath();
    String substring = fileName.substring(0, fileName.length() - 4);
    Pattern p = Pattern.compile("0*[0-9]*$");
    Matcher m = p.matcher(substring);

    StringBuilder sb = new StringBuilder();
    if (m.find()) {
      int numberLength = m.group().length();
      substring = substring.substring(0, substring.length() - numberLength);
      sb.append(substring);

      if (getSeparatorSwitch() && !Separators.anyMatchEnds(substring.toString())) {
        sb.append(getSeparator().getSeparator());
      }

      String format = getNumbering().getFormat();
      sb.append(String.format(format, index));
    }
    sb.append(".png");
    return new File(sb.toString());
  }

  @Override
  public String toString() {
    return String.format(
        "separator: %s, sep: %s, format: %s, fontSize: %d",
        separatorSwitch, separators.getSeparator(), numberings.getFormat(), fontSize);
  }
}

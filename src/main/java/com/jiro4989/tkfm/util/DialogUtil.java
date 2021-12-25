package com.jiro4989.tkfm.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DialogUtil {
  /**
   * 原因不明の汎用的なエラーダイアログを表示して待機する。
   *
   * @param exception Exception名
   */
  public static void showAndWaitCommonExceptionDialog(String exception) {
    showAndWaitExceptionDialog(exception, "コンソールに出力されているメッセージと一緒に作者に問い合わせてください");
  }

  /**
   * エラーダイアログを表示して待機する。
   *
   * @param exception Exception名
   * @param contentText テキスト
   */
  public static void showAndWaitExceptionDialog(String exception, String contentText) {
    var dialog = new Alert(AlertType.ERROR);
    // dialog.initOwner(stage);
    dialog.setTitle("Exception - " + exception);
    dialog.setContentText(contentText);
    dialog.showAndWait();
  }
}

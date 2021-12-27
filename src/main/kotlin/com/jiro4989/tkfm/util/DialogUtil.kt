package com.jiro4989.tkfm.util

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType

/**
 * 原因不明の汎用的なエラーダイアログを表示して待機する。
 *
 * @param exception Exception名
 */
fun showAndWaitCommonExceptionDialog(exception: String) {
  showAndWaitExceptionDialog(exception, "コンソールに出力されているメッセージと一緒に作者に問い合わせてください")
}

/**
 * エラーダイアログを表示して待機する。
 *
 * @param exception Exception名
 * @param contentText テキスト
 */
fun showAndWaitExceptionDialog(exception: String, contentText: String) {
  var dialog = Alert(AlertType.ERROR)
  // dialog.initOwner(stage);
  dialog.setTitle("Exception - " + exception)
  dialog.setContentText(contentText)
  dialog.showAndWait()
}

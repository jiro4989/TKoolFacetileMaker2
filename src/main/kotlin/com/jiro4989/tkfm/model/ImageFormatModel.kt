package com.jiro4989.tkfm.model

import javafx.beans.property.*

/** 画像フォーマット。画像フォーマット名、行数、列数、1タイルあたりの矩形を管理する。 */
data class ImageFormatModel(
    /** フォーマットの名前 */
    val name: String,
    /** 行数 */
    val rowProperty: IntegerProperty,
    /** 列数 */
    val colProperty: IntegerProperty,
    /** 1タイルあたりの矩形 */
    val rectangle: RectangleModel
) {
  constructor(name: String, row: Int, col: Int, rect: RectangleModel) : this(
      name, SimpleIntegerProperty(row), SimpleIntegerProperty(col), rect)
}

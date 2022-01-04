package com.jiro4989.tkfm.model

import javafx.beans.property.SimpleStringProperty as SSP
import javafx.beans.property.StringProperty
import javafx.scene.control.TextField

/** 入力フォームが受け付ける文字列であるかを検証する */
internal fun isAvailableInteger(value: String, emptyOK: Boolean): Boolean {
  return when {
    // 空文字はOK
    emptyOK && value == "" -> true
    // 自然数はOK。0始まりの数値はNG
    Regex("""^[1-9]\d*$""").matches(value) -> true
    else -> false
  }
}

class ImageFormatViewModel(
    val nameProperty: StringProperty = SSP("My Format"),
    val rowProperty: StringProperty = SSP("2"),
    val colProperty: StringProperty = SSP("4"),
    val tileWidthProperty: StringProperty = SSP("144"),
    val tileHeightProperty: StringProperty = SSP("144")
) {
  constructor(name: String, row: String, col: String, tileWidth: String, tileHeight: String) : this(
      SSP(name), SSP(row), SSP(col), SSP(tileWidth), SSP(tileHeight))

  fun validate(): Boolean {
    return when {
      nameProperty.get() == "" -> false
      !isAvailableInteger(rowProperty.get(), false) -> false
      !isAvailableInteger(colProperty.get(), false) -> false
      !isAvailableInteger(tileWidthProperty.get(), false) -> false
      !isAvailableInteger(tileHeightProperty.get(), false) -> false
      else -> true
    }
  }
}

package com.jiro4989.tkfm.model

import java.io.File
import javafx.collections.FXCollections
import javafx.collections.ObservableList

data class ImageFilesModel(
    private val croppingImage: CroppingImageModel,
    val files: ObservableList<ImageFileModel> = FXCollections.observableArrayList(mutableListOf())
) {

  fun add(file: ImageFileModel) {
    files.add(file)
    select(0)
  }

  fun add(file: File) {
    add(ImageFileModel(file))
  }

  fun remove(i: Int) {
    if (i < 0 || files.size <= i) return
    files.removeAt(i)
    select(i)
  }

  fun clear() {
    files.clear()
    croppingImage.clearImage()
  }

  /** i番目の画像を読み込んでトリミング済み画像としてセットする */
  fun select(i: Int) {
    val min = 0
    val max = files.size
    if (i < min || max <= i) {
      return
    }

    val file = files[i]
    val img = file.readImage()
    croppingImage.setImage(img)
  }
}

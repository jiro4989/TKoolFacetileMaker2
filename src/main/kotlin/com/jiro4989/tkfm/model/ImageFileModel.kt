package com.jiro4989.tkfm.model

import java.io.File
import javafx.scene.image.Image

class ImageFileModel(private val file: File) {
  fun readImage() = Image(file.toURI().toString())
  override fun toString() = file.getName()
}

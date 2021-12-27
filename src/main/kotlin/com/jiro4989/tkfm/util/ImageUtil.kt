package com.jiro4989.tkfm.util

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javax.imageio.ImageIO

fun writeFile(image: Image, file: File) {
  val img = SwingFXUtils.fromFXImage(image, null)
  ImageIO.write(img, "png", file)
}

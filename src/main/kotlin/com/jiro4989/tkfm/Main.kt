package com.jiro4989.tkfm

import com.jiro4989.tkfm.controller.MainViewController
import com.jiro4989.tkfm.model.PropertiesModel
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

val title = "TKoolFacetileMaker2"

/** プログラムのエントリーポイント */
fun main(args: Array<String>) {
  println("--------------------------------------------")
  println("application_name: $title")
  println("version: ${Version.version}")
  println("commit_hash: ${Version.commitHash}")
  println("document: README.txt")
  println("author: 次郎 (jiro)")
  println("contact: https://twitter.com/jiro_saburomaru")
  println("--------------------------------------------")

  Application.launch(Main::class.java, *args)
}

/** プログラムのエントリーポイント */
class Main : Application() {
  private lateinit var controller: MainViewController
  private lateinit var root: BorderPane
  private lateinit var stage: Stage
  private val prop = PropertiesModel.Window()

  override fun start(primaryStage: Stage) {
    prop.load()
    stage = primaryStage
    try {
      val loader = FXMLLoader(this.javaClass.getResource("fxml/main_view.fxml"))
      root = loader.load() as BorderPane
      controller = loader.getController() as MainViewController

      val scene = Scene(root, 1280.0, 880.0)
      scene.stylesheets += this.javaClass.getResource("css/application.css").toExternalForm()

      stage.scene = scene
      stage.icons += Image(this.javaClass.getResource("img/logo.png").toExternalForm())
      stage.title = title
      stage.x = prop.getX()
      stage.y = prop.getY()
      stage.width = prop.getWidth()
      stage.height = prop.getHeight()

      stage.show()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun stop() {
    controller.storeProperties()
    prop.setX(stage.getX())
    prop.setY(stage.getY())
    prop.setWidth(stage.getWidth())
    prop.setHeight(stage.getHeight())
    prop.store()
  }
}

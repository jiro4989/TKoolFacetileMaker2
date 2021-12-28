package com.jiro4989.tkfm

import com.jiro4989.tkfm.controller.MainViewController
import com.jiro4989.tkfm.model.WindowPropertiesModel
import java.util.Properties
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

val applicationTitle = "TKoolFacetileMaker2"

/** プログラムのエントリーポイント */
fun main(args: Array<String>) {
  Application.launch(Main::class.java, *args)
}

/** プログラムのエントリーポイント */
class Main : Application() {
  private lateinit var controller: MainViewController
  private lateinit var root: BorderPane
  private lateinit var stage: Stage
  private val prop = WindowPropertiesModel()

  override fun start(primaryStage: Stage) {
    printApplicationInformation()
    prop.load()
    stage = primaryStage
    try {
      val loader = FXMLLoader(this.javaClass.getResource("fxml/main_view.fxml"))
      root = loader.load() as BorderPane
      controller = loader.getController() as MainViewController

      val scene = Scene(root, 1280.0, 880.0)
      scene.stylesheets += this.javaClass.getResource("css/application.css").toExternalForm()

      val thisClass = this.javaClass
      stage.apply {
        setScene(scene)
        icons.add(Image(thisClass.getResource("img/logo.png").toExternalForm()))
        setTitle(applicationTitle)
        setX(prop.x)
        setY(prop.y)
        setWidth(prop.width)
        setHeight(prop.height)
        show()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun stop() {
    controller.storeProperties()
    prop.apply {
      x = stage.x
      y = stage.y
      width = stage.width
      height = stage.height
      store()
    }
  }

  fun printApplicationInformation() {
    val property = Properties()
    this.javaClass.getResourceAsStream("properties/application.properties")?.bufferedReader().use {
      property.load(it)
    }
    val version = property.get("version")
    val commitHash = property.get("commithash")
    println("--------------------------------------------")
    println("application_name: $applicationTitle")
    println("version: $version")
    println("commit_hash: $commitHash")
    println("document: README.txt")
    println("author: 次郎 (jiro)")
    println("contact: https://twitter.com/jiro_saburomaru")
    println("--------------------------------------------")
  }
}

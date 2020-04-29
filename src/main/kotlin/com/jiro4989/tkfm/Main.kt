package com.jiro4989.tkfm

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.*

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

internal class Main : Application() {

    override fun start(primaryStage: Stage) {
        val res = ResourceBundle.getBundle("dict.main", Locale.getDefault())
        val root: Parent? = FXMLLoader.load(this.javaClass.getResource("/layout/main.fxml"), res)
        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.title = "TKool Facetile Maker"
        primaryStage.minWidth = 1280.0
        primaryStage.minHeight = 720.0
        //primaryStage.icons     += Image(Texts.APP_ICON)

        primaryStage.show()
    }
}

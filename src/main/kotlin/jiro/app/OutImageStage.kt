package jiro.app

import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import jiro.app.model.VersionModel
import java.util.*

class OutImageStage(tkoolVersion: VersionModel) : AnchorPane() {
    private lateinit var controller: OutImageController

    init {
        val res = ResourceBundle.getBundle("dict.main", Locale.getDefault())
        val loader = FXMLLoader(this.javaClass.getResource("/layout/out_image.fxml"), res)
        val root = loader.load() as AnchorPane
        controller = loader.getController<OutImageController>()
        controller.tkoolVersion = tkoolVersion
        this.children += root
    }
}
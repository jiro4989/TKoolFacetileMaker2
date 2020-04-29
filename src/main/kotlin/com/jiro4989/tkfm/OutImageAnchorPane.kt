package com.jiro4989.tkfm

import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import com.jiro4989.tkfm.model.VersionModel
import java.util.*

class OutImageAnchorPane(tkoolVersion: VersionModel, private val mainController: MainController) : AnchorPane() {
    private var controller: OutImageController

    init {
        val res = ResourceBundle.getBundle("dict.main", Locale.getDefault())
        val loader = FXMLLoader(this.javaClass.getResource("/layout/out_image.fxml"), res)
        val root = loader.load() as AnchorPane
        controller = loader.getController<OutImageController>()
        controller.tkoolVersion = tkoolVersion
        controller.mainController = mainController
        controller.initImage()
        controller.drawTile()
        this.children += root
    }

    fun clear() {
        controller.clear()
    }

    fun setImages(index: Int, images: List<Image>) = controller.setImages(index, images)
    fun changeClickModeToSetImage() = controller.changeClickModeToSetImage()
    fun changeClickModeToDeleteImage() = controller.changeClickModeToDeleteImage()
    fun changeClickModeToSwapImage() = controller.changeClickModeToSwapImage()
    fun changeClickModeToFlipImage() = controller.changeClickModeToFlipImage()
    fun mouseClickEvent(mouseEvent: MouseEvent, images: List<Image>) {}
}
package jiro.app

import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File
import javax.swing.plaf.FileChooserUI

fun openFileMenu(): List<File> {
    val stage = Stage(StageStyle.UTILITY)
    val fc = FileChooser()
    fc.extensionFilters += FileChooser.ExtensionFilter("Image Files", "*.png")
    fc.initialDirectory = File(".")
    val files = fc.showOpenMultipleDialog(stage)
    return files
}


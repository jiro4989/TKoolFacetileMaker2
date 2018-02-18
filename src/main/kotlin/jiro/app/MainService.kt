package jiro.app

import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import jiro.app.model.FileListModel
import jiro.app.model.TrimPosManageModel
import java.io.File
import java.io.IOException

class MainService(
        private val fileListModel: FileListModel
        , private val trimPosManageModel: TrimPosManageModel
) {
    // 一度でも保存したファイル
    private var savedFile: File? = null

    /**
     * ファイル選択ダイアログを開き、トリミング対象として取り込む画像を選択する。
     */
    fun open(): List<File> {
        val stage = Stage(StageStyle.UTILITY)
        val fc = FileChooser()
        fc.extensionFilters += FileChooser.ExtensionFilter("Image Files", "*.png")
        fc.initialDirectory = File(".")
        val files = fc.showOpenMultipleDialog(stage)
        return files
    }

    /**
     * 画像を上書き保存する。
     * 初めての保存処理の場合は別名保存の処理を実行する。
     */
    fun save() {
        savedFile?.let {
            try {
                //outImagePreviewModel.saveImage(it)
            } catch (e: IOException) {
                e.printStackTrace()
                TODO("エラーメッセージをGUIで表示する")
                TODO("エラーをログファイルに出力する")
            }
            return
        }

        // 初めての保存処理なんで別名保存ダイアログを表示して対象を選択する
        saveAs()
    }

    /**
     * 画像を別名保存する。
     */
    fun saveAs() {
        val stage = Stage(StageStyle.UTILITY)
        val fileChooser = FileChooser()
        fileChooser.extensionFilters += FileChooser.ExtensionFilter("Image Files", "*.png")
        fileChooser.initialDirectory = savedFile?.let { it.parentFile } ?: File(".")

        savedFile = fileChooser.showSaveDialog(stage)
        savedFile?.let {
            try {
                //outImagePreviewModel.saveImage(it)
            } catch (e: IOException) {
                e.printStackTrace()
                TODO("エラーメッセージをGUIで表示する")
                TODO("エラーをログファイルに出力する")
            }
        }
    }
}


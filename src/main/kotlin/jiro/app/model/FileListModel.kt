package jiro.app.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ListView
import jiro.app.util.MAX_IMAGE_COUNT
import java.io.File

/**
 * 画像ファイルのパス情報のObservableListのラッパークラス。
 * 原則ListViewへのデータの追加はこのラッパークラスのメソッド経由で行う。
 */
class FileListModel(private val listView: ListView<File>) {
    val files: ObservableList<File> = FXCollections.observableArrayList()

    init {
        listView.items = files
    }

    fun add(files: List<File>) {
        this.files.addAll(files)
    }

    fun clear() {
        listView.selectionModel.clearSelection()
        files.clear()
    }

    fun delete(index: Int) = files.removeAt(index)
    fun swapPos(from: Int, to: Int) {
        val fromFile = files[from]
        val toFile = files[to]
        files[from] = toFile
        files[to] = fromFile
        listView.selectionModel.clearSelection()
        listView.selectionModel.select(to)
    }

    fun selectedItems(): MutableList<File> {
        val selectedItems = listView.selectionModel.selectedItems
        val max = if (selectedItems.size <= MAX_IMAGE_COUNT) selectedItems.size else MAX_IMAGE_COUNT
        return selectedItems.subList(0, max)
    }

    fun deleteSelectedItems() {
        val selectedItems = listView.selectionModel.selectedItems
        listView.items.removeAll(selectedItems)
    }

    fun swapUp() {
        val idx = listView.selectionModel.selectedIndex
        val swapIdx = idx - 1
        if (0 <= swapIdx) swapPos(idx, swapIdx)
    }

    fun swapDown() {
        val idx = listView.selectionModel.selectedIndex
        val swapIdx = idx + 1
        val size = listView.items.size
        if (swapIdx < size) swapPos(idx, swapIdx)
    }
}
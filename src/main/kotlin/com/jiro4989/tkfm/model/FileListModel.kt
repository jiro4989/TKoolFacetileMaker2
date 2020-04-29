package com.jiro4989.tkfm.model

import java.io.File
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ListView

/**
 * 画像ファイルのパス情報のObservableListのラッパークラス。
 * 原則ListViewへのデータの追加はこのラッパークラスのメソッド経由で行う。
 */
class FileListModel(private val listView: ListView<File>, private val version: VersionModel) {
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

    fun getSelectedItems(): MutableList<File> {
        val selectedItems = listView.selectionModel.selectedItems
        val max = if (selectedItems.size <= version.getMaxImageCount()) selectedItems.size else version.getMaxImageCount()
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
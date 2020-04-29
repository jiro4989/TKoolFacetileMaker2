package jiro.app.data

import jiro.app.model.VersionModel

/**
 * 幅
 */
data class Size(val width: Double, val height: Double)

/**
 * x, y座標
 */
data class Point(val x: Double = 0.0, val y: Double = 0.0, val version: VersionModel) {
    // 画像タイル上の番号(0~)
    val index = Math.floor(Math.floor(x / version.getImageOneTileWidth()) + Math.floor(y / version.getImageOneTileHeight()) * version.getImageColumnCount()).toInt()

    fun trim(): Point = trim(index)

    /**
     * タイル上の画像のときの、トリミング位置(左上)のPositionを返す
     */
    fun trim(index: Int): Point {
        val c = index % version.getImageColumnCount()
        val r = index / version.getImageColumnCount()

        val w = version.getImageOneTileWidth()
        val h = version.getImageOneTileHeight()
        val x = (c * w).toDouble()
        val y = (r * h).toDouble()
        return Point(x, y, version)
    }
}

/**
 * 矩形
 */
data class Rectangle(val point: Point, val size: Size)

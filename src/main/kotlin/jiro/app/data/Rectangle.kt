package jiro.app.data

import jiro.app.model.VersionModel
import jiro.app.tkoolVersion

/**
 * 幅
 */
data class Size(val width: Double, val height: Double)

/**
 * x, y座標
 */
data class Point(val x: Double = 0.0, val y: Double = 0.0) {
    // 画像タイル上の番号(0~)
    val index = Math.floor(Math.floor(x / tkoolVersion.getImageOneTileWidth()) + Math.floor(y / tkoolVersion.getImageOneTileHeight()) * tkoolVersion.getImageColumnCount()).toInt()

    fun trim(): Point = trim(index)

    /**
     * タイル上の画像のときの、トリミング位置(左上)のPositionを返す
     */
    fun trim(index: Int, version: VersionModel = tkoolVersion): Point {
        val c = index % version.getImageColumnCount()
        val r = index / version.getImageColumnCount()

        val w = version.getImageOneTileWidth()
        val h = version.getImageOneTileHeight()
        val x = (c * w).toDouble()
        val y = (r * h).toDouble()
        return Point(x, y)
    }
}

/**
 * 矩形
 */
data class Rectangle(val point: Point = Point(), val size: Size)

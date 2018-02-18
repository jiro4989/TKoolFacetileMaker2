package jiro.app.data

import jiro.app.util.IMAGE_HEIGHT
import jiro.app.util.IMAGE_WIDTH
import jiro.app.util.TILE_COLUMN_COUNT
import jiro.app.configModel as config

/**
 * 幅
 */
data class Size(val width: Double, val height: Double)

/**
 * x, y座標
 */
data class Point(val x: Double = 0.0, val y: Double = 0.0) {
    // 画像タイル上の番号(0~)
    val index = Math.floor(Math.floor(x / IMAGE_WIDTH) + Math.floor(y / IMAGE_HEIGHT) * TILE_COLUMN_COUNT).toInt()

    fun trim(): Point = trim(index)

    /**
     * タイル上の画像のときの、トリミング位置(左上)のPositionを返す
     */
    fun trim(index: Int): Point {
        val c = index % TILE_COLUMN_COUNT
        val r = index / TILE_COLUMN_COUNT

        val w = IMAGE_WIDTH
        val h = IMAGE_HEIGHT
        val x = (c * w).toDouble()
        val y = (r * h).toDouble()
        return Point(x, y)
    }
}

/**
 * 矩形
 */
data class Rectangle(val point: Point = Point(), val size: Size)

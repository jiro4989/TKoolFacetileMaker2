package com.jiro4989.tkfm.model

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty as DP

data class RectangleModel(val widthProperty: DoubleProperty, val heightProperty: DoubleProperty) {
  var width
    get() = widthProperty.get()
    set(value) = widthProperty.set(value)
  var height
    get() = heightProperty.get()
    set(value) = heightProperty.set(value)

  constructor(w: Double, h: Double) : this(DP(w), DP(h))

  operator fun div(scale: Double) = Pair(width / scale, height / scale)
}

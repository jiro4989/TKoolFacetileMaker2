package com.jiro4989.tkfm.model

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty as DP

data class PositionModel(val xProperty: DoubleProperty, val yProperty: DoubleProperty) {
  var x
    get() = xProperty.get()
    set(value) = xProperty.set(value)
  var y
    get() = yProperty.get()
    set(value) = yProperty.set(value)
  constructor() : this(DP(0.0), DP(0.0))
  constructor(x: Double, y: Double) : this(DP(x), DP(y))

  operator fun div(scale: Double) = Pair(x / scale, y / scale)
}

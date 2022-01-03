package com.jiro4989.tkfm.util

/** Returns `true` when the string format `value` is integer. */
fun isInteger(value: String): Boolean {
  return when {
    Regex("""^0\d+$""").matches(value) -> false
    Regex("""^\d+$""").matches(value) -> true
    else -> false
  }
}

/** Returns `true` when the string format `value` is double. */
fun isDouble(value: String): Boolean {
  return when {
    Regex("""^0\.\d+$""").matches(value) -> true
    Regex("""^[^0]\d*\.\d+$""").matches(value) -> true
    Regex("""^[\d]+$""").matches(value) -> true
    else -> false
  }
}

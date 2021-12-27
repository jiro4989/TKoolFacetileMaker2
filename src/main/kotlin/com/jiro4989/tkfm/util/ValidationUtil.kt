package com.jiro4989.tkfm.util

/** Returns `true` when the string format `value` is integer. */
fun isInteger(value: String): Boolean {

  if (Regex("""^0\d+$""").matches(value)) return false
  if (Regex("""^\d+$""").matches(value)) return true
  return false
}

/** Returns `true` when the string format `value` is double. */
fun isDouble(value: String): Boolean {
  if (Regex("""^0\.\d+$""").matches(value)) return true
  if (Regex("""^[^0]\d*\.\d+$""").matches(value)) return true
  if (Regex("""^[\d]+$""").matches(value)) return true
  return false
}

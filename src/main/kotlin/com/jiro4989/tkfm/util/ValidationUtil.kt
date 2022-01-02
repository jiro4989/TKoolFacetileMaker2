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

/** 入力フォームが受け付ける文字列であるかを検証する */
fun isAvailableInteger(value: String, zeroOK: Boolean): Boolean {
  // 空文字はOK
  if (value == "") return true
  // ゼロを許容するならTrue
  if (zeroOK && value == "0") return true
  // 自然数はOK。0始まりの数値はNG
  if (Regex("""^[1-9]\d*$""").matches(value)) return true
  return false
}

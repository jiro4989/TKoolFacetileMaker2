package com.jiro4989.tkfm.util;

public class Validator {
  /** Returns `true` when the string format `value` is double. */
  public static boolean isDouble(String value) {
    if (value.matches("^0\\.\\d+$")) return true;
    if (value.matches("^[^0]\\d*\\.\\d+$")) return true;
    if (value.matches("^[\\d]+$")) return true;
    return false;
  }
}

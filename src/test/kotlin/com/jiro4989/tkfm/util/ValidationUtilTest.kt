package com.jiro4989.tkfm.util

import java.io.IOException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
public class ValidationUtilTest {
  @ParameterizedTest
  @CsvSource(
      "true, 0",
      "true, 1",
      "true, 9",
      "true, 10",
      "true, 12345678987",
      "false, -1",
      "false, helloworld",
      "false, 01",
      "false, 010")
  fun testIsInteger(want: Boolean, input: String) {
    var got = isInteger(input)
    assertEquals(want, got)
  }

  @ParameterizedTest
  @CsvSource(
      "true, 0",
      "true, 1",
      "true, 0.5",
      "true, 0.12",
      "true, 3.14",
      "true, 100",
      "true, 100.9",
      "false, -1",
      "false, 01.1234",
      "false, 3.14.1",
      "false, hello",
      "false, ---")
  fun testIsDouble(want: Boolean, input: String) {
    var got = isDouble(input)
    assertEquals(want, got)
  }

  @ParameterizedTest
  @CsvSource(
      "true, '', false",
      "true, 1, false",
      "true, 10, false",
      "true, 999, false",
      "true, 0, true",
      "false, 0, false",
      "false, -1, false",
      "false, a, false",
      "false, 01, false",
      "false, 1a, false",
      "false, あ, false")
  fun testIsAvailableInteger(want: Boolean, value: String, zeroOK: Boolean) {
    var got = isAvailableInteger(value, zeroOK)
    assertEquals(want, got)
  }
}

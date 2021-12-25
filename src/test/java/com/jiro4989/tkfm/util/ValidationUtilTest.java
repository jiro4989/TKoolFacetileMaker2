package com.jiro4989.tkfm.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class ValidationUtilTest {
  @Test
  public void testConstructor() throws IOException {
    new ValidationUtil();
  }

  @ParameterizedTest
  @CsvSource({
    "true, 0",
    "true, 1",
    "true, 9",
    "true, 10",
    "true, 12345678987",
    "false, -1",
    "false, helloworld",
    "false, 01",
    "false, 010",
  })
  public void testIsInteger(boolean want, String input) throws Exception {
    var got = ValidationUtil.isInteger(input);
    assertEquals(want, got);
  }

  @ParameterizedTest
  @CsvSource({
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
    "false, ---",
  })
  public void testIsDouble(boolean want, String input) throws Exception {
    var got = ValidationUtil.isDouble(input);
    assertEquals(want, got);
  }
}

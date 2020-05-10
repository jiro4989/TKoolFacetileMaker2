package com.jiro4989.tkfm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.*;

@ExtendWith(ApplicationExtension.class)
public class VersionTest {
  @Test
  public void testConstructor() throws Exception {
    new Version();
  }
}

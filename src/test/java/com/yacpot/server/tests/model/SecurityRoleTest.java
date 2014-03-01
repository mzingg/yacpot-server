package com.yacpot.server.tests.model;

import com.yacpot.server.model.SecurityRole;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecurityRoleTest {

  @Test
  public void testSettingDescription() {
    SecurityRole testObj = new SecurityRole().description("Expected description");

    assertEquals("Expected description", testObj.description());
  }
}

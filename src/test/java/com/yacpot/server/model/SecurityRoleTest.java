package com.yacpot.server.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecurityRoleTest {

  @Test
  public void testSettingDescription() {
    SecurityRole testObj = new SecurityRole().setDescription("Expected getDescription");

    assertEquals("Expected getDescription", testObj.getDescription());
  }
}

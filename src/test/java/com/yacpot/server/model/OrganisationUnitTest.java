package com.yacpot.server.model;

import org.junit.Test;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.*;

public class OrganisationUnitTest {

  @Test
  public void testAddRooms() {
    OrganisationUnit ou = new OrganisationUnit().room(new Room().label("Room B")).room(new Room().label("Room A"));

    assertEquals("Room A,Room B", toJoinedString(ou.rooms()));
  }

  @Test
  public void testAddSecuritRoles() {
    OrganisationUnit ou = new OrganisationUnit().role(new SecurityRole().label("role1")).role(new SecurityRole().label("role2"));

    assertEquals("role1,role2", toJoinedString(ou.roles()));
  }

  @Test
  public void testEmptyReturnsEmptyRoomlist() {
    OrganisationUnit ou = new OrganisationUnit();
    assertNotNull(ou.rooms());
    assertTrue(ou.rooms().size() == 0);
  }

  @Test
  public void testEmptyReturnsEmptyRolelist() {
    OrganisationUnit ou = new OrganisationUnit();
    assertNotNull(ou.roles());
    assertTrue(ou.roles().size() == 0);
  }
}

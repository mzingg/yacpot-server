package com.yacpot.server.model;

import org.junit.Test;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.*;

public class OrganisationUnitTest {

  @Test
  public void testAddRooms() {
    OrganisationUnit ou = new OrganisationUnit().addRoom(new Room().setLabel("Room B")).addRoom(new Room().setLabel("Room A"));

    assertEquals("Room A,Room B", toJoinedString(ou.getRooms()));
  }

  @Test
  public void testAddSecuritRoles() {
    OrganisationUnit ou = new OrganisationUnit().addRole(new SecurityRole().setLabel("role1")).addRole(new SecurityRole().setLabel("role2"));

    assertEquals("role1,role2", toJoinedString(ou.getRoles()));
  }

  @Test
  public void testEmptyReturnsEmptyRoomlist() {
    OrganisationUnit ou = new OrganisationUnit();
    assertNotNull(ou.getRooms());
    assertTrue(ou.getRooms().size() == 0);
  }

  @Test
  public void testEmptyReturnsEmptyRolelist() {
    OrganisationUnit ou = new OrganisationUnit();
    assertNotNull(ou.getRoles());
    assertTrue(ou.getRoles().size() == 0);
  }
}

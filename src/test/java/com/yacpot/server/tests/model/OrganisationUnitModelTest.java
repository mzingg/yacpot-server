package com.yacpot.server.tests.model;

import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.Room;
import com.yacpot.server.model.SecurityRole;
import org.junit.Test;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.assertEquals;

public class OrganisationUnitModelTest {

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

}

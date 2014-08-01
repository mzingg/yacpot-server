package com.yacpot.server.persistence;

import com.yacpot.core.persistence.Persistence;
import com.yacpot.core.persistence.PersistenceTest;
import com.yacpot.server.auth.AuthenticationSession;
import com.yacpot.server.model.*;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestModelPersistence extends PersistenceTest {

  @Test
  public void testSaveAndLoadUser() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      User expected = new User().setEmail("test@test.com");

      persistence.save(expected);

      User testObj = persistence.resolveById(expected.getId(), User.class);

      assertNotNull(testObj);
      assertEquals(expected.getEmail(), testObj.getEmail());
    }
  }

  @Test
  public void testSaveAndLoadSecurityRole() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      SecurityRole expected = new SecurityRole().setDescription("Role Description");

      persistence.save(expected);

      SecurityRole testObj = persistence.resolveById(expected.getId(), SecurityRole.class);

      assertNotNull(testObj);
      assertEquals(expected.getDescription(), testObj.getDescription());
    }
  }

  @Test
  public void testSaveAndLoadEvent() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      Event expected = new Event();

      persistence.save(expected);

      Event testObj = persistence.resolveById(expected.getId(), Event.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
    }
  }

  @Test
  public void testSaveAndLoadOu() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      Event event = new Event();
      event.getTimeline().addIncarnation(new SingleDateIncarnation().setDate(LocalDateTime.now().plusDays(2)));
      Room room = new Room().addEvent(event).addEvent(new Event());
      room.getChannel().setLabel("Channel Label");
      OrganisationUnit expected = new OrganisationUnit().addRoom(room).addRole(new SecurityRole());

      persistence.save(expected);

      OrganisationUnit testObj = persistence.resolveById(expected.getId(), OrganisationUnit.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
      assertEquals("Channel Label", testObj.getRooms().first().getChannel().getLabel());
      assertEquals(expected.getRooms().first().getChannel().getLabel(), testObj.getRooms().first().getChannel().getLabel());
    }
  }

  @Test
  public void testAuthenticationSession() throws Exception {
    try (UserPersistence persistence = new UserPersistence(client, TEST_DATABASE_NAME)) {
      SecurityRole systemRole = new SecurityRole().setLabel("System Role");

      SecurityRole roleA = new SecurityRole().setLabel("Role A");
      SecurityRole roleB = new SecurityRole().setLabel("Role B");
      SecurityRole roleC = new SecurityRole().setLabel("Role C");

      OrganisationUnit ou1 = new OrganisationUnit().setLabel("Organisation 1");
      ou1.addRole(roleA).addRole(roleC);

      OrganisationUnit ou2 = new OrganisationUnit().setLabel("Organisation 2");
      ou2.addRole(roleB);

      persistence.save(systemRole);
      persistence.save(ou1);
      persistence.save(ou2);

      AuthenticationSession session = new AuthenticationSession().addSystemRole(systemRole).setUser(new User().setEmail("some@somwehere.com"));
      session.addRolesInOrganisationUnit(ou1, roleA);
      session.addRolesInOrganisationUnit(ou2, roleB);

      persistence.save(session);

      AuthenticationSession testObj = persistence.resolveById(session.getId(), AuthenticationSession.class);

      assertNotNull(testObj);
      assertEquals("Role A", testObj.getOrganisationUnitsRoles().get(ou1).get(0).getLabel());
    }
  }

}

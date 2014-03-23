package com.yacpot.server.persistence;

import com.mongodb.MongoClient;
import com.yacpot.server.auth.AuthenticationSession;
import com.yacpot.server.model.*;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

public class PersistenceTest {

  private final static String TEST_DATABASE_NAME = "test";

  private static MongoClient client;

  @BeforeClass
  public static void initialize() throws Exception {
    try {
      client = new MongoClient();
      client.getDB("admin").command("ping").throwOnError();

      client.dropDatabase(TEST_DATABASE_NAME);
    } catch (Exception ex) {
      client = null;
    }
  }

  @Before
  public void setUp() throws Exception {
    assumeNotNull(client);
  }

  @Test
  public void testNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      EmptyGenericModel testObj = persistence.resolveById(new GenericModelIdentifier(ObjectId.get().toString()), EmptyGenericModel.class);

      assertNull(testObj);
    }
  }

  @Test
  public void testResolveNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      GenericModel<?> testObj = persistence.resolveById(new GenericModelIdentifier(ObjectId.get().toString()));

      assertNull(testObj);
    }
  }

  @Test
  public void testResolvingById() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      User expected1 = new User().setEmail("test@test.com");
      persistence.save(expected1);

      SecurityRole expected2 = new SecurityRole().setDescription("Role Description");
      persistence.save(expected2);

      GenericModel<?> testObj1 = persistence.resolveById(expected1.getId());
      GenericModel<?> testObj2 = persistence.resolveById(expected2.getId());

      assertTrue(testObj1 instanceof User);
      assertTrue(testObj2 instanceof SecurityRole);
      assertTrue(testObj1.equals(expected1));
      assertTrue(testObj2.equals(expected2));
    }
  }

  @Test
  public void testSaveAndLoadgenericModel() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      EmptyGenericModel expected = new EmptyGenericModel().setLabel("Label").setOrderWeight(20);

      persistence.save(expected);

      EmptyGenericModel testObj = persistence.resolveById(expected.getId(), EmptyGenericModel.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
      assertEquals(expected.getLabel(), testObj.getLabel());
      assertEquals(expected.getOrderWeight(), testObj.getOrderWeight());
      assertEquals(expected.getTimestamp(), testObj.getTimestamp());
    }
  }

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
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
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

      AuthenticationSession session = new AuthenticationSession().addSystemRole(systemRole).setUser(User.ANONYMOUS);
      session.addRolesInOrganisationUnit(ou1, roleA);
      session.addRolesInOrganisationUnit(ou2, roleB);

      persistence.save(session);

      AuthenticationSession testObj = persistence.resolveById(session.getId(), AuthenticationSession.class);

      assertNotNull(testObj);
      assertEquals("Role A", testObj.getOrganisationUnitsRoles().get(ou1).get(0).getLabel());
    }
  }

}

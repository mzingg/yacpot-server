package com.yacpot.server.persistence;

import com.mongodb.MongoClient;
import com.yacpot.server.model.*;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersistenceTest {

  private final static String TEST_DATABASE_NAME = "test";

  private MongoClient client;

  public PersistenceTest() throws Exception {
    this.client = new MongoClient();
  }

  @BeforeClass
  public static void initialize() throws Exception {
    new MongoClient().dropDatabase(TEST_DATABASE_NAME);
  }

  @Test
  public void testNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      EmptyGenericModel testObj = persistence.resolveById(ObjectId.get().toString(), EmptyGenericModel.class);

      assertNull(testObj);
    }
  }

  @Test
  public void testResolveNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new Persistence(client, TEST_DATABASE_NAME)) {
      GenericModel<?> testObj = persistence.resolveById(ObjectId.get().toString());

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
      event.getTimeline().addIncarnation(new SingleDateIncarnation(LocalDateTime.now().plusDays(2)));
      Room room = new Room().addEvent(event).addEvent(new Event());
      room.getChannel().setLabel("Channel Label");
      OrganisationUnit expected = new OrganisationUnit().addRoom(room).addRole(new SecurityRole());

      persistence.save(expected);

      OrganisationUnit testObj = persistence.resolveById(expected.getId(), OrganisationUnit.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
      assertEquals("Channel Label", expected.getRooms().first().getChannel().getLabel());
      assertEquals(expected.getRooms().first().getChannel().getLabel(), testObj.getRooms().first().getChannel().getLabel());
    }
  }
}

package com.yacpot.server.persistence;

import com.mongodb.MongoClient;
import com.yacpot.server.model.EmptyGenericModel;
import com.yacpot.server.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PersistenceTest {

  private final static String TEST_DATABASE_NAME = "test";

  @BeforeClass
  public static void setUp() throws Exception {
    MongoClient client = new MongoClient();
    client.dropDatabase(TEST_DATABASE_NAME);
  }

  @Test
  public void testSaveAndLoadgenericModel() throws Exception {
    MongoClient client = new MongoClient();

    try (Persistence<EmptyGenericModel> persistence = new Persistence<>(client, TEST_DATABASE_NAME)) {
      EmptyGenericModel expected = new EmptyGenericModel().setLabel("Label").setOrderWeight(20);

      persistence.save(expected);

      EmptyGenericModel testObj = persistence.findById(expected.getId(), EmptyGenericModel.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
      assertEquals(expected.getLabel(), testObj.getLabel());
      assertEquals(expected.getOrderWeight(), testObj.getOrderWeight());
      assertEquals(expected.getTimestamp(), testObj.getTimestamp());
    }
  }

  @Test
  public void testSaveAndLoadUser() throws Exception {
    MongoClient client = new MongoClient();

    try (Persistence<User> persistence = new Persistence<>(client, TEST_DATABASE_NAME)) {
      User expected = new User().setEmail("test@test.com");

      persistence.save(expected);

      User testObj = persistence.findById(expected.getId(), User.class);

      assertNotNull(testObj);
      assertEquals(expected.getEmail(), testObj.getEmail());
    }
  }

}

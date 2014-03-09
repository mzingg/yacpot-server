package com.yacpot.server.persistence;

import com.mongodb.MongoClient;
import com.yacpot.server.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PersistenceTest {

  @Test
  public void testSaveAndLoad() throws Exception {

    MongoClient client = new MongoClient();

    try (Persistence<User> persistence = new Persistence<User>(client, "test")) {
      User expected = new User().setEmail("test@test.com").setLabel("Testuser");

      persistence.save(expected);

      User testObj = persistence.findById(expected.getId(), User.class);

      assertNotNull(testObj);
      assertEquals(expected.getEmail(), testObj.getEmail());
    }
  }
}

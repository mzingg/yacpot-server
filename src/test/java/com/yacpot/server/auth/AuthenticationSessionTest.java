package com.yacpot.server.auth;

import com.yacpot.core.persistence.mongodb.MongoDbApplication;
import com.yacpot.server.jetty.JettyRequestFactory;
import com.yacpot.server.model.User;
import com.yacpot.server.persistence.UserPersistence;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static com.yacpot.server.tests.JettyTestUtils.aRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

public class AuthenticationSessionTest {
  private final static String TEST_DATABASE_NAME = "test";

  private static MongoDbApplication application;

  @BeforeClass
  public static void initialize() throws Exception {
    try {
      application = new MongoDbApplication(TEST_DATABASE_NAME);
      application.getMongoClient().dropDatabase(TEST_DATABASE_NAME);

      try (UserPersistence persistence = new UserPersistence(application)) {
        User testuser = new User().setEmail("test@test.local").changePassword("aPassword", null).setLabel("Test User");
        persistence.save(testuser);
      }

    } catch (Exception ex) {
      application = null;
    }
  }

  @Before
  public void setUp() throws Exception {
    assumeNotNull(application);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters1() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, null, null, null);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters2() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, "some@email.org", null, null);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters3() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, "some@email.org", Calendar.getInstance().getTimeInMillis(), null);
  }

  @Test
  public void testAuthentication() throws Exception {
    try (UserPersistence persistence = new UserPersistence(application)) {
      AuthenticationSession testObj = new AuthenticationSession();

      testObj.authenticate(persistence, "test@test.local", 1397653858L, "ff5754533e3914301ba8d1a9c650c762ebb599366540287654763ab2aaa6129d");
      assertEquals("test@test.local", testObj.getUser().getEmail());
    }
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationFailsWithWrongTimestamp() throws Exception {
    try (UserPersistence persistence = new UserPersistence(application)) {
      AuthenticationSession testObj = new AuthenticationSession();

      testObj.authenticate(persistence, "test@test.local", 1397653859L, "ff5754533e3914301ba8d1a9c650c762ebb599366540287654763ab2aaa6129d");
    }
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationFailsWithWrongCode() throws Exception {
    try (UserPersistence persistence = new UserPersistence(application)) {
      AuthenticationSession testObj = new AuthenticationSession();

      testObj.authenticate(persistence, "test@test.local", 1397653858L, "ef5754533e3914301ba8d1a9c650c762ebb599366540287654763ab2aaa6129d");
    }
  }

  @Test
  public void testNoAuthReturnsAnonymousSession() throws Exception {

    JettyRequestFactory factory = new JettyRequestFactory();
    AuthenticationSession session = factory.getSession(aRequest("GET", "/a/getPath"));

    assertEquals(User.ANONYMOUS, session.getUser());
  }

}

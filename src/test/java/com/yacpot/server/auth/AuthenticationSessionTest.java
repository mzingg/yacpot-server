package com.yacpot.server.auth;

import com.mongodb.MongoClient;
import com.yacpot.server.jetty.JettyRequestFactory;
import com.yacpot.server.model.User;
import com.yacpot.server.persistence.UserPersistence;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static com.yacpot.server.tests.jetty.JettyTestUtils.aRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

public class AuthenticationSessionTest {
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

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters1() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, null, -100, null);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters2() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, "some@email.org", 0, null);
  }

  @Test(expected = AuthenticationException.class)
  public void testAuthenticationThrowsExceptionWithInvalidParameters3() throws Exception {
    AuthenticationSession testObj = new AuthenticationSession();
    testObj.authenticate(null, "some@email.org", Calendar.getInstance().getTimeInMillis(), null);
  }

  @Test
  public void testAuthentication() throws Exception {
    try (UserPersistence persistence = new UserPersistence(client, TEST_DATABASE_NAME)) {
      AuthenticationSession testObj = new AuthenticationSession();

      User testuser = new User().setEmail("mzingg@gmx.net").changePassword("761109", null).setLabel("Markus Zingg");
      persistence.save(testuser);

      testObj.authenticate(persistence, "mzingg@gmx.net", 1397222674, "adc07e23903618b1c9ce2853cd10c9f2d88596dcdc285cf417253f7161262aa5fb3c1b7c889ac6eada2dc6794122d0d72f2ca1a9a46d657c39a386c996ca79c3");
    }
  }

  @Test
  public void testNoAuthReturnsAnonymousSession() throws Exception {

    JettyRequestFactory factory = new JettyRequestFactory();
    AuthenticationSession session = factory.getSession(aRequest("GET", "/a/getPath"));

    assertEquals(User.ANONYMOUS, session.getUser());
  }

}

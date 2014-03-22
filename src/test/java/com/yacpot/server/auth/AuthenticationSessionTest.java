package com.yacpot.server.auth;

import com.mongodb.MongoClient;
import com.yacpot.server.jetty.JettyRequestFactory;
import com.yacpot.server.model.EmptyGenericModel;
import com.yacpot.server.model.OrganisationUnit;
import com.yacpot.server.model.SecurityRole;
import com.yacpot.server.model.User;
import com.yacpot.server.persistence.Persistence;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.yacpot.server.tests.jetty.JettyTestUtils.aRequest;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AuthenticationSessionTest {

  private final static String TEST_DATABASE_NAME = "test-session";

  private MongoClient client;

  public AuthenticationSessionTest() throws Exception {
    this.client = new MongoClient();
  }

  @BeforeClass
  public static void initialize() throws Exception {
    new MongoClient().dropDatabase(TEST_DATABASE_NAME);
  }

  @Test
  public void testPersistence() throws Exception {
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

      AuthenticationSession testObj = new AuthenticationSession().addSystemRole(systemRole).setUser(User.ANONYMOUS);
      testObj.addRolesInOrganisationUnit(ou1, roleA);
      testObj.addRolesInOrganisationUnit(ou2, roleB);

      persistence.save(testObj);

      assertNotNull(testObj);
    }
  }

  @Test
  public void testNoAuthReturnsAnonymousSession() throws Exception {

    JettyRequestFactory factory = new JettyRequestFactory();
    AuthenticationSession session = factory.getSession(aRequest("GET", "/a/getPath"));

    Assert.assertEquals(User.ANONYMOUS, session.getUser());
  }

}

package com.yacpot.server.auth;

import com.yacpot.server.jetty.JettyRequestFactory;
import com.yacpot.server.model.User;
import org.junit.Test;

import static com.yacpot.server.tests.jetty.JettyTestUtils.aRequest;
import static org.junit.Assert.assertEquals;

public class AuthenticationSessionTest {

  @Test
  public void testNoAuthReturnsAnonymousSession() throws Exception {

    JettyRequestFactory factory = new JettyRequestFactory();
    AuthenticationSession session = factory.getSession(aRequest("GET", "/a/getPath"));

    assertEquals(User.ANONYMOUS, session.getUser());
  }

}

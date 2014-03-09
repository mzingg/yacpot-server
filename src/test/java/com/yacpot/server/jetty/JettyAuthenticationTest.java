package com.yacpot.server.jetty;

import com.yacpot.server.model.User;
import org.junit.Assert;
import org.junit.Test;

import static com.yacpot.server.tests.jetty.JettyTestUtils.aRequest;

public class JettyAuthenticationTest {

  @Test
  public void testNoAuthReturnsAnonymousSession() throws Exception {

    JettyRequestFactory factory = new JettyRequestFactory();
    JettyAuthenticationSession session = factory.getSession(aRequest("GET", "/a/path"));

    Assert.assertEquals(User.ANONYMOUS, session.user());
  }
}

package com.yacpot.server.tests.jetty;

import com.yacpot.server.jetty.JettyAuthenticationTest;
import com.yacpot.server.jetty.JettyRequestResolvingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({JettyAuthenticationTest.class, JettyRequestResolvingTest.class})
public class AllTests {
}

package com.yacpot.server.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllModelTests.class, AllRestTests.class, AllAuthTests.class, AllJettyTests.class})
public class ServerTests {
}

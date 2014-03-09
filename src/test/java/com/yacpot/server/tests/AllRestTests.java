package com.yacpot.server.tests;

import com.yacpot.server.jetty.JettyRequestResolvingTest;
import com.yacpot.server.rest.ApplicationMappingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationMappingTest.class, JettyRequestResolvingTest.class})
public class AllRestTests {
}

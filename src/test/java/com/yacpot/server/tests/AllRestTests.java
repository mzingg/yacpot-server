package com.yacpot.server.tests;

import com.yacpot.server.rest.ApplicationMappingTest;
import com.yacpot.server.rest.JettyRequestResolvingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationMappingTest.class, JettyRequestResolvingTest.class})
public class AllRestTests {
}

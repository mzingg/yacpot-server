package com.yacpot.server.tests;

import com.yacpot.server.persistence.PersistenceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AllModelTests.class, AllRestTests.class, PersistenceTest.class, AllAuthTests.class, com.yacpot.server.tests.jetty.AllTests.class})
public class AllTests {
}

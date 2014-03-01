package com.yacpot.server.tests;

import com.yacpot.server.tests.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({com.yacpot.server.tests.model.AllTests.class, com.yacpot.server.tests.rest.AllTests.class})
public class AllTests {
}

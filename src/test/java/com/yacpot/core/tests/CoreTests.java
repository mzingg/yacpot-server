package com.yacpot.core.tests;

import com.yacpot.core.persistence.PersistenceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllModelTests.class, PersistenceTest.class})
public class CoreTests {
}

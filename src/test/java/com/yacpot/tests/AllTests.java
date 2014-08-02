package com.yacpot.tests;

import com.yacpot.community.tests.CommunityTests;
import com.yacpot.core.tests.CoreTests;
import com.yacpot.server.tests.ServerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CoreTests.class, ServerTests.class, CommunityTests.class})
public class AllTests {
}

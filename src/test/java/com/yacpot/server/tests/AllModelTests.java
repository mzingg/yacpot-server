package com.yacpot.server.tests;

import com.yacpot.server.model.SecurityRoleTest;
import com.yacpot.server.persistence.TestModelPersistence;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({SecurityRoleTest.class, TestModelPersistence.class})
public class AllModelTests {
}

package com.yacpot.server.tests;

import com.yacpot.server.model.EventTest;
import com.yacpot.server.model.OrganisationUnitTest;
import com.yacpot.server.model.RoomTest;
import com.yacpot.server.model.SecurityRoleTest;
import com.yacpot.server.persistence.TestModelPersistence;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OrganisationUnitTest.class, RoomTest.class, EventTest.class, SecurityRoleTest.class, TestModelPersistence.class})
public class AllModelTests {
}

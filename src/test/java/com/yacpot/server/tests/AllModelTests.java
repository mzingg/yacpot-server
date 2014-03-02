package com.yacpot.server.tests;

import com.yacpot.server.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OrganisationUnitTest.class, GenericModelTest.class, RoomTest.class, EventTest.class, SecurityRoleTest.class})
public class AllModelTests {
}

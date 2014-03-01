package com.yacpot.server.tests;

import com.yacpot.server.tests.model.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({OrganisationUnitModelTest.class, GenericModelTest.class, RoomModelTest.class, EventModelTest.class, SecurityRoleTest.class})
public class AllTests {
}

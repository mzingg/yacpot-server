package com.yacpot.server.tests;

import com.yacpot.server.tests.model.EventModelTest;
import com.yacpot.server.tests.model.GenericModelTest;
import com.yacpot.server.tests.model.OrganisationUnitModelTest;
import com.yacpot.server.tests.model.RoomModelTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({OrganisationUnitModelTest.class, GenericModelTest.class, RoomModelTest.class, EventModelTest.class})
public class AllTests {
}

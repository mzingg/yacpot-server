package com.yacpot.community.tests;

import com.yacpot.community.model.EventTest;
import com.yacpot.community.model.OrganisationUnitTest;
import com.yacpot.community.model.RoomTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OrganisationUnitTest.class, RoomTest.class, EventTest.class})
public class CommunityTests {
}

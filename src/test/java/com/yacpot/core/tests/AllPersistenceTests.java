package com.yacpot.core.tests;

import com.yacpot.core.persistence.mongodb.MongoDbIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MongoDbIntegrationTest.class})
public class AllPersistenceTests {
}

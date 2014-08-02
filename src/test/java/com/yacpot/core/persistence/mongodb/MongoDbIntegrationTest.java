package com.yacpot.core.persistence.mongodb;

import com.yacpot.core.model.GenericModel;
import com.yacpot.core.model.ModelIdentifier;
import com.yacpot.core.persistence.Persistence;
import com.yacpot.core.persistence.TestModel;
import com.yacpot.core.persistence.TestSubModelOne;
import com.yacpot.core.persistence.TestSubModelTwo;
import com.yacpot.core.persistence.mongodb.MongoDbApplication;
import com.yacpot.core.persistence.mongodb.MongoDbPersistence;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

public class MongoDbIntegrationTest {

  private final static String TEST_DATABASE_NAME = "test";

  protected static MongoDbApplication application;

  @BeforeClass
  public static void initialize() throws Exception {
    try {
      application = new MongoDbApplication(TEST_DATABASE_NAME);
      application.getMongoClient().dropDatabase(TEST_DATABASE_NAME);
    } catch (Exception ex) {
      application = null;
    }
  }

  @Before
  public void setUp() throws Exception {
    assumeNotNull(application);
  }

  @Test
  public void testNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel testObj = persistence.resolveById(new ModelIdentifier(ObjectId.get().toString()), TestModel.class);

      assertNull(testObj);
    }
  }

  @Test
  public void testResolveNotFoundReturnNull() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      GenericModel<?> testObj = persistence.resolveById(new ModelIdentifier(ObjectId.get().toString()));

      assertNull(testObj);
    }
  }

  @Test
  public void testResolvingById() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel expected1 = new TestModel().setLabel("Expected 1");
      persistence.save(expected1);

      TestSubModelOne expected2 = new TestSubModelOne().setLabel("Expected 2");
      persistence.save(expected2);

      GenericModel<?> testObj1 = persistence.resolveById(expected1.getId());
      GenericModel<?> testObj2 = persistence.resolveById(expected2.getId());

      assertTrue(testObj1 instanceof TestModel);
      assertTrue(testObj2 instanceof TestSubModelOne);
      assertTrue(testObj1.equals(expected1));
      assertTrue(testObj2.equals(expected2));
    }
  }

  @Test
  public void testSaveAndLoadgenericModel() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      LocalDateTime timestamp = LocalDateTime.now();
      TestModel expected = new TestModel().setLabel("Label").setOrderWeight(20).setTimestamp(timestamp);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertEquals(expected.getId(), testObj.getId());
      assertEquals("Label", testObj.getLabel());
      assertEquals(20, testObj.getOrderWeight());
      assertEquals(timestamp, testObj.getTimestamp());
    }
  }

  @Test
  public void testSaveAndLoadPrimitiveDataTypes() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel expected = new TestModel();
      expected.setByteValue(Byte.MAX_VALUE).setShortValue(Short.MAX_VALUE).setIntValue(Integer.MAX_VALUE).setLongValue(Long.MAX_VALUE);
      expected.setFloatValue(Float.MAX_VALUE).setDoubleValue(Double.MAX_VALUE);
      expected.setBoolValue(true);
      expected.setCharValue('z').setStringValue("aString");

      Date expectedDate = new Date();
      LocalDateTime expectedJodaTime = new LocalDateTime(2001, 5, 20, 10, 30);
      expected.setDateValue(expectedDate).setJodaTimeValue(expectedJodaTime);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertEquals(Byte.MAX_VALUE, testObj.getByteValue());
      assertEquals(Short.MAX_VALUE, testObj.getShortValue());
      assertEquals(Integer.MAX_VALUE, testObj.getIntValue());
      assertEquals(Long.MAX_VALUE, testObj.getLongValue());
      assertEquals(Float.MAX_VALUE, testObj.getFloatValue(), 1e-15);
      assertEquals(Double.MAX_VALUE, testObj.getDoubleValue(), 1e-15);
      assertTrue(testObj.isBoolValue());
      assertEquals('z', testObj.getCharValue());
      assertEquals("aString", testObj.getStringValue());
      assertEquals(expectedDate, testObj.getDateValue());
      assertEquals(expectedJodaTime, testObj.getJodaTimeValue());
    }
  }

  @Test
  public void testSaveAndLoadSimpleCollectionsAndMaps() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel expected = new TestModel();

      List<String> expectedList = new ArrayList<>();
      expectedList.add("a");
      expectedList.add("b");
      expectedList.add("c");
      expectedList.add("d");
      expected.setSimpleList(expectedList);

      Map<Integer, String> expectedMap = new HashMap<>();
      expectedMap.put(1, "One");
      expectedMap.put(2, "Two");
      expected.setSimpleMap(expectedMap);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertEquals("a>b>c>d", StringUtils.join(testObj.getSimpleList(), ">"));
      assertTrue(testObj.getSimpleMap().size() == 2);
      assertEquals("Two", testObj.getSimpleMap().get(2));
    }
  }

  @Test
  public void testSubmodelCollection() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel expected = new TestModel();

      TestSubModelOne one = new TestSubModelOne().setLabel("Sub One");
      TestSubModelOne two = new TestSubModelOne().setLabel("Sub Two");
      two.setSelfReference(one);

      expected.addSubModel(one).addSubModel(two);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertTrue(testObj.getSubModels().size() == 2);
      Iterator<TestSubModelOne> it = testObj.getSubModels().iterator();
      TestSubModelOne next = it.next();
      assertEquals("Sub One", next.getLabel());
      next = it.next();
      assertEquals("Sub Two", next.getLabel());
      assertEquals(one, next.getSelfReference());
    }
  }

  @Test
  public void testSaveAndLoadSubModel() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {
      TestModel expected = new TestModel().setSubModelValue(new TestSubModelOne().setLabel("Submodel Label"));

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertNotNull(testObj.getSubModelValue());
      assertEquals("Submodel Label", testObj.getSubModelValue().getLabel());
    }
  }

  @Test
  public void testStringToOneMap() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {

      Map<String, TestSubModelOne> stringKeyMap = new HashMap<>();
      stringKeyMap.put("one", new TestSubModelOne().setLabel("one"));
      stringKeyMap.put("two", new TestSubModelOne().setLabel("two").setSelfReference(stringKeyMap.get("one")));
      TestModel expected = new TestModel().setStringKeyMap(stringKeyMap);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertNotNull(testObj.getStringKeyMap());
      assertNotNull(testObj.getStringKeyMap().size() == 2);
      assertEquals("one", testObj.getStringKeyMap().get("one").getLabel());
    }
  }

  @Test
  public void testOneToOneMap() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {

      TestSubModelOne expectedKey = new TestSubModelOne().setLabel("two");

      Map<TestSubModelOne, TestSubModelTwo> oneToOneMap = new HashMap<>();
      oneToOneMap.put(new TestSubModelOne().setLabel("one"), new TestSubModelTwo().setLabel("valueOne"));
      oneToOneMap.put(expectedKey, new TestSubModelTwo().setLabel("valueTwo"));
      oneToOneMap.put(new TestSubModelOne().setLabel("three"), new TestSubModelTwo().setLabel("valueThree"));
      TestModel expected = new TestModel().setModelKeyMap(oneToOneMap);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertNotNull(testObj.getModelKeyMap());
      assertNotNull(testObj.getModelKeyMap().size() == 3);
      assertEquals("valueTwo", testObj.getModelKeyMap().get(expectedKey).getLabel());
    }
  }

  @Test
  public void testOneToManyMap() throws Exception {
    try (Persistence persistence = new MongoDbPersistence(application)) {

      TestSubModelOne expectedKey = new TestSubModelOne().setLabel("two");

      Map<TestSubModelOne, Collection<TestSubModelTwo>> oneToManyMap = new HashMap<>();
      Collection<TestSubModelTwo> l1 = new ArrayList<>();
      l1.add(new TestSubModelTwo().setLabel("valueOne"));
      oneToManyMap.put(new TestSubModelOne().setLabel("one"), l1);
      Collection<TestSubModelTwo> l2 = new ArrayList<>();
      l2.add(new TestSubModelTwo().setLabel("valueOne"));
      l2.add(new TestSubModelTwo().setLabel("valueTwo"));
      oneToManyMap.put(expectedKey, l2);

      TestModel expected = new TestModel().setModelKeyMapWithListValue(oneToManyMap);

      persistence.save(expected);

      TestModel testObj = persistence.resolveById(expected.getId(), TestModel.class);

      assertNotNull(testObj);
      assertNotNull(testObj.getModelKeyMapWithListValue());
      assertNotNull(testObj.getModelKeyMapWithListValue().size() == 2);
      Iterator<TestSubModelTwo> it = testObj.getModelKeyMapWithListValue().get(expectedKey).iterator();
      assertEquals("valueOne", it.next().getLabel());
      assertEquals("valueTwo", it.next().getLabel());
    }
  }

}

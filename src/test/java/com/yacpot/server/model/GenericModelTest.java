package com.yacpot.server.model;

import com.yacpot.server.model.sort.GenericModelComparator;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.*;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GenericModelTest {

  @Test
  public void testEmptyReturnsNonNullObjects() {
    GenericModel im = new AbstractGenericModel() {
    };

    assertNotNull(im.getId());
    assertNotNull(im.getLabel());
    assertNotNull(im.getTimestamp());
  }

  @Test
  public void testEqualityOnlyWithId() {
    String aUUID = UUID.randomUUID().toString();
    GenericModel aIdentityObject = new AbstractGenericModel() {
    }.setId(aUUID).setLabel("Label is ignored");
    GenericModel anotherIdentityObject = new AbstractGenericModel() {
    }.setId(aUUID).setOrderWeight(10);

    assertEquals(aIdentityObject, anotherIdentityObject);
    assertEquals(aIdentityObject.hashCode(), anotherIdentityObject.hashCode());
  }

  @Test
  public void testSetterChaining() {
    GenericModel aIdentityObject = new AbstractGenericModel() {
    }.setLabel("Label").setOrderWeight(10);

    assertEquals("Label", aIdentityObject.getLabel());
    assertEquals(10, aIdentityObject.getOrderWeight());
  }

  @Test
  public void sortByLabelAscTest() throws InterruptedException {
    Collection<GenericModel> orderedList1 = getIdentityModels(new GenericModelComparator<>().byLabel());
    Collection<GenericModel> orderedList2 = getIdentityModels(new GenericModelComparator<>().byLabel().ascending());

    assertEquals("A,E,Y,Z", toJoinedString(orderedList1));
    assertEquals("A,E,Y,Z", toJoinedString(orderedList2));
  }

  @Test
  public void sortByLabelDescTest() throws InterruptedException {
    Collection<GenericModel> orderedList = getIdentityModels(new GenericModelComparator<>().byLabel().descending());

    assertEquals("Z,Y,E,A", toJoinedString(orderedList));
  }

  @Test
  public void sortByWeightAscTest() throws InterruptedException {
    Collection<GenericModel> orderedList1 = getIdentityModels(new GenericModelComparator<>().byWeight());
    Collection<GenericModel> orderedList2 = getIdentityModels(new GenericModelComparator<>().byWeight().ascending());

    assertEquals("Y,A,Z,E", toJoinedString(orderedList1));
    assertEquals("Y,A,Z,E", toJoinedString(orderedList2));
  }

  @Test
  public void sortByWeightDescTest() throws InterruptedException {
    Collection<GenericModel> orderedList = getIdentityModels(new GenericModelComparator<>().byWeight().descending());

    assertEquals("E,Z,A,Y", toJoinedString(orderedList));
  }

  @Test
  public void sortByTimestampAsc() throws InterruptedException {
    Collection<GenericModel> orderedList1 = getIdentityModels(new GenericModelComparator<>().byTimestamp());
    Collection<GenericModel> orderedList2 = getIdentityModels(new GenericModelComparator<>().byTimestamp().ascending());

    assertEquals("A,Z,E,Y", toJoinedString(orderedList1));
    assertEquals("A,Z,E,Y", toJoinedString(orderedList2));
  }

  @Test
  public void sortByTimestampDesc() throws InterruptedException {
    Collection<GenericModel> orderedList = getIdentityModels(new GenericModelComparator<>().byTimestamp().descending());

    assertEquals("Y,E,Z,A", toJoinedString(orderedList));
  }

  @Test
  public void sortByNoneTest() throws InterruptedException {
    Collection<GenericModel> orderedList1 = getIdentityModels(new GenericModelComparator<>());
    Collection<GenericModel> orderedList2 = getIdentityModels(new GenericModelComparator<>().withoutOrder());
    Collection<GenericModel> orderedList3 = getIdentityModels(new GenericModelComparator<>().withoutOrder().ascending());
    Collection<GenericModel> orderedList4 = getIdentityModels(new GenericModelComparator<>().withoutOrder().descending());

    assertEquals("A,Z,E,Y", toJoinedString(orderedList1));
    assertEquals("A,Z,E,Y", toJoinedString(orderedList2));
    assertEquals("A,Z,E,Y", toJoinedString(orderedList3));
    assertEquals("A,Z,E,Y", toJoinedString(orderedList4));
  }

  private Collection<GenericModel> getIdentityModels(Comparator<GenericModel> comparator) {
    List<GenericModel> orderedList = new ArrayList<>();
    orderedList.add(new AbstractGenericModel() {
    }.setLabel("A").setOrderWeight(10).setTimestamp(new LocalDateTime(2014, 2, 22, 14, 0, 0)));
    orderedList.add(new AbstractGenericModel() {
    }.setLabel("Z").setOrderWeight(50).setTimestamp(new LocalDateTime(2014, 2, 22, 15, 0, 0)));
    orderedList.add(new AbstractGenericModel() {
    }.setLabel("E").setOrderWeight(70).setTimestamp(new LocalDateTime(2014, 2, 22, 16, 0, 0)));
    orderedList.add(new AbstractGenericModel() {
    }.setLabel("Y").setOrderWeight(5).setTimestamp(new LocalDateTime(2014, 2, 22, 17, 0, 0)));

    Collections.sort(orderedList, comparator);
    return orderedList;
  }

}
package com.yacpot.server.tests.model;

import com.yacpot.server.model.DateIntervalIncarnation;
import com.yacpot.server.model.Event;
import com.yacpot.server.model.EventTimeline;
import com.yacpot.server.model.SingleDateIncarnation;
import com.yacpot.server.model.sort.EventModelComparator;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventModelTest {

  @Test
  public void testEmptyEventReturnsNonNullTimeline() {
    Event testObj = new Event();
    assertNotNull(testObj.timeline());
  }

  @Test
  public void testTimelineInheritsTimestamp() {
    LocalDateTime timestamp = new LocalDateTime(2014, 1, 10, 10, 0);
    Event testObj = new Event().timestamp(timestamp);
    assertEquals(testObj.timestamp(), testObj.timeline().timestamp());
  }

  @Test
  public void testEventComparatorDisableEventsorting() {
    EventModelComparator testObj = new EventModelComparator().sortByEventDate(false).byLabel();
    Set<Event> testList = new TreeSet<>(testObj);
    testList.add(new Event().label("Event Z"));
    testList.add(new Event().label("Event A"));

    assertEquals("Event A,Event Z", toJoinedString(testList));
  }

  @Test
  public void testTimelineSortOneIncarnationsreturnDateOfIt() throws Exception {
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 4, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new DateIntervalIncarnation(first, first.plusDays(1)));

    assertEquals(first, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAllDatesBeforeAnchor() throws Exception {
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 4, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(third, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAllDatesAfterAnchor() {
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2013, 12, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(first, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesBefore() {
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 9, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(second, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesAfter() {
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 11, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(third, testObj.sortDate(anchor));
  }
}

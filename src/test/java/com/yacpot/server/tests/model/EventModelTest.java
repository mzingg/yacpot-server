package com.yacpot.server.tests.model;

import com.yacpot.server.model.DateIntervalIncarnation;
import com.yacpot.server.model.Event;
import com.yacpot.server.model.EventTimeline;
import com.yacpot.server.model.SingleDateIncarnation;
import com.yacpot.server.model.sort.EventModelComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.*;

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
    LocalDateTime third = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2013, 12, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(first, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesBefore() {
    LocalDateTime second = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 9, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(second, testObj.sortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesAfter() {
    LocalDateTime third = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 11, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new SingleDateIncarnation(first)).incarnation(new SingleDateIncarnation(second)).incarnation(new SingleDateIncarnation(third));

    assertEquals(third, testObj.sortDate(anchor));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntervalThrowsExceptionWhenDatesAreNotConsecutive() throws Exception {
    LocalDate intervalStart = new LocalDate(2014, 1, 10);
    LocalDate intervalEnd = intervalStart.plusDays(2);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new DateIntervalIncarnation(intervalStart.toLocalDateTime(new LocalTime(0, 0)), intervalEnd.toLocalDateTime(new LocalTime(0, 0))));

    testObj.hasIncarnationsDuring(intervalStart.plusDays(1), intervalStart);
  }

  @Test
  public void testTakesPlaceBounds() {
    LocalDate intervalStart = new LocalDate(2014, 1, 10);
    LocalDate intervalEnd = intervalStart.plusDays(2);

    EventTimeline testObj = new EventTimeline();
    testObj.incarnation(new DateIntervalIncarnation(intervalStart.toLocalDateTime(new LocalTime(0, 0)), intervalEnd.toLocalDateTime(new LocalTime(0, 0))));

    assertFalse(testObj.hasIncarnationsDuring(intervalStart.minusDays(1), intervalStart));
    assertTrue(testObj.hasIncarnationsDuring(intervalStart, intervalStart.plusDays(5)));
    assertTrue(testObj.hasIncarnationsDuring(intervalEnd.minusDays(10), intervalEnd));
    assertFalse(testObj.hasIncarnationsDuring(intervalEnd.plusDays(1), intervalEnd.plusDays(2)));
  }
}

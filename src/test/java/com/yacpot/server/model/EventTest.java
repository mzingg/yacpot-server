package com.yacpot.server.model;

import com.yacpot.server.model.sort.EventModelComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static com.yacpot.core.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.*;

public class EventTest {

  @Test
  public void testEmptyEventReturnsNonNullTimeline() {
    Event testObj = new Event();
    assertNotNull(testObj.getTimeline());
  }

  @Test
  public void testTimelineInheritsTimestamp() {
    LocalDateTime timestamp = new LocalDateTime(2014, 1, 10, 10, 0);
    Event testObj = new Event().setTimestamp(timestamp);
    assertEquals(testObj.getTimestamp(), testObj.getTimeline().getTimestamp());
  }

  @Test
  public void testEventComparatorDisableEventsorting() {
    EventModelComparator testObj = new EventModelComparator().sortByEventDate(false).byLabel();
    Set<Event> testList = new TreeSet<>(testObj);
    testList.add(new Event().setLabel("Event Z"));
    testList.add(new Event().setLabel("Event A"));

    assertEquals("Event A,Event Z", toJoinedString(testList));
  }

  @Test
  public void testTimelineSortOneIncarnationsreturnDateOfIt() throws Exception {
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 4, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new DateIntervalIncarnation().setStartDate(first).setEndDate(first.plusDays(1)));

    assertEquals(first, testObj.getSortDate(anchor));
  }

  @Test
  public void testTimelineSortAllDatesBeforeAnchor() throws Exception {
    LocalDateTime first = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 4, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new SingleDateIncarnation().setDate(first)).addIncarnation(new SingleDateIncarnation().setDate(second)).addIncarnation(new SingleDateIncarnation().setDate(third));

    assertEquals(third, testObj.getSortDate(anchor));
  }

  @Test
  public void testTimelineSortAllDatesAfterAnchor() {
    LocalDateTime third = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2013, 12, 10, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new SingleDateIncarnation().setDate(first)).addIncarnation(new SingleDateIncarnation().setDate(second)).addIncarnation(new SingleDateIncarnation().setDate(third));

    assertEquals(first, testObj.getSortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesBefore() {
    LocalDateTime second = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime third = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 9, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new SingleDateIncarnation().setDate(first)).addIncarnation(new SingleDateIncarnation().setDate(second)).addIncarnation(new SingleDateIncarnation().setDate(third));

    assertEquals(second, testObj.getSortDate(anchor));
  }

  @Test
  public void testTimelineSortAnchorBetweenDatesAfter() {
    LocalDateTime third = new LocalDateTime(2014, 1, 10, 10, 0);
    LocalDateTime second = new LocalDateTime(2014, 2, 10, 10, 0);
    LocalDateTime first = new LocalDateTime(2014, 3, 10, 10, 0);

    LocalDateTime anchor = new LocalDateTime(2014, 2, 11, 10, 0);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new SingleDateIncarnation().setDate(first)).addIncarnation(new SingleDateIncarnation().setDate(second)).addIncarnation(new SingleDateIncarnation().setDate(third));

    assertEquals(third, testObj.getSortDate(anchor));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIntervalThrowsExceptionWhenDatesAreNotConsecutive() throws Exception {
    LocalDate intervalStart = new LocalDate(2014, 1, 10);
    LocalDate intervalEnd = intervalStart.plusDays(2);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new DateIntervalIncarnation().setStartDate(intervalStart.toLocalDateTime(new LocalTime(0, 0))).setEndDate(intervalEnd.toLocalDateTime(new LocalTime(0, 0))));

    testObj.hasIncarnationsDuring(intervalStart.plusDays(1), intervalStart);
  }

  @Test
  public void testTakesPlaceBounds() {
    LocalDate intervalStart = new LocalDate(2014, 1, 10);
    LocalDate intervalEnd = intervalStart.plusDays(2);

    EventTimeline testObj = new EventTimeline();
    testObj.addIncarnation(new DateIntervalIncarnation().setStartDate(intervalStart.toLocalDateTime(new LocalTime(0, 0))).setEndDate(intervalEnd.toLocalDateTime(new LocalTime(0, 0))));

    assertFalse(testObj.hasIncarnationsDuring(intervalStart.minusDays(1), intervalStart));
    assertTrue(testObj.hasIncarnationsDuring(intervalStart, intervalStart.plusDays(5)));
    assertTrue(testObj.hasIncarnationsDuring(intervalEnd.minusDays(10), intervalEnd));
    assertFalse(testObj.hasIncarnationsDuring(intervalEnd.plusDays(1), intervalEnd.plusDays(2)));
  }
}

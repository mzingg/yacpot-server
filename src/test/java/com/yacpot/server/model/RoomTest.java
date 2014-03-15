package com.yacpot.server.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static com.yacpot.server.tests.ModelTestUtil.toJoinedString;
import static org.junit.Assert.*;

public class RoomTest {

  @Test
  public void testChannelNotNull() {
    Room room = new Room();

    assertNotNull(room.getChannel());
  }

  @Test
  public void testAddEmptyEventsSortByLabel() {
    Room room = new Room();
    room.addEvent(new Event().setLabel("Event Z"));
    room.addEvent(new Event().setLabel("Event B"));

    assertEquals("Event B,Event Z", toJoinedString(room.getEvents()));
  }

  @Test
  public void testEventsSortedByIncarnationSortDate() {
    Event first = new Event().setLabel("Event Z");
    first.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 1, 10, 10, 0)));

    Event second = new Event().setLabel("Event A");
    second.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 3, 10, 10, 0)));

    Event third = new Event().setLabel("Event D");
    third.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 2, 10, 10, 0)));

    LocalDateTime anchor = new LocalDateTime(2014, 2, 10, 10, 0);

    Room room = new Room().addEvent(first).addEvent(second).addEvent(third).setEventSortAnchorDate(anchor);

    assertEquals("Event Z,Event D,Event A", toJoinedString(room.getEvents()));
  }

  @Test
  public void testEmptyEventsSortedByTimestamp() {
    Event first = new Event().setLabel("Event Z").setTimestamp(new LocalDateTime(2014, 1, 10, 10, 0));

    Event second = new Event().setLabel("Event A").setTimestamp(new LocalDateTime(2014, 3, 10, 10, 0));

    Event third = new Event().setLabel("Event D").setTimestamp(new LocalDateTime(2014, 2, 10, 10, 0));

    LocalDateTime anchor = new LocalDateTime(2014, 2, 10, 10, 0);

    Room room = new Room().addEvent(first).addEvent(second).addEvent(third).setEventSortAnchorDate(anchor);

    assertEquals("Event Z,Event D,Event A", toJoinedString(room.getEvents()));
  }

  @Test
  public void testEmptyEventReturnsEmptyCalenderList() {
    Room room = new Room();
    LocalDate calendarStart = new LocalDate(2014, 1, 10);
    assertTrue(room.getCalendar(calendarStart, calendarStart.plusWeeks(1)).size() == 0);
  }

  @Test
  public void testCalendarList() {
    Event first = new Event().setLabel("Event Z");
    first.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 1, 10, 10, 0)));

    Event second = new Event().setLabel("Event A");
    second.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 3, 10, 10, 0)));

    Event third = new Event().setLabel("Event D");
    third.getTimeline().addIncarnation(new SingleDateIncarnation(new LocalDateTime(2014, 2, 9, 10, 0)));

    Room room = new Room().addEvent(first).addEvent(second).addEvent(third);

    LocalDate calendarStart = new LocalDate(2014, 1, 10);

    assertEquals("Event Z,Event D", toJoinedString(room.getCalendar(calendarStart, calendarStart.plusMonths(1))));
  }
}

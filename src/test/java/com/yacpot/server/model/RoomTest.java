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

    assertNotNull(room.channel());
  }

  @Test
  public void testAddEmptyEventsSortByLabel() {
    Room room = new Room();
    room.event(new Event().label("Event Z"));
    room.event(new Event().label("Event B"));

    assertEquals("Event B,Event Z", toJoinedString(room.events()));
  }

  @Test
  public void testEventsSortedByIncarnationSortDate() {
    Event first = new Event().label("Event Z");
    first.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 1, 10, 10, 0)));

    Event second = new Event().label("Event A");
    second.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 3, 10, 10, 0)));

    Event third = new Event().label("Event D");
    third.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 2, 10, 10, 0)));

    LocalDateTime anchor = new LocalDateTime(2014, 2, 10, 10, 0);

    Room room = new Room().event(first).event(second).event(third).eventSortAnchorDate(anchor);

    assertEquals("Event Z,Event D,Event A", toJoinedString(room.events()));
  }

  @Test
  public void testEmptyEventsSortedByTimestamp() {
    Event first = new Event().label("Event Z").timestamp(new LocalDateTime(2014, 1, 10, 10, 0));

    Event second = new Event().label("Event A").timestamp(new LocalDateTime(2014, 3, 10, 10, 0));

    Event third = new Event().label("Event D").timestamp(new LocalDateTime(2014, 2, 10, 10, 0));

    LocalDateTime anchor = new LocalDateTime(2014, 2, 10, 10, 0);

    Room room = new Room().event(first).event(second).event(third).eventSortAnchorDate(anchor);

    assertEquals("Event Z,Event D,Event A", toJoinedString(room.events()));
  }

  @Test
  public void testEmptyEventReturnsEmptyCalenderList() {
    Room room = new Room();
    LocalDate calendarStart = new LocalDate(2014, 1, 10);
    assertTrue(room.calendar(calendarStart, calendarStart.plusWeeks(1)).size() == 0);
  }

  @Test
  public void testCalendarList() {
    Event first = new Event().label("Event Z");
    first.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 1, 10, 10, 0)));

    Event second = new Event().label("Event A");
    second.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 3, 10, 10, 0)));

    Event third = new Event().label("Event D");
    third.timeline().incarnation(new SingleDateIncarnation(new LocalDateTime(2014, 2, 9, 10, 0)));

    Room room = new Room().event(first).event(second).event(third);

    LocalDate calendarStart = new LocalDate(2014, 1, 10);

    assertEquals("Event Z,Event D", toJoinedString(room.calendar(calendarStart, calendarStart.plusMonths(1))));
  }
}

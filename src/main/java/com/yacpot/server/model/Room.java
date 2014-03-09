package com.yacpot.server.model;

import com.yacpot.server.model.sort.EventModelComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.*;

public class Room extends AbstractGenericModel<Room> {

  private Channel channel;

  private final Set<Event> eventSet;

  private final EventModelComparator comparator;

  public Room() {
    super();
    this.channel = new Channel();
    this.comparator = new EventModelComparator();
    this.eventSet = new TreeSet<>(comparator);
  }

  public Room setEventSortAnchorDate(LocalDateTime anchorDate) {
    comparator.anchorDate(anchorDate);
    return this;
  }

  public Channel getChannel() {
    return channel;
  }

  public Room addEvent(Event event) {
    this.eventSet.add(event);
    return this;
  }

  public Collection<Event> getEvents() {
    return Collections.unmodifiableSet(eventSet);
  }

  public Collection<Event> getCalendar(LocalDate fromDate, LocalDate toDate) {
    Collection<Event> result = new ArrayList<>();
    for (Event e : getEvents()) {
      if (e.getTimeline().hasIncarnationsDuring(fromDate, toDate)) {
        result.add(e);
      }
    }
    return result;
  }

}
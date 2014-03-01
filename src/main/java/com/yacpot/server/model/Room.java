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

  public Room eventSortAnchorDate(LocalDateTime anchorDate) {
    comparator.anchorDate(anchorDate);
    return this;
  }

  public Channel channel() {
    return channel;
  }

  public Room event(Event event) {
    this.eventSet.add(event);
    return this;
  }

  public Collection<Event> events() {
    return Collections.unmodifiableSet(eventSet);
  }

  public Collection<Event> calendar(LocalDate fromDate, LocalDate toDate) {
    Collection<Event> result = new ArrayList<>();
    for (Event e : events()) {
      if (e.timeline().hasIncarnationsDuring(fromDate, toDate)) {
        result.add(e);
      }
    }
    return result;
  }

}
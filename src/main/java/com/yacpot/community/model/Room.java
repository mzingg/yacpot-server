package com.yacpot.community.model;

import com.yacpot.core.model.AbstractModel;
import com.yacpot.community.model.sort.EventModelComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Room extends AbstractModel<Room> {

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

  public Room setChannel(Channel channel) {
    this.channel = channel;
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
    return getEvents().stream().filter(e -> e.getTimeline().hasIncarnationsDuring(fromDate, toDate)).collect(Collectors.toList());
  }

}
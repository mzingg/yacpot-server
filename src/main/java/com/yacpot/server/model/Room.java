package com.yacpot.server.model;

import com.yacpot.server.model.sort.EventModelComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Room extends GenericModel<Room> {

  private Channel channel;

  private final Set<Event> eventSet;

  public Room() {
    super();
    this.channel = new Channel();
    this.eventSet = new TreeSet<>(new EventModelComparator());
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
}
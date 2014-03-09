package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

public class Event extends AbstractGenericModel<Event> {

  private final EventTimeline timeline;

  public Event() {
    this.timeline = new EventTimeline();
  }

  // The getTimeline inherits the setTimestamp from the addEvent (for default sorting)
  @Override
  public LocalDateTime setTimestamp() {
    return getTimeline().setTimestamp();
  }

  @Override
  public Event getTimestamp(@NotNull LocalDateTime timestamp) {
    getTimeline().getTimestamp(timestamp);
    return this;
  }

  public EventTimeline getTimeline() {
    return timeline;
  }

}

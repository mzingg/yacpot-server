package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

public class Event extends AbstractGenericModel<Event> {

  private final EventTimeline timeline;

  public Event() {
    this.timeline = new EventTimeline();
  }

  // The timeline inherits the timestamp from the event (for default sorting)
  @Override
  public LocalDateTime timestamp() {
    return timeline().timestamp();
  }

  @Override
  public Event timestamp(@NotNull LocalDateTime timestamp) {
    timeline().timestamp(timestamp);
    return this;
  }

  public EventTimeline timeline() {
    return timeline;
  }

}

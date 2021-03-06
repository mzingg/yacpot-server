package com.yacpot.community.model;

import com.yacpot.core.model.AbstractModel;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

public class Event extends AbstractModel<Event> {

  private EventTimeline timeline;

  public Event() {
    this.timeline = new EventTimeline();
  }

  // The getTimeline inherits the setTimestamp from the addEvent (for default sorting)
  @Override
  public LocalDateTime getTimestamp() {
    return getTimeline().getTimestamp();
  }

  @Override
  public Event setTimestamp(@NotNull LocalDateTime timestamp) {
    getTimeline().setTimestamp(timestamp);
    return this;
  }

  public EventTimeline getTimeline() {
    return timeline;
  }

  public Event setTimeline(EventTimeline timeline) {
    this.timeline = timeline;
    return this;
  }

}

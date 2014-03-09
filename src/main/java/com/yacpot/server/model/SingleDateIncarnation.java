package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class SingleDateIncarnation extends AbstractGenericModel<SingleDateIncarnation> implements EventIncarnation {

  private LocalDateTime date;

  public SingleDateIncarnation(LocalDateTime date) {
    this.date = date;
  }

  public LocalDateTime getDate() {
    return date;
  }

  @Override
  public LocalDateTime getSortDate(LocalDateTime anchorDate) {
    // LocalDateTime is immutable - so we can just return it here.
    return date;
  }

  @Override
  public boolean takesPlaceDuring(@NotNull LocalDate startDay, @NotNull LocalDate endDay) {
    Interval testInterval = new Interval(startDay.toDateTimeAtStartOfDay(), endDay.toDateTimeAtStartOfDay());
    return testInterval.contains(date.toDateTime());
  }

}

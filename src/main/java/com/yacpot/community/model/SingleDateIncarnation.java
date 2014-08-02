package com.yacpot.community.model;

import com.yacpot.core.model.AbstractGenericModel;
import org.jetbrains.annotations.NotNull;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class SingleDateIncarnation extends AbstractGenericModel<SingleDateIncarnation> implements EventIncarnation {

  private LocalDateTime date;

  public SingleDateIncarnation() {
    super();
    this.date = LocalDateTime.now();
  }

  public LocalDateTime getDate() {
    return date;
  }

  public SingleDateIncarnation setDate(LocalDateTime date) {
    this.date = date;
    return this;
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

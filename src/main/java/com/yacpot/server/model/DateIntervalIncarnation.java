package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.*;

public class DateIntervalIncarnation extends AbstractGenericModel<DateIntervalIncarnation> implements EventIncarnation {

  private final LocalDateTime startDate;
  private final LocalDateTime endDate;

  public DateIntervalIncarnation(LocalDateTime startDate, LocalDateTime endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public LocalDateTime startDate() {
    return startDate;
  }

  public LocalDateTime endDate() {
    return endDate;
  }

  @Override
  public LocalDateTime sortDate(LocalDateTime anchorDate) {
    // LocalDateTime is immutable - so we can just return it here.
    return startDate;
  }

  @Override
  public boolean takesPlaceDuring(@NotNull LocalDate startDay, @NotNull LocalDate endDay) {
    Interval localInterval = new Interval(startDate.toLocalDate().toDateTimeAtStartOfDay(), endDate.toLocalDate().toDateTimeAtStartOfDay());
    Interval testInterval = new Interval(startDay.toDateTimeAtStartOfDay(), endDay.toDateTimeAtStartOfDay());

    return testInterval.overlaps(localInterval);
  }
}

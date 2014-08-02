package com.yacpot.community.model;

import com.yacpot.core.model.AbstractGenericModel;
import org.jetbrains.annotations.NotNull;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DateIntervalIncarnation extends AbstractGenericModel<DateIntervalIncarnation> implements EventIncarnation {

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  public DateIntervalIncarnation() {
    super();
    this.startDate = LocalDateTime.now();
    this.endDate = LocalDateTime.now();
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public DateIntervalIncarnation setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }


  public DateIntervalIncarnation setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  @Override
  public LocalDateTime getSortDate(LocalDateTime anchorDate) {
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

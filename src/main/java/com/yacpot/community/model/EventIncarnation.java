package com.yacpot.community.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface EventIncarnation {
  LocalDateTime getSortDate(LocalDateTime anchorDate);

  boolean takesPlaceDuring(LocalDate startDate, LocalDate endDate);
}

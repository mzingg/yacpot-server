package com.yacpot.server.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface EventIncarnation {
  LocalDateTime sortDate(LocalDateTime anchorDate);

  boolean takesPlaceDuring(LocalDate startDate, LocalDate endDate);
}
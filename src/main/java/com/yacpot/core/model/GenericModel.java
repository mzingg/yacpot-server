package com.yacpot.core.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

public interface GenericModel<T extends GenericModel> {
  ModelIdentifier getId();

  T setId(@NotNull ModelIdentifier newId);

  String getLabel();

  T setLabel(@NotNull String label);

  int getOrderWeight();

  T setOrderWeight(int orderWeight);

  LocalDateTime getTimestamp();

  T setTimestamp(@NotNull LocalDateTime timestamp);
}

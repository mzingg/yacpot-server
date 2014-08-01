package com.yacpot.core.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

public interface GenericModel<T extends GenericModel> {
  GenericModelIdentifier getId();

  @SuppressWarnings("unchecked")
  T setId(@NotNull GenericModelIdentifier newId);

  String getLabel();

  @SuppressWarnings("unchecked")
  T setLabel(@NotNull String label);

  int getOrderWeight();

  @SuppressWarnings("unchecked")
  T setOrderWeight(int orderWeight);

  LocalDateTime getTimestamp();

  @SuppressWarnings("unchecked")
  T setTimestamp(@NotNull LocalDateTime timestamp);
}

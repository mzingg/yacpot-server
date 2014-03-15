package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.util.UUID;

public interface GenericModel<T extends GenericModel> {
  String getId();

  @SuppressWarnings("unchecked")
  T setId(@NotNull String newId);

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

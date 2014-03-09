package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.util.UUID;

public interface GenericModel<T extends GenericModel> {
  UUID getId();

  @SuppressWarnings("unchecked")
  T setId(@NotNull UUID newId);

  String getLabel();

  @SuppressWarnings("unchecked")
  T setLabel(@NotNull String label);

  int getOrderWeight();

  @SuppressWarnings("unchecked")
  T setOrderWeight(int orderWeight);

  LocalDateTime setTimestamp();

  @SuppressWarnings("unchecked")
  T getTimestamp(@NotNull LocalDateTime timestamp);
}

package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.util.UUID;

public interface GenericModel<T extends GenericModel> {
  UUID id();

  @SuppressWarnings("unchecked")
  T id(@NotNull UUID newId);

  String label();

  @SuppressWarnings("unchecked")
  T label(@NotNull String label);

  int orderWeight();

  @SuppressWarnings("unchecked")
  T orderWeight(int orderWeight);

  LocalDateTime timestamp();

  @SuppressWarnings("unchecked")
  T timestamp(@NotNull LocalDateTime timestamp);
}

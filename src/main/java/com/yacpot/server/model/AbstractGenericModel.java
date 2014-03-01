package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractGenericModel<T extends GenericModel> implements Serializable, GenericModel<T> {

  private UUID id;

  private LocalDateTime timestamp;

  private transient String label;

  private transient int orderWeight;

  public AbstractGenericModel() {
    this.id = UUID.randomUUID();
    this.orderWeight = 0;
    this.label = id.toString();
    timestamp = LocalDateTime.now();
  }

  @Override
  public UUID id() {
    return id;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T id(@NotNull UUID newId) {
    this.id = newId;
    return (T) this;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T label(@NotNull String label) {
    this.label = label;
    return (T) this;
  }

  @Override
  public int orderWeight() {
    return orderWeight;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T orderWeight(int orderWeight) {
    this.orderWeight = orderWeight;
    return (T) this;
  }

  @Override
  public LocalDateTime timestamp() {
    return this.timestamp;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T timestamp(@NotNull LocalDateTime timestamp) {
    this.timestamp = timestamp;
    return (T) this;
  }

  @Override
  public boolean equals(@NotNull Object o) {
    if (this == o) return true;
    if (!(o instanceof GenericModel)) return false;

    GenericModel that = (GenericModel) o;

    return id().equals(that.id());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}

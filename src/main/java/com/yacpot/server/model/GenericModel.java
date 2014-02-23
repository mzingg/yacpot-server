package com.yacpot.server.model;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.UUID;

public abstract class GenericModel<T extends GenericModel> implements Serializable {

  private UUID id;

  private LocalDateTime timestamp;

  private transient String label;

  private transient int orderWeight;

  public GenericModel() {
    this.id = UUID.randomUUID();
    this.orderWeight = 0;
    this.label = id.toString();
    timestamp = LocalDateTime.now();
  }

  public UUID id() {
    return id;
  }

  @SuppressWarnings("unchecked")
  public T id(@NotNull UUID newId) {
    this.id = newId;
    return (T) this;
  }

  public String label() {
    return label;
  }

  @SuppressWarnings("unchecked")
  public T label(@NotNull String label) {
    this.label = label;
    return (T) this;
  }

  public int orderWeight() {
    return orderWeight;
  }

  @SuppressWarnings("unchecked")
  public T orderWeight(int orderWeight) {
    this.orderWeight = orderWeight;
    return (T) this;
  }

  public LocalDateTime timestamp() {
    return this.timestamp;
  }

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

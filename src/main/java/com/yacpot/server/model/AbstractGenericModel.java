package com.yacpot.server.model;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractGenericModel<T extends GenericModel> implements Serializable, GenericModel<T> {

  private String id;

  private LocalDateTime timestamp;

  private transient String label;

  private transient int orderWeight;

  public AbstractGenericModel() {
    this.id = ObjectId.get().toString();
    this.orderWeight = 0;
    this.label = id;
    timestamp = LocalDateTime.now();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T setId(@NotNull String newId) {
    this.id = newId;
    return (T) this;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T setLabel(@NotNull String label) {
    this.label = label;
    return (T) this;
  }

  @Override
  public int getOrderWeight() {
    return orderWeight;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T setOrderWeight(int orderWeight) {
    this.orderWeight = orderWeight;
    return (T) this;
  }

  @Override
  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T setTimestamp(@NotNull LocalDateTime timestamp) {
    this.timestamp = timestamp;
    return (T) this;
  }

  @Override
  public boolean equals(@NotNull Object o) {
    if (this == o) return true;
    if (!(o instanceof GenericModel)) return false;

    GenericModel that = (GenericModel) o;

    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}

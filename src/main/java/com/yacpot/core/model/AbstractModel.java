package com.yacpot.core.model;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public abstract class AbstractModel<T extends GenericModel> implements Serializable, GenericModel<T> {

  private ModelIdentifier id;

  private LocalDateTime timestamp;

  private transient String label;

  private transient int orderWeight;

  public AbstractModel() {
    this.id = new ModelIdentifier(ObjectId.get().toString());
    this.orderWeight = 0;
    this.label = this.id.toString();
    timestamp = LocalDateTime.now();
  }

  @Override
  public ModelIdentifier getId() {
    return id;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T setId(@NotNull ModelIdentifier newId) {
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

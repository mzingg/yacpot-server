package com.yacpot.server.model.sort;

import com.yacpot.server.model.GenericModel;

import java.util.Comparator;

public abstract class AbstractModelComparator<T extends GenericModel, U extends AbstractModelComparator> implements Comparator<T> {

  private OrderStrategy orderStrategy;
  private int sortDirectionMultiplier;

  public AbstractModelComparator() {
    this.orderStrategy = OrderStrategy.NO_ORDER;
    this.sortDirectionMultiplier = 1;
  }

  @SuppressWarnings("unchecked")
  public U ascending() {
    this.sortDirectionMultiplier = 1;
    return (U) this;
  }

  @SuppressWarnings("unchecked")
  public U descending() {
    this.sortDirectionMultiplier = -1;
    return (U) this;
  }

  @SuppressWarnings("unchecked")
  public U withoutOrder() {
    this.orderStrategy = OrderStrategy.NO_ORDER;
    return (U) this;
  }

  @SuppressWarnings("unchecked")
  public U byWeight() {
    this.orderStrategy = OrderStrategy.WEIGHT;
    return (U) this;
  }

  @SuppressWarnings("unchecked")
  public U byLabel() {
    this.orderStrategy = OrderStrategy.LABEL;
    return (U) this;
  }

  @SuppressWarnings("unchecked")
  public U byTimestamp() {
    this.orderStrategy = OrderStrategy.TIMESTAMP;
    return (U) this;
  }

  @Override
  public int compare(T o1, T o2) {
    if (orderStrategy.equals(OrderStrategy.LABEL)) {
      return sortDirectionMultiplier * o1.label().compareTo(o2.label());
    }
    if (orderStrategy.equals(OrderStrategy.WEIGHT)) {
      return sortDirectionMultiplier * Integer.valueOf(o1.orderWeight()).compareTo(o2.orderWeight());
    }
    if (orderStrategy.equals(OrderStrategy.TIMESTAMP)) {
      return sortDirectionMultiplier * o1.timestamp().compareTo(o2.timestamp());
    }
    return 0;
  }

  private enum OrderStrategy {
    NO_ORDER,
    WEIGHT,
    LABEL,
    TIMESTAMP
  }
}

package com.yacpot.server.model.sort;

import com.yacpot.server.model.GenericModel;

import java.util.Comparator;

public class GenericModelComparator<T extends GenericModel> implements Comparator<T> {

  private OrderStrategy orderStrategy;
  private int sortDirectionMultiplier;

  public GenericModelComparator() {
    this.orderStrategy = OrderStrategy.NO_ORDER;
    this.sortDirectionMultiplier = 1;
  }

  public GenericModelComparator<T> ascending() {
    this.sortDirectionMultiplier = 1;
    return this;
  }

  public GenericModelComparator<T> descending() {
    this.sortDirectionMultiplier = -1;
    return this;
  }

  public GenericModelComparator<T> withoutOrder() {
    this.orderStrategy = OrderStrategy.NO_ORDER;
    return this;
  }

  public GenericModelComparator<T> byWeight() {
    this.orderStrategy = OrderStrategy.WEIGHT;
    return this;
  }

  public GenericModelComparator<T> byLabel() {
    this.orderStrategy = OrderStrategy.LABEL;
    return this;
  }

  public GenericModelComparator<T> byTimestamp() {
    this.orderStrategy = OrderStrategy.TIMESTAMP;
    return this;
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

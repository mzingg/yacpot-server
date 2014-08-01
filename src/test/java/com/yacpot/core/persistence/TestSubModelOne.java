package com.yacpot.core.persistence;

import com.yacpot.core.model.AbstractGenericModel;

public class TestSubModelOne extends AbstractGenericModel<TestSubModelOne> {

  private String value;

  private TestSubModelOne selfReference;

  public String getValue() {
    return value;
  }

  public TestSubModelOne setValue(String value) {
    this.value = value;
    return this;
  }

  public TestSubModelOne getSelfReference() {
    return selfReference;
  }

  public TestSubModelOne setSelfReference(TestSubModelOne selfReference) {
    this.selfReference = selfReference;
    return this;
  }
}

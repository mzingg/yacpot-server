package com.yacpot.server.model;

public class SecurityRole extends GenericModel<SecurityRole> {
  private String description;

  public String description() {
    return this.description;
  }

  public SecurityRole description(String description) {
    this.description = description;
    return this;
  }
}

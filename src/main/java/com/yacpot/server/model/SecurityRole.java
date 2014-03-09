package com.yacpot.server.model;

public class SecurityRole extends AbstractGenericModel<SecurityRole> {
  private String description;

  public String getDescription() {
    return this.description;
  }

  public SecurityRole setDescription(String description) {
    this.description = description;
    return this;
  }
}

package com.yacpot.server.model;

import org.apache.commons.lang3.StringUtils;

public class User extends AbstractGenericModel<User> {

  public final static User ANONYMOUS = new User().setLabel("Anonymous User");

  private String email;

  public User() {
    this.email = StringUtils.EMPTY;
  }

  public String getEmail() {
    return this.email;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }
}

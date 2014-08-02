package com.yacpot.server.model;

import com.yacpot.core.model.AbstractModel;
import org.apache.commons.lang3.StringUtils;

public class SecurityRole extends AbstractModel<SecurityRole> {
  private String description;

  public SecurityRole() {
    this.description = StringUtils.EMPTY;
  }

  public String getDescription() {
    return this.description;
  }

  public SecurityRole setDescription(String description) {
    this.description = description;
    return this;
  }
}

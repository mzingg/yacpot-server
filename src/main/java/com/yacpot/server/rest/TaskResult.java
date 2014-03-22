package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TaskResult {

  public static TaskResult NotFoundResult = new TaskResult() {
  };

  private String json;

  private Map<String, String> userAttributes;

  public TaskResult() {
    this.json = StringUtils.EMPTY;
    this.userAttributes = new HashMap<>();
  }

  public TaskResult setJson(String json) {
    this.json = json;
    return this;
  }

  public String getJson() {
    return json;
  }

  public TaskResult addUserAttribute(String attributeKey, String attributeValue) {
    userAttributes.put(attributeKey, attributeValue);
    return this;
  }

  public Map<String, String> getUserAttributes() {
    return Collections.unmodifiableMap(userAttributes);
  }
}

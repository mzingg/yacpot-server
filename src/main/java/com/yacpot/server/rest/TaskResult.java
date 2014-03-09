package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

public class TaskResult {

  public static TaskResult OkResult = new TaskResult() {
  };
  public static TaskResult NotFoundResult = new TaskResult() {
  };

  private String json;

  public TaskResult() {
    this.json = StringUtils.EMPTY;
  }

  public TaskResult setJson(String json) {
    this.json = json;
    return this;
  }

  public String getJson() {
    return json;
  }
}

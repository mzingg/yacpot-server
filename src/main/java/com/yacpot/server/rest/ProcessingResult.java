package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

public class ProcessingResult {

  public static ProcessingResult OkResult = new ProcessingResult(){};

  private String json;

  public ProcessingResult() {
    this.json = StringUtils.EMPTY;
  }

  public ProcessingResult json(String json) {
    this.json = json;
    return this;
  }

  public String json() {
    return json;
  }
}

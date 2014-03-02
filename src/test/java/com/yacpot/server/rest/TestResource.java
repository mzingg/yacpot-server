package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

@Resource
public class TestResource {

  @ResourceMapping(pattern = "^/a/path$")
  public ProcessingResult test() {
    return new ProcessingResult() {
    }.json("aTestResult");
  }

  @ResourceMapping(pattern = "^/a/path/invalid$")
  public String testInvalidReturnType() {
    return StringUtils.EMPTY;
  }

  @ResourceMapping(pattern = "^/a/path/p1/(\\d+)$")
  public ProcessingResult testParameter(String id) {
    return new ProcessingResult() {
    }.json(id);
  }
}

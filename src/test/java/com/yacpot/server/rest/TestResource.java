package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

@Resource
public class TestResource {

  @ResourceMapping(pattern = "^/a/path$")
  public TaskResult test() {
    return new TaskResult() {
    }.json("aTestResult");
  }

  @ResourceMapping(pattern = "^/a/path/invalid$")
  public String testInvalidReturnType() {
    return StringUtils.EMPTY;
  }

  @ResourceMapping(pattern = "^/a/path/p1/(\\d+)$")
  public TaskResult testParameter(String id) {
    return new TaskResult() {
    }.json(id);
  }

  @ResourceMapping(pattern = "^/a/path/p2/(\\d+)$")
  public TaskResult testParameterWithTask(Task task, String id) {
    return new TaskResult() {
    }.json(id);
  }
}

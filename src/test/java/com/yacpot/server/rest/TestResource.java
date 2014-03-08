package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

@Resource
public class TestResource {

  @ResourceMapping(pattern = "^/a/path$", supportsOperations = Operation.READ)
  public TaskResult test() {
    return new TaskResult() {
    }.json("aTestResult");
  }

  @ResourceMapping(pattern = "^/a/path/invalid1$")
  public String testInvalidReturnType() {
    return StringUtils.EMPTY;
  }

  @ResourceMapping(pattern = "^/a/path/invalid2/(\\d+)$")
  public TaskResult testInvalidParameterType(int intParam) {
    return new TaskResult() {
    }.json(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/path/invalid3/(.+)$")
  public TaskResult testInvalidTooManyParameters(String p1, String p2) {
    return new TaskResult() {
    }.json(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/path/invalid4/(.+)/(.+)$")
  public TaskResult testInvalidNotEnoughParameters(String p1) {
    return new TaskResult() {
    }.json(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/path/invalid5$")
  public TaskResult testInvalidWithException() throws Exception {
    throw new Exception("Testexception");
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

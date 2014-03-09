package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;

@Resource
public class TestResource {

  @ResourceMapping(pattern = "^/a/getPath$", supportsOperations = Operation.READ)
  public TaskResult test() {
    return new TaskResult() {
    }.setJson("aTestResult");
  }

  @ResourceMapping(pattern = "^/a/getPath/invalid1$")
  public String testInvalidReturnType() {
    return StringUtils.EMPTY;
  }

  @ResourceMapping(pattern = "^/a/getPath/invalid2/(\\d+)$")
  public TaskResult testInvalidParameterType(int intParam) {
    return new TaskResult() {
    }.setJson(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/getPath/invalid3/(.+)$")
  public TaskResult testInvalidTooManyParameters(String p1, String p2) {
    return new TaskResult() {
    }.setJson(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/getPath/invalid4/(.+)/(.+)$")
  public TaskResult testInvalidNotEnoughParameters(String p1) {
    return new TaskResult() {
    }.setJson(StringUtils.EMPTY);
  }

  @ResourceMapping(pattern = "^/a/getPath/invalid5$")
  public TaskResult testInvalidWithException() throws Exception {
    throw new Exception("Testexception");
  }

  @ResourceMapping(pattern = "^/a/getPath/p1/(\\d+)$")
  public TaskResult testParameter(String id) {
    return new TaskResult() {
    }.setJson(id);
  }

  @ResourceMapping(pattern = "^/a/getPath/p2/(\\d+)$")
  public TaskResult testParameterWithTask(Task task, String id) {
    return new TaskResult() {
    }.setJson(id);
  }
}

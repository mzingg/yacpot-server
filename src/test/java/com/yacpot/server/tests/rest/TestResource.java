package com.yacpot.server.tests.rest;

import com.yacpot.server.rest.ProcessingResult;
import com.yacpot.server.rest.Resource;
import com.yacpot.server.rest.ResourceMapping;
import org.apache.commons.lang3.StringUtils;

@Resource
public class TestResource {
  public final static ProcessingResult testProcessingResult = new ProcessingResult() {
  };

  @ResourceMapping(pattern = "^/a/path$")
  public ProcessingResult test() {
    return testProcessingResult;
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

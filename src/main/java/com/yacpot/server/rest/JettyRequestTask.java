package com.yacpot.server.rest;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JettyRequestTask extends Task<JettyRequestTask> {

  private Map<String, String[]> parameterMap;

  public JettyRequestTask(String path, Map<String, String[]> parameterMap) {
    super(path, mapJettyParameterToAttributes(parameterMap));
  }

  private static Map<String, Object> mapJettyParameterToAttributes(Map<String, String[]> parameterMap) {
    Map<String, Object> result = new HashMap<>();
    if (parameterMap == null) {
      return result;
    }
    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
      result.put(entry.getKey(), Arrays.asList(entry.getValue()));
    }
    return result;
  }

  @Override
  public Object getAttribute(@NotNull String attributeName) {
    Object attribute = super.getAttribute(attributeName);
    if (attribute instanceof List && ((List) attribute).size() == 1) {
      attribute = ((List) attribute).get(0);
    }

    return attribute;
  }
}

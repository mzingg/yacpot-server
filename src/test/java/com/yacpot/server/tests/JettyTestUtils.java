package com.yacpot.server.tests;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JettyTestUtils {
  public static Request aRequest(String method, String path) {
    Request request = mock(Request.class);
    when(request.getUri()).thenReturn(new HttpURI("http://test:8080" + path));
    when(request.getMethod()).thenReturn(method);
    return request;
  }

  public static Request aRequestWithContextPath(String method, String path) {
    Request request = aRequest(method, path);
    when(request.getContextPath()).thenReturn("/context");
    return request;
  }

  public static Request aRequestWithParameters(String method, String path, String... parameters) {
    Request request = aRequest(method, path);

    Map<String, String[]> theMap = new HashMap<>();
    for (int idx = 0; idx < parameters.length - 1; idx += 2) {
      String key = parameters[idx];
      String[] value = new String[]{parameters[idx + 1]};
      theMap.put(key, value);
    }
    when(request.getParameterMap()).thenReturn(theMap);
    return request;
  }
}

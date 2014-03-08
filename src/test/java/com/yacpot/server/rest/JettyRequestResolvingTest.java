package com.yacpot.server.rest;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JettyRequestResolvingTest {

  @Test
  public void testResolve() throws Exception {
    JettyRequestTaskFactory factory = new JettyRequestTaskFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve(factory.getTask(aRequest("GET", "/a/path"))).json());
  }

  @Test
  public void testResolveWithContextPath() throws Exception {
    JettyRequestTaskFactory factory = new JettyRequestTaskFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve(factory.getTask(aRequestWithContextPath("GET", "/context/a/path"))).json());
  }

  @Test(expected = TaskException.class)
  public void testResolveDoesNotStartWithContextThrowsException() throws Exception {
    JettyRequestTaskFactory factory = new JettyRequestTaskFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(factory.getTask(aRequestWithContextPath("GET", "/somewhereelse/a/path")));
  }

  @Test
  public void testMethodMapping() throws Exception {
    JettyRequestTaskFactory factory = new JettyRequestTaskFactory();
    assertEquals(Operation.CREATE, factory.getTask(aRequest("PUT", "/a/path")).operation());
    assertEquals(Operation.READ, factory.getTask(aRequest("GET", "/a/path")).operation());
    assertEquals(Operation.UPDATE, factory.getTask(aRequest("POST", "/a/path")).operation());
    assertEquals(Operation.DELETE, factory.getTask(aRequest("DELETE", "/a/path")).operation());
    assertEquals(Operation.READ, factory.getTask(aRequest("SOMEOTHER", "/a/path")).operation());
  }

  @Test
  public void testParameterPassing() throws Exception {
    JettyRequestTaskFactory factory = new JettyRequestTaskFactory();
    JettyRequestTask testObj = factory.getTask(aRequestWithParameters("PUT", "/a/path", "param1", "value"));

    assertEquals("value", testObj.getAttribute("param1"));
  }

  private Request aRequest(String method, String path) {
    Request request = mock(Request.class);
    when(request.getUri()).thenReturn(new HttpURI("http://test:8080" + path));
    when(request.getMethod()).thenReturn(method);
    return request;
  }

  private Request aRequestWithContextPath(String method, String path) {
    Request request = aRequest(method, path);
    when(request.getContextPath()).thenReturn("/context");
    return request;
  }

  private Request aRequestWithParameters(String method, String path, String... parameters) {
    Request request = aRequest(method, path);

    Map<String, String[]> theMap = new HashMap<>();
    for (int idx = 0; idx < parameters.length - 1; idx += 2) {
      String key = (String) parameters[idx];
      String[] value = new String[]{parameters[idx + 1]};
      theMap.put(key, value);
    }
    when(request.getParameterMap()).thenReturn(theMap);
    return request;
  }
}

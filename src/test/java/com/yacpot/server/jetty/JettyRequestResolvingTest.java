package com.yacpot.server.jetty;

import com.yacpot.server.rest.*;
import org.junit.Test;

import static com.yacpot.server.tests.jetty.JettyTestUtils.*;
import static org.junit.Assert.assertEquals;


public class JettyRequestResolvingTest {

  @Test
  public void testResolve() throws Exception {
    JettyRequestFactory factory = new JettyRequestFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve(factory.getTask(aRequest("GET", "/a/getPath"))).getJson());
  }

  @Test
  public void testResolveWithContextPath() throws Exception {
    JettyRequestFactory factory = new JettyRequestFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve(factory.getTask(aRequestWithContextPath("GET", "/context/a/getPath"))).getJson());
  }

  @Test(expected = TaskException.class)
  public void testResolveDoesNotStartWithContextThrowsException() throws Exception {
    JettyRequestFactory factory = new JettyRequestFactory();
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(factory.getTask(aRequestWithContextPath("GET", "/somewhereelse/a/getPath")));
  }

  @Test
  public void testMethodMapping() throws Exception {
    JettyRequestFactory factory = new JettyRequestFactory();
    assertEquals(Operation.CREATE, factory.getTask(aRequest("PUT", "/a/getPath")).getOperation());
    assertEquals(Operation.READ, factory.getTask(aRequest("GET", "/a/getPath")).getOperation());
    assertEquals(Operation.UPDATE, factory.getTask(aRequest("POST", "/a/getPath")).getOperation());
    assertEquals(Operation.DELETE, factory.getTask(aRequest("DELETE", "/a/getPath")).getOperation());
    assertEquals(Operation.READ, factory.getTask(aRequest("SOMEOTHER", "/a/getPath")).getOperation());
  }

  @Test
  public void testParameterPassing() throws Exception {
    JettyRequestFactory factory = new JettyRequestFactory();
    JettyRequestTask testObj = factory.getTask(aRequestWithParameters("PUT", "/a/getPath", "param1", "value"));

    assertEquals("value", testObj.getAttribute("param1"));
  }

}

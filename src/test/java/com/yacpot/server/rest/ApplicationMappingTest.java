package com.yacpot.server.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationMappingTest {

  @Test(expected = MappingException.class)
  public void testInvalidReturnTypeThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path/invalid");
  }

  @Test
  public void testMappingResolves() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve("/a/path").json());
  }

  @Test
  public void testMappingResolvesWithApplicationContext() throws Exception {

    ApplicationMapping mapping = new ApplicationMapping().contextPath("/contextPath");
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve("/contextPath/a/path").json());
  }

  @Test(expected = MappingException.class)
  public void testResolveDoesNotStartWithContaxtPathThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping().contextPath("/contextPath");
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path").json();
  }

  @Test
  public void testParameterPassing() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping().contextPath("/contextPath");
    mapping.registerResource(new TestResource());

    assertEquals("23", mapping.resolve("/a/path/p1/23").json());

  }
}

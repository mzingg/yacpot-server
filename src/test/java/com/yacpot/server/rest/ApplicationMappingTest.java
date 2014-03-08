package com.yacpot.server.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationMappingTest {

  @Test(expected = MappingException.class)
  public void testInvalidReturnTypeThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path/invalid1");
  }

  @Test(expected = MappingException.class)
  public void testInvalidParameterTypeThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path/invalid2/15");
  }

  @Test(expected = MappingException.class)
  public void testTooManyParametersThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path/invalid3/aParam");
  }

  @Test(expected = MappingException.class)
  public void testNotEnoughParametersThrowsException() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    mapping.resolve("/a/path/invalid4/aParam/andAnother");
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
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    assertEquals("77", mapping.resolve("/a/path/p1/77").json());
  }

  @Test
  public void testParameterPassingWithTaskParameter() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping();
    mapping.registerResource(new TestResource());

    assertEquals("23", mapping.resolve("/a/path/p2/23").json());
  }
}

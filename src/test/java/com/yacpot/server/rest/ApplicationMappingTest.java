package com.yacpot.server.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationMappingTest {

  @SuppressWarnings("ConstantConditions")
  @Test(expected = IllegalArgumentException.class)
  public void testNullResourceregisteringNotAllowed() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(null);
  }

  @Test(expected = MappingException.class)
  public void testInvalidReturnTypeThrowsException() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(new Task("/a/path/invalid1"));
  }

  @Test(expected = MappingException.class)
  public void testInvalidParameterTypeThrowsException() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(new Task("/a/path/invalid2/15"));
  }

  @Test(expected = MappingException.class)
  public void testTooManyParametersThrowsException() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(new Task("/a/path/invalid3/aParam"));
  }

  @Test(expected = MappingException.class)
  public void testNotEnoughParametersThrowsException() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(new Task("/a/path/invalid4/aParam/andAnother"));
  }

  @Test(expected = MappingException.class)
  public void testExceptionInResourceMethodThrowsMappingException() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    mapping.resolve(new Task("/a/path/invalid5"));
  }

  @Test
  public void testNotSupportedOperationIsNotResolved() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals(TaskResult.NotFoundResult, mapping.resolve(new Task("/a/path").operation(Operation.CREATE)));
  }

  @Test
  public void testMappingResolves() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("aTestResult", mapping.resolve(new Task("/a/path")).json());
  }

  @Test
  public void testParameterPassing() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("77", mapping.resolve(new Task("/a/path/p1/77")).json());
  }

  @Test
  public void testParameterPassingWithTaskParameter() throws Exception {
    ApplicationMapping<Task> mapping = new ApplicationMapping<>();
    mapping.registerResource(new TestResource());

    assertEquals("23", mapping.resolve(new Task("/a/path/p2/23")).json());
  }
}

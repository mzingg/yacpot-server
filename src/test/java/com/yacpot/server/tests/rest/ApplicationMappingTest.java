package com.yacpot.server.tests.rest;

import com.yacpot.server.rest.ApplicationMapping;
import com.yacpot.server.rest.MappingException;
import org.junit.Assert;
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

    assertEquals(TestResource.testProcessingResult, mapping.resolve("/a/path"));
  }

  @Test
  public void testMappingResolvesWithApplicationContext() throws Exception {

    ApplicationMapping mapping = new ApplicationMapping().contextPath("/contextPath");
    mapping.registerResource(new TestResource());

    assertEquals(TestResource.testProcessingResult, mapping.resolve("/contextPath/a/path"));
  }

  @Test
  public void testParameterPassing() throws Exception {
    ApplicationMapping mapping = new ApplicationMapping().contextPath("/contextPath");
    mapping.registerResource(new TestResource());

    assertEquals("23", mapping.resolve("/a/path/p1/23").json());

  }
}

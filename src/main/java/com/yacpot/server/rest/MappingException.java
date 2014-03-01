package com.yacpot.server.rest;

public class MappingException extends Exception {
  public MappingException(String message) {
    super(message);
  }

  public MappingException(String message, Throwable exception) {
    super(message, exception);
  }
}

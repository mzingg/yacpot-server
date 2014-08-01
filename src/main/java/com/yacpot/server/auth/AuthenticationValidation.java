package com.yacpot.server.auth;

public class AuthenticationValidation {

  private final long timestamp;

  private final String validationCode;

  public AuthenticationValidation(long timestamp, String validationCode) {
    this.timestamp = timestamp;
    this.validationCode = validationCode;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getValidationCode() {
    return validationCode;
  }
}

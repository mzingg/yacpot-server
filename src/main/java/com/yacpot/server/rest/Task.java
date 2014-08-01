package com.yacpot.server.rest;

import com.yacpot.server.auth.AuthenticationSession;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Task<T extends Task> {

  private final String path;

  private Operation operation;

  private Map<String, Object> attributes;

  private final List<String> parameters;

  private AuthenticationSession authenticationSession;

  public Task(String path) {
    this.path = path;
    this.operation = Operation.READ;
    this.parameters = new ArrayList<>();
    this.attributes = new HashMap<>();
    this.authenticationSession = null;
  }

  protected Task(String path, Map<String, Object> attributes) {
    this(path);
    this.attributes = attributes;
  }

  public String getPath() {
    return this.path;
  }

  @SuppressWarnings("unchecked")
  public T setOperation(Operation operation) {
    this.operation = operation;
    return (T) this;
  }

  public Operation getOperation() {
    return this.operation;
  }

  public void addParameter(String... parameters) {
    this.parameters.addAll(Arrays.asList(parameters));
  }

  public int getPathParameterCount() {
    return this.parameters.size();
  }

  public String getPathParameter(int idx) {
    return this.parameters.get(idx);
  }

  public boolean hasAttribute(@NotNull String attributeName) {
    return attributes.containsKey(attributeName);
  }

  public Object getAttribute(@NotNull String attributeName) {
    return attributes.get(attributeName);
  }

  @SuppressWarnings("unchecked")
  public <E> E getAttribute(@NotNull String attributeName, @NotNull Class<E> desiredClass) {
    Object result = getAttribute(attributeName);
    if (result == null || !desiredClass.isAssignableFrom(result.getClass())) {
      return null;
    }

    return (E) result;
  }

  @SuppressWarnings("unchecked")
  public <E> E getAttribute(@NotNull String attributeName, E defaultValue) {
    E result = (E) getAttribute(attributeName, defaultValue.getClass());
    if (result == null) {
      return defaultValue;
    }

    return result;
  }

  public boolean isAuthenticated() {
    return authenticationSession != null && authenticationSession.isValid();
  }

  public AuthenticationSession getAuthenticationSession() {
    return authenticationSession;
  }

  public Task<T> setAuthenticationSession(AuthenticationSession authenticationSession) {
    this.authenticationSession = authenticationSession;
    return this;
  }
}

package com.yacpot.server.rest;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Task<T extends Task> {

  private String path;

  private Operation operation;

  private Map<String, Object> attributes;

  private List<String> parameters;

  public Task(String path) {
    this.path = path;
    this.operation = Operation.READ;
    this.parameters = new ArrayList<>();
    this.attributes = new HashMap<>();
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
  public <T> T getAttribute(@NotNull String attributeName, @NotNull Class<T> desiredClass) {
    Object result = getAttribute(attributeName);
    if (!desiredClass.isAssignableFrom(result.getClass())) {
      return null;
    }

    return (T) result;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(@NotNull String attributeName, T defaultValue) {
    T result = (T) getAttribute(attributeName, defaultValue.getClass());
    if (result == null) {
      return defaultValue;
    }

    return result;
  }
}

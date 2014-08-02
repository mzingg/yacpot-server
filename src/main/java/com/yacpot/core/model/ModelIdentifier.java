package com.yacpot.core.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class ModelIdentifier implements Serializable {

  private final String id;

  public ModelIdentifier(@NotNull String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ModelIdentifier)) return false;

    ModelIdentifier that = (ModelIdentifier) o;

    return id.equals(that.id);

  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id;
  }
}

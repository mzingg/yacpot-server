package com.yacpot.core.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class GenericModelIdentifier implements Serializable {

  private final String id;

  public GenericModelIdentifier(@NotNull String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GenericModelIdentifier)) return false;

    GenericModelIdentifier that = (GenericModelIdentifier) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return id;
  }
}

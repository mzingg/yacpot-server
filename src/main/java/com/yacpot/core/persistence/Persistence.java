package com.yacpot.core.persistence;

import com.yacpot.core.model.GenericModel;
import com.yacpot.core.model.ModelIdentifier;

import java.io.Closeable;


public interface Persistence extends Closeable {
  GenericModel<?> resolveById(ModelIdentifier id) throws PersistenceException;

  <T> T resolveById(ModelIdentifier id, Class<T> desiredObjectType) throws PersistenceException;

  void save(Object model) throws PersistenceException;
}

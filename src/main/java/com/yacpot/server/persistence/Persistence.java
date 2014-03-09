package com.yacpot.server.persistence;

import com.mongodb.*;
import com.yacpot.server.model.GenericModel;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Persistence<T extends GenericModel> implements Closeable {

  private MongoClient mongo;
  private DB database;

  public Persistence(MongoClient mongo, String databaseName) {
    this.mongo = mongo;
    this.database = mongo.getDB(databaseName);
  }

  protected DB getDatabase() {
    return this.database;
  }

  public T findById(String id, Class<T> desiredObjectType) throws PersistenceException {
    try {
      T result = desiredObjectType.newInstance();

      DBCollection collection = getCollection(desiredObjectType);
      DBObject mongoObj = collection.findOne(new BasicDBObject("id", id));
      if (mongoObj == null) {
        return null;
      }

      fillModelWith(result, mongoObj);

      return result;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new PersistenceException(e.getLocalizedMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public void save(T model) throws PersistenceException {
    DBCollection collection = getCollection((Class<T>) model.getClass());
    BasicDBObject mongoObj = new BasicDBObject();
    mapModelTo(model, mongoObj);

    collection.save(mongoObj);
    ensureIndex(collection);
  }

  protected void ensureIndex(DBCollection collection) {
    collection.ensureIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
  }

  protected void fillModelWith(T model, DBObject mongoObj) throws PersistenceException {
    for (Method setter : getMethodsStartingWith(model.getClass(), "set")) {
      String propertyName = getPropertyName(setter);
      try {
        Object mongoValue = mongoObj.get(propertyName);
        Class<?>[] signature = setter.getParameterTypes();
        if (mongoValue != null && signature.length == 1 && signature[0].isAssignableFrom(mongoValue.getClass())) {
          setter.invoke(model, mongoValue);
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  protected void mapModelTo(T model, BasicDBObject mongoObj) throws PersistenceException {
    for (Method getter : getMethodsStartingWith(model.getClass(), "get", "is")) {
      String propertyName = getPropertyName(getter);
      if (getter.getParameterTypes().length > 0 || "class".equals(propertyName)) {
        continue;
      }
      try {
        mongoObj.append(propertyName, getter.invoke(model));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  private List<Method> getMethodsStartingWith(Class<?> modelClass, String... prefixes) {
    List<Method> result = new ArrayList<>();
    for (Method candidate : modelClass.getMethods()) {
      for (String prefix : prefixes) {
        if (candidate.getName().startsWith(prefix)) {
          result.add(candidate);
        }
      }
    }
    return result;
  }

  private String getPropertyName(Method method) {
    int prefixLength = 3;
    if (method.getName().startsWith("is")) {
      prefixLength = 2;
    }
    String propertyName = method.getName().substring(prefixLength);
    return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
  }

  protected DBCollection getCollection(Class<T> desiredObjectType) {
    return getDatabase().getCollection(desiredObjectType.getSimpleName());
  }

  @Override
  public void close() throws IOException {
  }

}

package com.yacpot.server.persistence;

import com.mongodb.*;
import com.yacpot.server.model.GenericModel;
import org.joda.time.LocalDateTime;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persistence<T extends GenericModel> implements Closeable {

  private final static Map<Class<?>, Class<?>> boxedTypes = new HashMap<>();
  static {
    boxedTypes.put(int.class, Integer.class);
    boxedTypes.put(long.class, Long.class);
    boxedTypes.put(double.class, Double.class);
    boxedTypes.put(float.class, Float.class);
    boxedTypes.put(boolean.class, Boolean.class);
    boxedTypes.put(char.class, Character.class);
    boxedTypes.put(byte.class, Byte.class);
    boxedTypes.put(short.class, Short.class);
  }

  private DB database;

  public Persistence(MongoClient mongo, String databaseName) {
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
        Class<?>[] signature = setter.getParameterTypes();
        Class<?> targetType = getBoxedClass(signature[0]);
        Object mongoValue = reverseMapSpecialValues(mongoObj.get(propertyName), targetType);
        if (mongoValue != null && signature.length == 1 && targetType.isAssignableFrom(mongoValue.getClass())) {
          setter.invoke(model, mongoValue);
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  private Class<?> getBoxedClass(Class<?> primitiveOrConcreteClass) {
    if (!boxedTypes.containsKey(primitiveOrConcreteClass))
      return primitiveOrConcreteClass;

    return boxedTypes.get(primitiveOrConcreteClass);
  }

  protected void mapModelTo(T model, BasicDBObject mongoObj) throws PersistenceException {
    for (Method getter : getMethodsStartingWith(model.getClass(), "get", "is")) {
      String propertyName = getPropertyName(getter);
      if (getter.getParameterTypes().length > 0 || "class".equals(propertyName)) {
        continue;
      }
      try {
        mongoObj.append(propertyName, mapSpecialValues(getter.invoke(model)));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  private Object mapSpecialValues(Object o) {
    if (o instanceof LocalDateTime) {
      return ((LocalDateTime)o).toDateTime().getMillis();
    }

    return o;
  }

  private Object reverseMapSpecialValues(Object o, Class<?> targetType) {
    if (LocalDateTime.class == targetType && o instanceof Long) {
      return new LocalDateTime(((Long)o).longValue());
    }

    return o;
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

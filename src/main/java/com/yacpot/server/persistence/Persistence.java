package com.yacpot.server.persistence;

import com.mongodb.*;
import com.yacpot.server.model.GenericModel;
import org.joda.time.LocalDateTime;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Persistence implements Closeable {

  private final static String ID_FIELD_NAME = "id";
  private final static String CLASS_FIELD_NAME = "class";

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

  public GenericModel<?> resolveById(String id) throws PersistenceException {
    try {
      for (String collectionName : database.getCollectionNames()) {
        DBCollection collection = database.getCollection(collectionName);
        DBObject mongoObj = collection.findOne(new BasicDBObject(ID_FIELD_NAME, id));
        if (mongoObj == null || !mongoObj.containsField(CLASS_FIELD_NAME) || !(mongoObj.get(CLASS_FIELD_NAME) instanceof String)) {
          continue;
        }

        String className = (String) mongoObj.get(CLASS_FIELD_NAME);
        Class<?> modelClass = Class.forName(className);
        Object model = modelClass.newInstance();

        fillModelWith(model, mongoObj);

        return (GenericModel<?>) model;
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new PersistenceException(e.getLocalizedMessage(), e);
    }

    return null;
  }

  public <T> T findById(String id, Class<T> desiredObjectType) throws PersistenceException {
    try {
      T result = desiredObjectType.newInstance();

      DBCollection collection = getCollection(desiredObjectType);
      DBObject mongoObj = collection.findOne(new BasicDBObject(ID_FIELD_NAME, id));
      if (mongoObj == null) {
        return null;
      }

      fillModelWith(result, mongoObj);

      return result;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new PersistenceException(e.getLocalizedMessage(), e);
    }
  }

  public void save(Object model) throws PersistenceException {
    DBCollection collection = getCollection(model.getClass());
    BasicDBObject mongoObj = new BasicDBObject();
    mapModelTo(model, mongoObj);

    collection.save(mongoObj);
    ensureIndex(collection);
  }

  protected void ensureIndex(DBCollection collection) {
    collection.ensureIndex(new BasicDBObject(ID_FIELD_NAME, 1), new BasicDBObject("unique", true));
  }

  protected void fillModelWith(Object model, DBObject mongoObj) throws PersistenceException {
    for (Method setter : getMethodsStartingWith(model.getClass(), "set", "add")) {
      String propertyName = getPropertyName(setter);
      if (setter.getName().startsWith("add")) {
        propertyName += "s";
      }
      try {
        Class<?>[] signature = setter.getParameterTypes();
        Class<?> targetType = getBoxedClass(signature[0]);
        Object mongoValue = reverseMapSpecialValues(mongoObj.get(propertyName), targetType);
        if (mongoValue != null && signature.length == 1) {
          if (GenericModel.class.isAssignableFrom(targetType) && mongoValue instanceof String) {
            GenericModel<?> referencedValue = resolveById((String)mongoValue);
            setter.invoke(model, referencedValue);
          } else if (GenericModel.class.isAssignableFrom(targetType) && mongoValue instanceof Collection<?>) {
            for (Object entry : (Collection<?>)mongoValue) {
              GenericModel<?> referencedValue = resolveById((String)entry);
              setter.invoke(model, referencedValue);
            }
          } else if (targetType.isAssignableFrom(mongoValue.getClass())) {
            setter.invoke(model, mongoValue);
          }
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

  protected void mapModelTo(Object model, BasicDBObject mongoObj) throws PersistenceException {
    for (Method getter : getMethodsStartingWith(model.getClass(), "get", "is")) {
      Class<?>[] signature = getter.getParameterTypes();
      String propertyName = getPropertyName(getter);
      if (signature.length > 0) {
        continue;
      }
      try {
        Class<?> sourceType = getter.getReturnType();
        if (GenericModel.class.isAssignableFrom(sourceType)) {
          GenericModel<?> subModel = (GenericModel<?>)getter.invoke(model);
          save(subModel);
          mongoObj.append(propertyName, subModel.getId());
        } else if (Collection.class.isAssignableFrom(sourceType)) {
          List<String> idList = new ArrayList<>();
          for (Object listEntry : (Collection<?>)getter.invoke(model)) {
            GenericModel<?> subModel = (GenericModel<?>)listEntry;
            save(subModel);
            idList.add(subModel.getId());
          }
          mongoObj.append(propertyName, idList);
        } else {
          mongoObj.append(propertyName, mapSpecialValues(getter.invoke(model)));
        }
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

  protected DBCollection getCollection(Class<?> desiredObjectType) {
    return getDatabase().getCollection(desiredObjectType.getSimpleName());
  }

  protected Object mapSpecialValues(Object o) {
    if (o instanceof LocalDateTime) {
      return ((LocalDateTime) o).toDateTime().getMillis();
    }

    if (o instanceof Class<?>) {
      return ((Class<?>) o).getName();
    }

    return o;
  }

  protected Object reverseMapSpecialValues(Object o, Class<?> targetType) {
    if (LocalDateTime.class == targetType && o instanceof Long) {
      return new LocalDateTime(((Long) o).longValue());
    }

    return o;
  }

  @Override
  public void close() throws IOException {
  }

}

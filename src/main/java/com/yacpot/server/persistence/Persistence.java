package com.yacpot.server.persistence;

import com.mongodb.*;
import com.yacpot.server.model.GenericModel;
import org.bson.types.ObjectId;
import org.joda.time.LocalDateTime;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Persistence implements Closeable {

  private final static String ID_FIELD_NAME = "id";
  private final static String ID_PROPERTY_NAME = "_id";
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

  public GenericModel<?> resolveByReference(DBRef ref) throws PersistenceException {
    return findById((ObjectId) ref.getId(), ref.getRef(), null);
  }

  public GenericModel<?> resolveById(String id) throws PersistenceException {
    for (String collectionName : database.getCollectionNames()) {
      GenericModel<?> result = findById(new ObjectId(id), collectionName, null);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T resolveById(String id, Class<T> desiredObjectType) throws PersistenceException {
    return (T) findById(new ObjectId(id), null, desiredObjectType);
  }

  protected GenericModel<?> findById(ObjectId id, String collectionName, Class<?> desiredObjectType) throws PersistenceException {
    if (desiredObjectType != null && !GenericModel.class.isAssignableFrom(desiredObjectType)) {
      throw new IllegalArgumentException("Class must implement from GenricModel");
    }

    String effectiveCollectionName = desiredObjectType != null ? getCollectionName(desiredObjectType) : collectionName;

    try {
      DBCollection collection = database.getCollection(effectiveCollectionName);
      DBObject mongoObj = collection.findOne(new BasicDBObject(ID_PROPERTY_NAME, id));
      if (mongoObj == null) {
        return null;
      }

      Object model;
      if (desiredObjectType != null) {
        model = desiredObjectType.newInstance();
      } else {
        if (!mongoObj.containsField(CLASS_FIELD_NAME) || !(mongoObj.get(CLASS_FIELD_NAME) instanceof String)) {
          throw new PersistenceException("Could not resolve class value from document");
        }

        String className = (String) mongoObj.get(CLASS_FIELD_NAME);
        Class<?> modelClass = Class.forName(className);
        model = modelClass.newInstance();
      }

      fillModelWith(model, mongoObj);

      return (GenericModel<?>) model;
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new PersistenceException(e.getLocalizedMessage(), e);
    }
  }

  public void save(Object model) throws PersistenceException {
    if (!(model instanceof GenericModel)) {
      throw new IllegalArgumentException("Can only store objects which implement GenericModel.");
    }
    DBCollection collection = database.getCollection(getCollectionName(model.getClass()));
    BasicDBObject mongoObj = new BasicDBObject();
    mapModelTo(model, mongoObj);

    collection.save(mongoObj);
    ensureIndex(collection);
  }

  protected void ensureIndex(DBCollection collection) {
    //collection.ensureIndex(new BasicDBObject(ID_FIELD_NAME, 1), new BasicDBObject("unique", true));
  }

  protected void fillModelWith(Object model, DBObject mongoObj) throws PersistenceException {
    for (Method setter : getMethodsStartingWith(model.getClass(), "set", "add")) {
      String propertyName = getPropertyName(setter);
      try {
        Class<?>[] signature = setter.getParameterTypes();
        Class<?> targetType = getBoxedClass(signature[0]);
        Object mongoValue = reverseMapSpecialValues(propertyName, mongoObj.get(propertyName), targetType);
        if (mongoValue != null && signature.length == 1) {
          if (GenericModel.class.isAssignableFrom(targetType) && mongoValue instanceof DBRef) {
            GenericModel<?> referencedValue = resolveByReference((DBRef) mongoValue);
            setter.invoke(model, referencedValue);
          } else if (GenericModel.class.isAssignableFrom(targetType) && mongoValue instanceof Collection<?>) {
            for (Object entry : (Collection<?>) mongoValue) {
              GenericModel<?> referencedValue;
              if (entry instanceof DBRef) {
                referencedValue = resolveByReference((DBRef) entry);
              } else {
                referencedValue = resolveById(entry.toString());
              }

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
      if (signature.length > 0) {
        continue;
      }
      String propertyName = getPropertyName(getter);
      try {
        Class<?> sourceType = getter.getReturnType();
        if (GenericModel.class.isAssignableFrom(sourceType)) {
          GenericModel<?> subModel = (GenericModel<?>) getter.invoke(model);
          save(subModel);
          mongoObj.append(propertyName, new DBRef(database, getCollectionName(subModel.getClass()), new ObjectId(subModel.getId())));
        } else if (Collection.class.isAssignableFrom(sourceType)) {
          List<DBRef> idList = new ArrayList<>();
          for (Object listEntry : (Collection<?>) getter.invoke(model)) {
            GenericModel<?> subModel = (GenericModel<?>) listEntry;
            save(subModel);
            idList.add(new DBRef(database, getCollectionName(subModel.getClass()), new ObjectId(subModel.getId())));
          }
          mongoObj.append(propertyName, idList);
        } else {
          mongoObj.append(propertyName, mapSpecialValues(propertyName, getter.invoke(model)));
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

  protected String getPropertyName(Method method) {
    int prefixLength = 3;
    if (method.getName().startsWith("is")) {
      prefixLength = 2;
    }
    String propertyName = method.getName().substring(prefixLength);
    propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
    if (method.getName().startsWith("add")) {
      propertyName += "s";
    }
    if (ID_FIELD_NAME.equals(propertyName)) {
      propertyName = ID_PROPERTY_NAME;
    }
    return propertyName;
  }

  protected String getCollectionName(Class<?> desiredObjectType) {
    return desiredObjectType.getSimpleName();
  }

  protected Object mapSpecialValues(String propertyName, Object o) {
    if (o instanceof LocalDateTime) {
      return ((LocalDateTime) o).toDateTime().getMillis();
    }

    if (o instanceof Class<?>) {
      return ((Class<?>) o).getName();
    }

    if (ID_PROPERTY_NAME.equals(propertyName) && o instanceof String) {
      return new ObjectId((String) o);
    }

    return o;
  }

  protected Object reverseMapSpecialValues(String propertyName, Object o, Class<?> targetType) {
    if (LocalDateTime.class == targetType && o instanceof Long) {
      return new LocalDateTime(((Long) o).longValue());
    }

    if (ID_PROPERTY_NAME.equals(propertyName) && o instanceof ObjectId) {
      return o.toString();
    }

    return o;
  }

  @Override
  public void close() throws IOException {
  }

}

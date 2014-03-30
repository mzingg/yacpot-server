package com.yacpot.server.persistence;

import com.mongodb.*;
import com.yacpot.server.model.GenericModel;
import com.yacpot.server.model.GenericModelIdentifier;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
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

  private static final String MAP_ENTRY_INDICATOR = "yacpot_map";
  private static final String MAP_KEY_FIELD = "key";
  private static final String MAP_VALUE_FIELD = "value";

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

  public GenericModel<?> resolveById(GenericModelIdentifier id) throws PersistenceException {
    for (String collectionName : database.getCollectionNames()) {
      GenericModel<?> result = findById(new ObjectId(id.toString()), collectionName, null);
      if (result != null) {
        return result;
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T resolveById(GenericModelIdentifier id, Class<T> desiredObjectType) throws PersistenceException {
    return (T) findById(new ObjectId(id.toString()), null, desiredObjectType);
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
        Object setterValue = castPrimitiveTypes(reverseMapValue(mongoObj.get(propertyName), targetType), targetType);
        if (setterValue != null) {
          // special support of addX methods for collections
          if (setter.getName().startsWith("add") && setterValue instanceof Collection<?>) {
            for (Object entry : (Collection<?>) setterValue) {
              if (targetType.isAssignableFrom(entry.getClass())) {
                setter.invoke(model, entry);
              }
            }
          } else if (targetType.isAssignableFrom(setterValue.getClass())) {
            setter.invoke(model, setterValue);
          }
        }

      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  private Object castPrimitiveTypes(Object value, Class<?> targetType) {
    // Mongo returns always Integer for numbers (except Long)
    if (value instanceof Integer && Byte.class.equals(targetType)) {
      return Byte.valueOf(((Integer)value).byteValue());
    } else if (value instanceof Integer && Short.class.equals(targetType)) {
      return Short.valueOf(((Integer)value).shortValue());
    // Mongo returns always Double for fractional numbers
    } else if (value instanceof Double && Float.class.equals(targetType)) {
      return Float.valueOf(((Double)value).floatValue());
    // Mongo returns Strings for Characters - chars are always length==1
    } else if (value instanceof String && Character.class.equals(targetType)) {
      return ((String)value).charAt(0);
    } else {
      return value;
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
      try {
        String propertyName = getPropertyName(getter);
        mongoObj.append(propertyName, mapAndPersistValue(getter.invoke(model)));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new PersistenceException(e.getLocalizedMessage(), e);
      }
    }
  }

  protected Object mapAndPersistValue(Object value) throws PersistenceException {
    if (value instanceof GenericModel<?>) {
      save(value);
      return new DBRef(database, getCollectionName(value.getClass()), new ObjectId(((GenericModel<?>) value).getId().toString()));
    } else if (value instanceof Collection<?>) {
      List<Object> entryList = new ArrayList<>();
      for (Object listEntry : (Collection<?>) value) {
        entryList.add(mapAndPersistValue(listEntry));
      }
      return entryList;
    } else if (value instanceof Map<?, ?>) {
      List<BasicDBObject> entryList = new ArrayList<>();
      for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
        BasicDBObject entryObject = new BasicDBObject();
        entryObject.put(MAP_ENTRY_INDICATOR, true);
        entryObject.put(MAP_KEY_FIELD, mapAndPersistValue(entry.getKey()));
        entryObject.put(MAP_VALUE_FIELD, mapAndPersistValue(entry.getValue()));
        entryList.add(entryObject);
      }
      return entryList;
    } else if (value instanceof LocalDateTime) {
      return ((LocalDateTime) value).toDateTime().getMillis();
    } else if (value instanceof Class<?>) {
      return ((Class<?>) value).getName();
    } else if (value instanceof GenericModelIdentifier) {
      return new ObjectId(value.toString());
    } else {
      return value;
    }
  }

  protected Object reverseMapValue(Object mongoValue, Class<?> targetType) throws PersistenceException {
    if ((targetType == null || GenericModel.class.isAssignableFrom(targetType)) && mongoValue instanceof DBRef) {
      return resolveByReference((DBRef) mongoValue);
    } else if (mongoValue instanceof Collection) {

      // Return empty collection when it is passed in
      Collection<?> collection = (Collection<?>) mongoValue;
      if (collection.isEmpty()) {
        return collection;
      }

      // read first element and decide whether this is a map or a collection
      Iterator<?> collectionIterator = collection.iterator();
      Object firstElement = collectionIterator.next();

      if (firstElement instanceof DBObject && ((DBObject) firstElement).containsField(MAP_ENTRY_INDICATOR)) {

        Map<Object, Object> result = new HashMap<>();
        retrieveAndPutMapEntry((DBObject) firstElement, result);
        while (collectionIterator.hasNext()) {
          retrieveAndPutMapEntry((DBObject) collectionIterator.next(), result);
        }
        return result;

      } else {
        Collection<Object> result = new ArrayList<>();
        result.add(reverseMapValue(firstElement, null));
        while (collectionIterator.hasNext()) {
          result.add(reverseMapValue(collectionIterator.next(), null));
        }
        return result;
      }

    } else if ((targetType == null || LocalDateTime.class == targetType) && mongoValue instanceof Long) {
      return new LocalDateTime(((Long) mongoValue).longValue());
    } else if ((targetType == null || GenericModelIdentifier.class.isAssignableFrom(targetType)) && mongoValue instanceof ObjectId) {
      return new GenericModelIdentifier(mongoValue.toString());
    }

    return mongoValue;
  }

  private void retrieveAndPutMapEntry(@NotNull DBObject mongoObject, @NotNull Map<Object, Object> targetMap) throws PersistenceException {
    Object key = reverseMapValue(mongoObject.get(MAP_KEY_FIELD), null);
    Object value = reverseMapValue(mongoObject.get(MAP_VALUE_FIELD), null);

    targetMap.put(key, value);
  }

  protected List<Method> getMethodsStartingWith(Class<?> modelClass, String... prefixes) {
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

  @Override
  public void close() throws IOException {
  }

}

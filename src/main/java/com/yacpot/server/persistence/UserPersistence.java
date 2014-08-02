package com.yacpot.server.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.yacpot.core.persistence.PersistenceException;
import com.yacpot.core.persistence.mongodb.MongoDbApplication;
import com.yacpot.core.persistence.mongodb.MongoDbPersistence;
import com.yacpot.server.model.User;

public class UserPersistence extends MongoDbPersistence {

  private static final String EMAIL_PROPERTY_NAME = "email";

  public UserPersistence(MongoDbApplication application) {
    super(application);
  }

  public User findByEmail(String email) throws PersistenceException {
    DBCollection collection = getDatabase().getCollection(getCollectionName(User.class));
    DBObject mongoObj = collection.findOne(new BasicDBObject(EMAIL_PROPERTY_NAME, email));
    if (mongoObj == null) {
      return null;
    }

    User result = new User();
    fillModel(result, mongoObj);

    return result;
  }

  @Override
  protected void ensureIndex(Class<?> targetClass, DBCollection collection) {
    super.ensureIndex(targetClass, collection);
    if (User.class.isAssignableFrom(targetClass)) {
      collection.ensureIndex(new BasicDBObject(EMAIL_PROPERTY_NAME, 1), new BasicDBObject("unique", true));
    }
  }
}

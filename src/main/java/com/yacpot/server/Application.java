package com.yacpot.server;

import com.mongodb.MongoClient;
import com.yacpot.core.persistence.Persistence;
import com.yacpot.server.persistence.UserPersistence;

public class Application {

  private final String databaseName;

  private MongoClient mongoClient;

  public Application(String databaseName) {

    this.databaseName = databaseName;

    try {
      mongoClient = new MongoClient();
      mongoClient.getDB("admin").command("ping").throwOnError();
    } catch (Exception ex) {
      mongoClient = null;
      throw new IllegalStateException("Connection to mongo database failed: " + ex.getLocalizedMessage(), ex);
    }

  }

  public Persistence getPersistence() {
    return new Persistence(mongoClient, databaseName);
  }

  public UserPersistence getUserPersistence() {
    return new UserPersistence(mongoClient, databaseName);
  }
}

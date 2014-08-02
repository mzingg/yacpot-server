package com.yacpot.core.persistence.mongodb;

import com.mongodb.MongoClient;
import com.yacpot.core.persistence.Application;

public class MongoDbApplication implements Application {

  private final String databaseName;

  private final MongoClient mongoClient;

  public MongoDbApplication(String databaseName) {
    this.databaseName = databaseName;
    try {
      mongoClient = new MongoClient();
      mongoClient.getDB("admin").command("ping").throwOnError();
    } catch (Exception ex) {
      throw new IllegalStateException("Connection to mongo database failed: " + ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  public MongoClient getMongoClient() {
    return mongoClient;
  }
}

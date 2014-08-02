package com.yacpot.server;

import com.yacpot.core.persistence.mongodb.MongoDbApplication;
import com.yacpot.server.jetty.YacpotHandler;
import org.eclipse.jetty.server.Server;

public class StandaloneServer {

  public static void main(String[] args) throws Exception {

    MongoDbApplication app = new MongoDbApplication("yacpot");

    Server server = new Server(8080);
    server.setHandler(new YacpotHandler(app));
    server.start();
    server.join();
  }

}

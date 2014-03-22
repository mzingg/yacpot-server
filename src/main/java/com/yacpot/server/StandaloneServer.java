package com.yacpot.server;

import com.yacpot.server.jetty.YacpotHandler;
import org.eclipse.jetty.server.Server;

public class StandaloneServer {

  public static void main(String[] args) throws Exception {
    Server server = new Server(8080);
    server.setHandler(new YacpotHandler());
    server.start();
    server.join();
  }

}

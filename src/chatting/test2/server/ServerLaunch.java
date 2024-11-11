package chatting.test2.server;

import chatting.test2.server.datacommunication.ServerHandler;

public class ServerLaunch {
  public static void main(String[] args) {
    ServerHandler serverHandler = new ServerHandler();
    serverHandler.startServer();
  }
}
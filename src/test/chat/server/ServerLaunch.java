package test.chat.server;

import test.chat.server.datacommunication.ServerHandler;

public class ServerLaunch {
  public static void main(String[] args) {
    ServerHandler serverHandler = new ServerHandler();
    serverHandler.startServer();
  }
}
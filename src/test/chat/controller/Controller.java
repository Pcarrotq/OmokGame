package test.chat.controller;

import java.util.*;

import test.chat.client.datacommunication.ClientSocket;
import test.member.*;

public class Controller {
  private static Controller singleton = new Controller();
  public String username = null;
  public ClientSocket clientSocket;
  DBConnection dbConnection;

  private Controller() {
    clientSocket = new ClientSocket();
    dbConnection = new DBConnection();
  }

  public static Controller getInstance() {
    return singleton;
  }

  public ArrayList<String> friendList() {
	    ArrayList<String> friends = new ArrayList<>();
	    List<UserProfile> userList = dbConnection.getAllUsers();

	    for (UserProfile user : userList) {
	        friends.add(user.getNickname());  // 또는 user.getId()로 ID를 추가할 수도 있습니다.
	    }

	    return friends;
  }
}
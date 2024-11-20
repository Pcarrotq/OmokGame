package test.chat.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import test.chat.client.frame.*;
import test.member.db.DBConnection;
import test.member.db.User;
import test.member.dbLoad.UserProfile;
import test.server.connect.ClientSocket;

public class Controller {
	private static Controller singleton = new Controller();
  	public String username = null;
  	public ClientSocket clientSocket;
  	DBConnection userDB;

  	private Controller() {
  		clientSocket = new ClientSocket();
  		userDB = new DBConnection();
  	}

  	public static Controller getInstance() {
  		return singleton;
	}

  	public void insertDB(User user) {
		DBConnection db = new DBConnection();
	    db.addMember(user.getId(), user.getPassword(), user.getName(), user.getNickname(), user.getEmail(),
				user.getBirthYear(), user.getBirthMonth(), user.getBirthDay(), user.getGender(), user.getPhoneNumber(),
				user.getPoscalCode(), user.getAddress(), user.getDetailAddress(), user.getProfileImage());
	}

	public UserProfile findUser(ArrayList<JTextField> userInfos) {
	    UserProfile loggedInUser = userDB.getUserProfile();

	    if (loggedInUser != null) {
	        JOptionPane.showMessageDialog(null, "로그인 성공: " + loggedInUser.getNickname(), "로그인", JOptionPane.INFORMATION_MESSAGE);
	        return loggedInUser;
	    } else {
	        JOptionPane.showMessageDialog(null, "로그인 실패: 유저 정보를 찾을 수 없습니다.", "로그인", JOptionPane.ERROR_MESSAGE);
	        return null;
	    }
	}

	public void friendList() {
		List<UserProfile> friends = new ArrayList<UserProfile>();
		friends = userDB.getAllUsers();
	}
}
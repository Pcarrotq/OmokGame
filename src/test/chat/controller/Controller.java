package test.chat.controller;

import java.io.IOException;
import java.net.Socket;
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
  	    try {
  	        // 서버 주소와 포트를 지정하여 소켓 연결 생성
  	        Socket socket = new Socket("localhost", 8080); // 서버 주소와 포트를 실제 환경에 맞게 수정
  	        clientSocket = new ClientSocket(socket);
  	    } catch (IOException e) {
  	        e.printStackTrace();
  	        JOptionPane.showMessageDialog(null, "서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE);
  	    }
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
  	        // 로그인 성공 시 username 설정
  	        username = loggedInUser.getNickname();
  	        JOptionPane.showMessageDialog(null, "로그인 성공: " + username, "로그인", JOptionPane.INFORMATION_MESSAGE);
  	        return loggedInUser;
  	    } else {
  	        JOptionPane.showMessageDialog(null, "로그인 실패: 유저 정보를 찾을 수 없습니다.", "로그인", JOptionPane.ERROR_MESSAGE);
  	        return null;
  	    }
  	}

	public List<String> friendList() {
	    List<String> friendNames = new ArrayList<>();
	    List<UserProfile> users = userDB.getAllUsers();

	    if (users != null) {
	        for (UserProfile user : users) {
	            friendNames.add(user.getNickname()); // 닉네임을 리스트에 추가
	        }
	    }
	    return friendNames;
	}
}
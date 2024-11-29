package omok.game.lobby.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import omok.member.db.DBConnection;

public class ServerSocketThread extends Thread {
	Socket socket;
	LobbyServer server;
	BufferedReader in;		// 입력 담당 클래스
	PrintWriter out;		// 출력 담당 클래스
	String name;
	String threadName;
	
	public ServerSocketThread(LobbyServer server, Socket socket) {
	    this.server = server;
	    this.socket = socket;
	    threadName = super.getName();
	    System.out.println(socket.getInetAddress() + "님이 입장하였습니다.");
	    System.out.println("Thread Name : " + threadName);

	    try {
	        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	    } catch (IOException e) {
	        System.out.println("출력 스트림(out) 초기화 실패: " + e.getMessage());
	        server.removeClient(this); // 리스트에서 해당 클라이언트 제거
	    }
	}
	// 클라이언트로 메시지 출력
	public void sendMessage(String str) {
	    try {
	        if (out != null) {
	            out.println(str);
	        } else {
	            throw new IOException("출력 스트림이 null입니다.");
	        }
	    } catch (IOException e) {
	        System.out.println("메시지 전송 실패: " + e.getMessage());
	        server.removeClient(this);
	    }
	}
	// 쓰레드
	@Override
	public void run() {
	    try {
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	        String receivedMessage;
	        while ((receivedMessage = in.readLine()) != null) {
	            receivedMessage = receivedMessage.trim();

	            // 닉네임 설정 처리
	            if (receivedMessage.startsWith("[닉네임]")) {
	                name = receivedMessage.substring(5).trim(); // 닉네임 저장
	                System.out.println("클라이언트 닉네임 설정됨: " + name);
	                continue; // 다른 메시지 처리를 건너뜀
	            }

	            if (receivedMessage.startsWith("[CREATE_ROOM]")) {
	                String roomName = receivedMessage.substring(13); // 방 이름 추출
	                String roomInfo = "0|" + roomName + "|" + name + "|1/2|WAITING"; // 방 정보 생성
	                server.addRoom(roomInfo); // 서버에 방 추가 및 브로드캐스트
	            } else if (receivedMessage.startsWith("[REMOVE_ROOM]")) {
	                String roomName = receivedMessage.substring(13); // 방 이름 추출
	                server.removeRoom(roomName); // 방 삭제 처리
	            } else {
	                server.broadCasting(receivedMessage); // 일반 메시지 브로드캐스트
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("클라이언트 연결 종료: " + e.getMessage());
	    } finally {
	        server.removeClient(this); // 연결 종료 시 서버에서 제거
	        try {
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	private String fetchNicknameFromDB() {
	    if (name == null || name.trim().isEmpty()) {
	        name = DBConnection.getNickname(); // DB에서 닉네임 가져오기
	        System.out.println("DB에서 닉네임 가져옴: " + name);
	    }
	    return name;
	}
}
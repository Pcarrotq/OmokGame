package omok.game.lobby.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

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

	        // 클라이언트로부터 닉네임 수신
	        String initialMessage = in.readLine();
	        if (initialMessage != null && initialMessage.startsWith("[닉네임]")) {
	            name = initialMessage.substring(6); // 닉네임 저장
	        }

	        // 클라이언트로부터 메시지 수신 및 브로드캐스팅
	        String receivedMessage;
	        while ((receivedMessage = in.readLine()) != null) {
	            String broadcastMessage = name + ": " + receivedMessage; // 포맷 설정
	            server.broadCasting(broadcastMessage); // 서버로 브로드캐스트
	        }
	    } catch (IOException e) {
	        System.out.println(threadName + " 연결 종료.");
	    } finally {
	        server.removeClient(this); // 클라이언트 제거
	        try {
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
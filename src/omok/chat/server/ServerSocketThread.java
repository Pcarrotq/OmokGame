package omok.chat.server;

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
	ChatServer server;
	BufferedReader in;		// 입력 담당 클래스
	PrintWriter out;		// 출력 담당 클래스
	String name;
	String threadName;
	
	public ServerSocketThread(ChatServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();	// Thread 이름을 얻어옴
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다.");	// IP주소 얻어옴
		System.out.println("Thread Name : " + threadName);
	}
	// 클라이언트로 메시지 출력
	public void sendMessage(String str) {
		out.println(str);
	}
	// 쓰레드
	@Override
	public void run() {
	    try {
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

	        String initialMessage = in.readLine();
	        if (initialMessage != null && initialMessage.startsWith("[LOGIN]")) {
	            name = initialMessage.substring(7).trim(); // "[LOGIN]" 뒤의 사용자 이름 저장
	        } else {
	            name = "Unknown"; // 닉네임이 전달되지 않았을 경우 기본값 설정
	        }
	        System.out.println("클라이언트 닉네임: " + name);
	        server.broadCasting("[System] [" + name + "]님이 입장하셨습니다.");

	        String str_in;
	        while ((str_in = in.readLine()) != null) {
	            String formattedMessage = "[Server][Message][" + name + "] " + str_in;
	            server.broadCasting(formattedMessage); // 모든 클라이언트로 메시지 전송
	        }
	    } catch (IOException e) {
	        System.out.println(name + " 퇴장했습니다.");
	        server.broadCasting("[Server][System][" + name + "]님이 퇴장하셨습니다.");
	        server.removeClient(this);
	    } finally {
	        try {
	            socket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
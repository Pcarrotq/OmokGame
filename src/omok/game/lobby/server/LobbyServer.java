package omok.game.lobby.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LobbyServer {
	ServerSocket serverSocket;
	Socket socket;
	List<ServerSocketThread> list;		// ServerSocketThread 객체 저장
	
	public LobbyServer() {
	    list = new ArrayList<ServerSocketThread>(); // 수정된 코드
	    System.out.println("서버가 시작되었습니다.");
	}
	public void giveAndTake() {
		try {
			serverSocket = new ServerSocket(8080);		// 소켓 접속 대기
			serverSocket.setReuseAddress(true); 		// ServerSocket이 port를 바로 다시 사용한다 설정(port를 잡고있음)
			
			while(true) {
				socket = serverSocket.accept();			// accept -> 1. 소켓 접속 대기 2. 소켓 접속 허락
				ServerSocketThread thread = new ServerSocketThread(this, socket);	// this -> ChatServer 자신
				addClient(thread);		// 리스트에 쓰레드 객체 저장
				thread.start();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	// synchronized : 쓰레드들이 공유데이터를 함께 사용하지 못하도록 하는 것
	// 클라이언트가 입장 시 호출되며, 리스트에 클라이언트 담당 쓰레드 저장
	private synchronized void addClient(ServerSocketThread thread) {
	    list.add(thread);
	    System.out.println("Client 1명 입장. 총 " + list.size() + "명");
	}
	// 클라이언트가 퇴장 시 호출되며, 리스트에 클라이언트 담당 쓰레드 제거
	public synchronized void removeClient(ServerSocketThread thread) {
	    if (list.contains(thread)) {
	        list.remove(thread);
	        System.out.println("Client 1명 퇴장. 총 " + list.size() + "명");
	    } else {
	        System.out.println("리스트에 존재하지 않는 클라이언트입니다.");
	    }
	}
	// 모든 클라이언트에게 채팅 내용 전달
	public synchronized void broadCasting(String message) {
	    System.out.println("브로드캐스팅 시작: " + message);
	    List<ServerSocketThread> invalidThreads = new ArrayList<>();

	    for (ServerSocketThread thread : list) {
	        if (thread.isAlive() && thread.out != null) {
	            try {
	                thread.sendMessage(message);
	            } catch (Exception e) {
	                System.out.println("메시지 전송 실패: " + e.getMessage());
	                invalidThreads.add(thread);
	            }
	        } else {
	            invalidThreads.add(thread);
	        }
	    }

	    // 유효하지 않은 클라이언트를 리스트에서 제거
	    for (ServerSocketThread thread : invalidThreads) {
	        removeClient(thread);
	    }
	}
}
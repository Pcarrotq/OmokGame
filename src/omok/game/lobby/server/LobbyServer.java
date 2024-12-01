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
	private List<String> roomList = new ArrayList<>();
	
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
	public synchronized void addClient(ServerSocketThread thread) {
	    for (ServerSocketThread existingThread : list) {
	        if (existingThread.getSocket().getInetAddress().equals(thread.getSocket().getInetAddress())
	            && existingThread.getSocket().getPort() == thread.getSocket().getPort()) {
	            System.out.println("중복 연결이 감지되어 추가하지 않습니다.");
	            return;
	        }
	    }
	    list.add(thread);
	    System.out.println("Client 1명 입장. 총 " + list.size() + "명");
	}
	// 클라이언트가 퇴장 시 호출되며, 리스트에 클라이언트 담당 쓰레드 제거
	public synchronized void removeClient(ServerSocketThread thread) {
	    if (list.contains(thread)) {
	        list.remove(thread);
	        System.out.println("Client 1명 퇴장. 총 " + list.size() + "명");

	        // 방 상태 업데이트
	        thread.handleClientDisconnection();
	    } else {
	        System.out.println("리스트에 존재하지 않는 클라이언트입니다.");
	    }
	}
	// 모든 클라이언트에게 채팅 내용 전달
	public synchronized void broadCasting(String message) {
	    // "null: " 또는 불필요한 텍스트 제거
	    message = message.replace("null: ", "").trim();
	    
	    // 메시지가 비어 있지 않은 경우만 브로드캐스트
	    if (message.isEmpty()) return;

	    System.out.println("브로드캐스팅 시작: " + message);
	    List<ServerSocketThread> invalidThreads = new ArrayList<>();

	    for (ServerSocketThread thread : list) {
	        try {
	            if (thread.isAlive() && thread.out != null) {
	                thread.sendMessage(message);
	            } else {
	                invalidThreads.add(thread); // 유효하지 않은 클라이언트
	            }
	        } catch (Exception e) {
	            invalidThreads.add(thread);
	        }
	    }

	    // 유효하지 않은 클라이언트를 리스트에서 제거
	    for (ServerSocketThread thread : invalidThreads) {
	        removeClient(thread);
	    }
	}
	
	public synchronized void addRoom(String roomInfo) {
	    if (!roomInfo.matches("^\\d+\\|[^|]+\\|[^|]+\\|\\d+/\\d+\\|WAITING$")) {
	        System.out.println("잘못된 방 형식: " + roomInfo);
	        return; // 형식이 잘못된 방은 추가하지 않음
	    }
	    roomList.add(roomInfo);
	    System.out.println("방 추가됨: " + roomInfo);
	    broadcastRoomList(); // 방 리스트 브로드캐스트
	}

	public synchronized void removeRoom(String roomName) {
	    roomList.removeIf(room -> room.contains("|" + roomName + "|"));
	    System.out.println("방 삭제됨: " + roomName);
	    broadcastRoomList(); // 방 리스트 브로드캐스트
	}

	public synchronized void broadcastRoomList() {
	    String roomListMessage = "[ROOM_LIST] " + String.join("\n", roomList);
	    System.out.println("브로드캐스트 방 리스트: " + roomListMessage);
	    broadCasting(roomListMessage);
	}
	
	// 방 리스트 반환
	public synchronized List<String> getRoomList() {
	    return new ArrayList<>(roomList); // 리스트 복사본 반환 (원본 보호)
	}
	
	// 방 정보 업데이트
	public synchronized void updateRoom(String oldRoom, String newRoom) {
	    int index = roomList.indexOf(oldRoom);
	    if (index != -1) {
	        roomList.set(index, newRoom); // 방 상태 변경
	        broadcastRoomList(); // 방 리스트 브로드캐스트
	    } else {
	        System.out.println("업데이트할 방을 찾을 수 없습니다: " + oldRoom);
	    }
	}
}
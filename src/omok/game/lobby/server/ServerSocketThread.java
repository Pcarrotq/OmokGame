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
	
    public Socket getSocket() {
        return socket;
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
	    	server.addClient(this); // 클라이언트 추가
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	        String receivedMessage;
	        while ((receivedMessage = in.readLine()) != null) {
	            receivedMessage = receivedMessage.trim();
	            System.out.println("클라이언트로부터 메시지 수신: " + receivedMessage);

	            // 닉네임 설정 처리
	            if (receivedMessage.startsWith("[닉네임]")) {
	                name = receivedMessage.substring(5).trim();
	                if (name.isEmpty() || name.contains("|") || name.length() > 20) { // 닉네임 유효성 검사
	                    System.out.println("유효하지 않은 닉네임입니다. 연결 종료: " + name);
	                    sendMessage("[INVALID_NICKNAME]");
	                    socket.close();
	                    return;
	                }
	                System.out.println("클라이언트 닉네임 설정됨: " + name);
	                continue;
	            }
	            
	            // 방 생성 처리
	            if (receivedMessage.startsWith("[CREATE_ROOM]")) {
	                String roomName = receivedMessage.substring(13).trim();
	                if (roomName.isEmpty() || roomName.contains("|") || roomName.length() > 30) {
	                    System.out.println("유효하지 않은 방 이름: " + roomName);
	                    sendMessage("[INVALID_ROOM_NAME]");
	                    return;
	                }
	                if (name == null || name.isEmpty()) {
	                    name = DBConnection.getNickname();
	                }
	                String roomInfo = String.join("|", "0", roomName, name, "1/2", "WAITING");
	                server.addRoom(roomInfo);
	                server.broadcastRoomList();
	                continue;
	            }
	            if (receivedMessage.startsWith("[REMOVE_ROOM]")) {
	                String roomName = receivedMessage.substring(13).trim();
	                server.removeRoom(roomName);
	                System.out.println("방 삭제 요청 처리됨: " + roomName);
	                continue;
	            }
	            if (receivedMessage.startsWith("[JOIN_ROOM]")) {
	                String roomName = receivedMessage.substring(11).trim(); // 방 이름 추출 및 공백 제거
	                synchronized (server) {
	                    for (String room : server.getRoomList()) {
	                        if (room.contains("|" + roomName + "|")) {
	                            String[] roomDetails = room.split("\\|");
	                            String currentPlayers = roomDetails[3];
	                            String[] playerCounts = currentPlayers.split("/");

	                            int currentCount = Integer.parseInt(playerCounts[0]);
	                            int maxCount = Integer.parseInt(playerCounts[1]);

	                            if (currentCount < maxCount && room.endsWith("WAITING")) {
	                                // 방 입장 가능
	                                currentCount++;
	                                String updatedPlayers = currentCount + "/" + maxCount;
	                                String updatedRoom = room.replace(currentPlayers, updatedPlayers);

	                                if (currentCount == maxCount) {
	                                    updatedRoom = updatedRoom.replace("WAITING", "IN_PROGRESS");
	                                }

	                                server.updateRoom(room, updatedRoom);
	                                sendMessage("[JOIN_SUCCESS]");
	                                server.broadcastRoomList();
	                                return;
	                            } else if (room.endsWith("IN_PROGRESS")) {
	                                // 방이 진행 중
	                                sendMessage("[JOIN_FAILURE_STARTED]");
	                                return;
	                            }
	                        }
	                    }
	                    // 방이 존재하지 않음
	                    sendMessage("[JOIN_FAILURE]");
	                }
	                
	                continue;
	            }
	            if (receivedMessage.startsWith("[ROOM_LIST]")) {
	                // 클라이언트로 방 리스트 메시지를 전송
	                String roomData = receivedMessage.length() > 12 ? receivedMessage.substring(12).trim() : "";
	                server.broadcastRoomList();
	                continue;
	            }
	            if (receivedMessage.startsWith("[START_GAME]")) {
	                String roomName = receivedMessage.substring(12).trim();
	                synchronized (server) {
	                    for (String room : server.getRoomList()) {
	                        if (room.contains("|" + roomName + "|") && room.endsWith("WAITING")) {
	                            String updatedRoom = room.replace("WAITING", "IN_PROGRESS");
	                            server.updateRoom(room, updatedRoom);
	                            server.broadcastRoomList();
	                            System.out.println("방 상태가 IN_PROGRESS로 변경됨: " + roomName);
	                            return;
	                        }
	                    }
	                    sendMessage("[START_GAME_FAILURE]");
	                }
	                
	                continue;
	            }
	            else {
	                server.broadCasting(receivedMessage); // 일반 메시지 브로드캐스트
	            }
	        }
	        
            // 일반 메시지 브로드캐스트
            server.broadCasting(receivedMessage);
	    } catch (IOException e) {
	        System.out.println("클라이언트 연결 종료: " + e.getMessage());
	    } finally {
	        server.removeClient(this); // 연결 종료 시 서버에서 제거
	        handleClientDisconnection();
	    }
	}
	
	private String fetchNicknameFromDB() {
	    if (name == null || name.trim().isEmpty()) {
	        name = DBConnection.getNickname(); // DB에서 닉네임 가져오기
	        System.out.println("DB에서 닉네임 가져옴: " + name);
	    }
	    return name;
	}
	
	public void handleClientDisconnection() {
	    for (String room : server.getRoomList()) {
	        if (room.contains("|" + name + "|")) {
	            String[] roomDetails = room.split("\\|");
	            String currentPlayers = roomDetails[3];
	            String[] playerCounts = currentPlayers.split("/");

	            int currentCount = Integer.parseInt(playerCounts[0]) - 1;
	            String updatedPlayers = currentCount + "/" + playerCounts[1];
	            String updatedRoom = room.replace(currentPlayers, updatedPlayers);

	            if (currentCount == 0) {
	                server.removeRoom(roomDetails[1]); // 방 삭제
	            } else {
	                server.updateRoom(room, updatedRoom);
	            }

	            server.broadcastRoomList();
	            break;
	        }
	    }
	}
	
	private void handleJoinRoom(String roomName) {
	    synchronized (server) {
	        roomName = roomName.trim(); // 클라이언트로부터 받은 방 이름 트림 처리

	        for (String room : server.getRoomList()) {
	            String[] roomDetails = room.split("\\|");
	            String serverRoomName = roomDetails[1].trim(); // 서버의 방 이름 트림 처리

	            if (serverRoomName.equals(roomName)) {
	                String currentPlayers = roomDetails[3];
	                String[] playerCounts = currentPlayers.split("/");

	                int currentCount = Integer.parseInt(playerCounts[0]);
	                int maxCount = Integer.parseInt(playerCounts[1]);

	                if (currentCount < maxCount && room.endsWith("WAITING")) {
	                    // 입장 가능 상태
	                    currentCount++;
	                    String updatedPlayers = currentCount + "/" + maxCount;
	                    String updatedRoom = room.replace(currentPlayers, updatedPlayers);

	                    if (currentCount == maxCount) {
	                        updatedRoom = updatedRoom.replace("WAITING", "IN_PROGRESS");
	                    }

	                    server.updateRoom(room, updatedRoom);
	                    sendMessage("[JOIN_SUCCESS]");
	                    server.broadcastRoomList();
	                    return;
	                } else if (room.endsWith("IN_PROGRESS")) {
	                    // 방이 이미 게임 중 상태
	                    sendMessage("[JOIN_FAILURE_STARTED]");
	                    return;
	                }
	            }
	        }
	        // 방 이름이 존재하지 않거나 입장 불가 상태
	        sendMessage("[JOIN_FAILURE]");
	        System.out.println("입장 실패: 방을 찾을 수 없거나 상태가 올바르지 않음 (" + roomName + ")");
	    }
	}
}
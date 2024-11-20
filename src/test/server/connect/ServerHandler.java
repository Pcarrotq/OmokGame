package test.server.connect;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerHandler {
    private ServerSocket serverSocket;
    private List<ClientSocket> clients;
    private boolean running;
    private Map<String, List<ClientSocket>> rooms = new HashMap<>(); // 방 이름과 클라이언트 리스트 매핑

    public ServerHandler() {
        clients = new ArrayList<>();
        rooms = new HashMap<>();
        running = false;
    }

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("서버 시작됨");

        // 클라이언트 연결 대기
        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientSocket client = new ClientSocket(clientSocket);
                    clients.add(client);

                    // 클라이언트 개별 처리
                    new Thread(() -> handleClient(client)).start();
                } catch (IOException e) {
                    // 서버 소켓이 닫힌 경우 오류 무시
                    if (running) {
                        System.err.println("서버 소켓 오류: " + e.getMessage());
                    } else {
                        System.out.println("서버가 정상적으로 중지되었습니다.");
                    }
                }
            }
        }).start();
    }

    private void handleClient(ClientSocket client) {
        try {
            String message;
            while ((message = client.readMessage()) != null) {
                if (message.startsWith("/create_room ")) {
                    String roomName = message.substring(13).trim();
                    createRoom(client, roomName);
                } else if (message.startsWith("/remove_room ")) {
                    String roomName = message.substring(13).trim();
                    removeRoom(client, roomName);
                } else if (message.startsWith("/join_room ")) {
                    String roomName = message.substring(11).trim();
                    joinRoom(client, roomName);
                } else if (message.startsWith("/chat ")) {
                    String chatMessage = message.substring(6).trim();
                    sendChat(client, chatMessage);
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Socket closed")) {
                System.out.println("클라이언트 연결 종료 (소켓 닫힘)");
            } else {
                e.printStackTrace();
            }
        } finally {
            clients.remove(client);
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("클라이언트 소켓 닫기 실패: " + e.getMessage());
            }
            System.out.println("클라이언트 연결 종료");
        }
    }

	// 방 생성 및 브로드캐스트
    private void createRoom(ClientSocket client, String roomName) {
        synchronized (rooms) {
            if (!rooms.containsKey(roomName)) {
                rooms.put(roomName, new ArrayList<>()); // 방 추가
                broadcastRoomList(); // 방 목록 브로드캐스트
                System.out.println(client.getUsername() + " created room: " + roomName);
            } else {
                client.sendMessage("이미 존재하는 방입니다.");
            }
        }
    }

    private void joinRoom(ClientSocket client, String roomName) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).add(client);
            System.out.println(client.getUsername() + " joined room: " + roomName);
        } else {
            client.sendMessage("방이 존재하지 않습니다.");
        }
    }

    private void sendChat(ClientSocket client, String message) {
        for (ClientSocket recipient : clients) {
            recipient.sendMessage(client.getUsername() + ": " + message);
        }
    }

	// 방 목록 브로드캐스트
    private void broadcastRoomList() {
        String roomList = String.join(",", rooms.keySet()); // 방 이름들을 ','로 연결
        synchronized (clients) {
            for (ClientSocket client : clients) {
                client.sendMessage("/room " + roomList);
            }
        }
    }
    
    private void removeRoom(ClientSocket client, String roomName) {
        synchronized (rooms) {
            if (rooms.containsKey(roomName)) {
                rooms.remove(roomName); // 방 삭제
                broadcastRoomList(); // 모든 클라이언트에 업데이트된 방 목록 브로드캐스트
                System.out.println(client.getUsername() + " removed room: " + roomName);
            } else {
                client.sendMessage("삭제할 방이 존재하지 않습니다.");
            }
        }
    }

    public void stopServer() throws IOException {
        running = false;
        for (ClientSocket client : clients) {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("클라이언트 소켓 닫기 실패: " + e.getMessage());
            }
        }
        clients.clear();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("서버 소켓 닫기 실패: " + e.getMessage());
            }
        }
        System.out.println("서버 중지됨");
    }
    
    public boolean isRunning() {
        return running; // 서버 상태 반환
    }
}
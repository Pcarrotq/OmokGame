package omok.game.board.server;

import java.io.*;
import java.net.*;
import java.util.*;

import omok.game.board.frame.BoardMap;

public class GameServer {
    private static final int PORT = 8081;
    private BoardMap map = new BoardMap();
    private boolean blackTurn = true;
    private List<ClientHandler> clients = new ArrayList<>();
    private Map<String, Boolean> roomTurns = new HashMap<>(); // 방별 턴 관리
    private Map<String, List<ClientHandler>> roomClients = new HashMap<>(); // 방별 클라이언트 관리
    private Map<String, BoardMap> roomMaps = new HashMap<>();
    private Map<String, Object> roomLocks = new HashMap<>();

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버 시작");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, clientSocket); // `this` 전달
                clients.add(client); // 리스트에 추가
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean placeStone(int x, int y, String roomName, ClientHandler client) {
        Object roomLock = roomLocks.computeIfAbsent(roomName, k -> new Object());
        final short BLACK = 1;
        final short WHITE = 2;
        synchronized (roomLock) {
            // 현재 턴 확인
            boolean isBlackTurn = roomTurns.getOrDefault(roomName, true);
            
            // 클라이언트의 턴인지 확인
            if (client.isBlackPlayer() != isBlackTurn) {
                client.sendMessage("INVALID_TURN");
                System.out.println("Invalid turn for client: " + client + ", Room: " + roomName);
                return false;
            }

            BoardMap map = roomMaps.get(roomName);
            if (map == null || map.getXY(y, x) != 0) {
                client.sendMessage("PLACE_FAIL");
                System.out.println("Place failed at (" + x + ", " + y + "), Room: " + roomName);
                return false;
            }

            // 돌 배치 및 승리 체크
            map.setMap(y, x, isBlackTurn ? BLACK : WHITE);
            System.out.println("Stone placed at (" + x + ", " + y + ") by " + (isBlackTurn ? "Black" : "White"));
            boolean isWin = map.winCheck(x, y);

            if (isWin) {
                broadcastToRoom(roomName, "WIN " + (isBlackTurn ? "BLACK" : "WHITE"));
                System.out.println("Game won by " + (isBlackTurn ? "Black" : "White") + " in Room: " + roomName);
                resetRoom(roomName);
            } else {
                toggleTurn(roomName);
                broadcastToRoom(roomName, "UPDATE " + x + " " + y);
                System.out.println("Turn toggled for Room: " + roomName);
            }
            return true;
        }
    }
    public synchronized void handleTimeout(String roomName) {
        boolean isBlackTurn = roomTurns.getOrDefault(roomName, true);
        String winner = isBlackTurn ? "WHITE" : "BLACK"; // 현재 턴의 상대방이 승리

        // 방에 승리 메시지 전송
        broadcastToRoom(roomName, "WIN " + winner);

        // 방 초기화
        resetRoom(roomName);
    }
    private void resetRoom(String roomName) {
        if (roomMaps.containsKey(roomName)) {
            roomMaps.get(roomName).reset(); // 보드 초기화
            roomTurns.put(roomName, true); // 턴 초기화
        }
    }
    
    public void toggleTurn(String roomName) {
        roomTurns.put(roomName, !roomTurns.getOrDefault(roomName, true)); // 턴 전환
    }

    public boolean isBlackTurn(String roomName) {
        return roomTurns.getOrDefault(roomName, true); // 현재 턴 반환 (기본값: 흑)
    }

    private synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    private synchronized void broadcastToRoom(String roomName, String message) {
        List<ClientHandler> clientsInRoom = roomClients.get(roomName);
        if (clientsInRoom != null) {
            for (ClientHandler client : clientsInRoom) {
            	System.out.println("Sending message to client in room: " + roomName + " Message: " + message);
                client.sendMessage(message);
            }
        }
    }
    
    private synchronized boolean joinRoom(String roomName, ClientHandler client) {
        if (!roomClients.containsKey(roomName)) {
            roomClients.put(roomName, new ArrayList<>());
        }
        List<ClientHandler> clientsInRoom = roomClients.get(roomName);
        if (clientsInRoom.size() < 2) { // 최대 2명까지만 허용
            client.setBlackPlayer(clientsInRoom.isEmpty()); // 첫 번째 클라이언트는 흑, 두 번째는 백
            clientsInRoom.add(client);
            client.setRoomName(roomName);
            System.out.println("Client joined room: " + roomName + ", Current clients: " + clientsInRoom.size());
            return true;
        }
        
        System.out.println("Join failed for room: " + roomName + ", Room is full");
        return false;
    }

    public synchronized void leaveRoom(String roomName, ClientHandler client) {
        List<ClientHandler> clientsInRoom = roomClients.get(roomName);
        if (clientsInRoom != null) {
            clientsInRoom.remove(client);
            System.out.println("클라이언트가 방에서 나갔습니다: " + roomName);

            if (clientsInRoom.isEmpty()) {
                roomClients.remove(roomName); // 방 삭제
                System.out.println("방이 비어 삭제되었습니다: " + roomName);
            }
        }
    }
    
    public synchronized void removeClient(ClientHandler client) {
        // 방에서 클라이언트 제거
        if (client.getRoomName() != null) {
            String roomName = client.getRoomName();
            leaveRoom(roomName, client); // 방에서 클라이언트 제거

            // 만약 방이 비었다면 삭제
            if (!roomClients.containsKey(roomName) || roomClients.get(roomName).isEmpty()) {
                roomClients.remove(roomName);
                System.out.println("방 삭제됨: " + roomName);
            }
        }

        // 전체 클라이언트 목록에서 제거
        clients.remove(client);
        System.out.println("클라이언트 연결 종료. 현재 클라이언트 수: " + clients.size());
    }
    
    public synchronized void broadcastChatMessage(String roomName, String message) {
        List<ClientHandler> clientsInRoom = roomClients.get(roomName);
        if (clientsInRoom != null) {
            for (ClientHandler client : clientsInRoom) {
                client.sendMessage("[CHAT] " + message); // 방별로 채팅 메시지 전달
            }
        }
    }

    private class ClientHandler implements Runnable {
        private GameServer server;
        private Socket socket;
        private String roomName;
        private PrintWriter out;
        private boolean isBlackPlayer;

        public ClientHandler(GameServer server, Socket socket) {
            this.server = server;
            this.socket = socket;
        }
        
        public String getRoomName() {
            return roomName;
        }
        
        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
        
        public boolean isBlackPlayer() {
            return isBlackPlayer;
        }

        public void setBlackPlayer(boolean isBlackPlayer) {
            this.isBlackPlayer = isBlackPlayer;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(" ");
                    if (parts[0].equals("JOIN_ROOM")) {
                        String requestedRoomName = parts[1];
                        if (server.joinRoom(requestedRoomName, this)) {
                            out.println("[JOIN_SUCCESS]");
                        } else {
                            out.println("[JOIN_FAIL]");
                        }
                    } else if (parts[0].equals("LEAVE_ROOM")) {
                        // 클라이언트가 방에서 나가는 요청
                        if (roomName != null) {
                            server.leaveRoom(roomName, this);
                            roomName = null;
                        }
                    } else if (parts[0].equals("PLACE")) {
                        // 돌 놓기 요청
                        if (roomName == null) {
                            out.println("INVALID_ROOM");
                            continue;
                        }

                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);

                        if (server.placeStone(x, y, roomName, this)) {
                            sendMessage("PLACE_SUCCESS " + x + " " + y);
                        } else {
                            sendMessage("PLACE_FAIL " + x + " " + y);
                        }
                    } else if (parts[0].equals("TIMEOUT")) {
                        String roomName = this.roomName;
                        if (roomName != null) {
                            server.handleTimeout(roomName);
                        }
                    } else if (message.startsWith("[CHAT]")) {
                        // 채팅 메시지 처리
                        String chatMessage = message.substring(6); // "[CHAT] " 이후의 메시지 추출
                        if (roomName != null) {
                            server.broadcastChatMessage(roomName, chatMessage);
                        } else {
                            sendMessage("[ERROR] You are not in a room.");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.removeClient(this); // 클라이언트 제거
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
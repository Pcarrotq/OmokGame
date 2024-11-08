package test.gameServer;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class OmokServer {
    private List<Room> rooms = new ArrayList<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private Map<String, String> currentTurn = new HashMap<>();
    private Connection connection;
    
    public static void main(String[] args) {
        OmokServer server = new OmokServer();
        server.connectDatabase(); // 데이터베이스 연결
        server.startServer(); // 서버 시작
        server.closeDatabase(); // 서버 종료 시 데이터베이스 연결 닫기
    }

    public void connectDatabase() {
        try {
            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // Oracle DB 주소
            String user = "sys as sysdba"; // DB 사용자명
            String password = "chocolate5871"; // DB 비밀번호
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeDatabase() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started on port 12345");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addRoom(String roomName) {
        String roomId = UUID.randomUUID().toString(); // 고유한 방 ID 생성
        Room newRoom = new Room(roomId, roomName);
        rooms.add(newRoom);
        currentTurn.put(roomId, "BLACK"); // 방이 생성되면 흑돌이 먼저 두도록 설정
        broadcastRoomList();
    }

    public synchronized void removeRoom(String roomId) {
        rooms.removeIf(room -> room.getRoomId().equals(roomId));
        currentTurn.remove(roomId); // 방 제거 시 턴 정보도 삭제
        broadcastRoomList();
    }

    public synchronized void broadcastRoomList() {
        String roomListMessage = "ROOM_LIST " + rooms.stream()
            .map(room -> room.getRoomId() + " " + room.getRoomName())
            .collect(Collectors.joining(" "));
        for (ClientHandler client : clients) {
            client.sendMessage(roomListMessage);
        }
    }

    public synchronized void broadcastMove(String roomId, String moveInfo, String playerColor) {
        if (!currentTurn.get(roomId).equals(playerColor)) {
            return; // 턴이 아닌 경우 무시
        }
        for (ClientHandler client : clients) {
            if (roomId.equals(client.getRoomId())) {
                client.sendMessage("MOVE " + moveInfo);
            }
        }
        // 턴 변경
        currentTurn.put(roomId, playerColor.equals("BLACK") ? "WHITE" : "BLACK");
    }

    public synchronized void broadcastChat(String roomId, String message) {
        for (ClientHandler client : clients) {
            if (roomId.equals(client.getRoomId())) {
                client.sendMessage("CHAT " + message);
            }
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        // 방에 클라이언트가 없으면 방을 제거하는 로직을 추가할 수 있음
    }

    private class Room {
        private String roomId; // 고유 방 ID
        private String roomName; // 방 이름

        public Room(String roomId, String roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getRoomName() {
            return roomName;
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String roomId; // 클라이언트가 참여한 방 ID

        public ClientHandler(Socket socket, OmokServer server) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("CREATE_ROOM")) {
                        String roomName = message.split(" ", 2)[1];
                        addRoom(roomName);
                    } else if (message.startsWith("JOIN_ROOM")) {
                        roomId = message.split(" ", 2)[1];
                        out.println("JOINED_ROOM " + roomId);
                    } else if (roomId != null) { // 클라이언트가 방에 있는지 확인
                        if (message.startsWith("CHAT")) {
                            String chatMessage = message.substring(5);
                            broadcastChat(roomId, chatMessage);
                        } else if (message.startsWith("PLACE_STONE")) {
                            String moveInfo = message.split(" ", 2)[1];
                            broadcastMove(roomId, moveInfo, "BLACK"); // 기본적으로 흑돌로 처리
                        }
                    } else if (message.startsWith("GET_ROOMS")) {
                        broadcastRoomList();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                removeClient(this);
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

        public String getRoomId() {
            return roomId;
        }
    }
}
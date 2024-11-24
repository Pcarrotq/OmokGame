package test.server.connect;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerHandler {
    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private List<Client> clients = new Vector<>();
    private Map<String, List<Client>> rooms = new HashMap<>(); // 방 이름과 클라이언트 매핑
    private boolean running;
    
    public ServerHandler() {
        running = false;
    }

    public void startServer(int port) {
        executorService = new ThreadPoolExecutor(
            10, // 코어 스레드 개수
            100, // 최대 스레드 개수
            120L, // 유휴 스레드 유지 시간
            TimeUnit.SECONDS,
            new SynchronousQueue<>()
        );

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("서버 시작됨");

            Runnable acceptTask = () -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("연결 수락: " + clientSocket.getRemoteSocketAddress());
                        Client client = new Client(clientSocket);
                        clients.add(client);
                        System.out.println("현재 연결된 클라이언트 수: " + clients.size());
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            };

            executorService.submit(acceptTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isRunning() {
        return running; // 서버 상태 반환
    }

    public void stopServer() {
        try {
            for (Client client : clients) {
                client.close();
            }
            clients.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }

            System.out.println("서버 중지됨");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Client {
        private Socket socket;
        private String username;

        public Client(Socket socket) {
            this.socket = socket;
            receive();
        }

        private void receive() {
            Runnable receiveTask = () -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("메시지 수신: " + message);

                        if (message.startsWith("/set_username ")) {
                            this.username = message.substring(13).trim();
                            System.out.println("사용자 이름 설정됨: " + username);
                        } else if (message.startsWith("/create_room ")) {
                            String roomName = message.substring(13).trim();
                            createRoom(roomName);
                        } else if (message.startsWith("/remove_room ")) {
                            String roomName = message.substring(13).trim();
                            removeRoom(roomName);
                        } else if (message.startsWith("/join_room ")) {
                            String roomName = message.substring(11).trim();
                            joinRoom(roomName);
                        } else if (message.startsWith("/chat ")) {
                            String chatMessage = message.substring(6).trim();
                            sendChat(chatMessage);
                        } else {
                            broadcast(username + ": " + message);
                        }
                    }
                } catch (IOException e) {
                    try {
                        clients.remove(this);
                        socket.close();
                    } catch (IOException ignored) {}
                }
            };

            executorService.submit(receiveTask);
        }

        private void send(String message) {
            Runnable sendTask = () -> {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
                    writer.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            executorService.submit(sendTask);
        }

        private void broadcast(String message) {
            for (Client client : clients) {
                if (client != this) {
                    client.send(message);
                }
            }
        }

        private void createRoom(String roomName) {
            synchronized (rooms) {
                if (!rooms.containsKey(roomName)) {
                    rooms.put(roomName, new ArrayList<>());
                    broadcastRoomList();
                    System.out.println(username + " created room: " + roomName);
                } else {
                    send("이미 존재하는 방입니다.");
                }
            }
        }

        private void joinRoom(String roomName) {
            synchronized (rooms) {
                if (rooms.containsKey(roomName)) {
                    rooms.get(roomName).add(this);
                    System.out.println(username + " joined room: " + roomName);
                } else {
                    send("방이 존재하지 않습니다.");
                }
            }
        }

        private void removeRoom(String roomName) {
            synchronized (rooms) {
                if (rooms.containsKey(roomName)) {
                    rooms.remove(roomName);
                    broadcastRoomList();
                    System.out.println(username + " removed room: " + roomName);
                } else {
                    send("삭제할 방이 존재하지 않습니다.");
                }
            }
        }

        private void sendChat(String message) {
            synchronized (rooms) {
                for (Client client : clients) {
                    client.send(username + ": " + message);
                }
            }
        }

        private void broadcastRoomList() {
            synchronized (rooms) {
                String roomList = String.join(",", rooms.keySet());
                for (Client client : clients) {
                    client.send("/room " + roomList);
                }
            }
        }

        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
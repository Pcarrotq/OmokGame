package omok.chat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerHandler {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private ServerSocket serverSocket;
    private final List<Client> connections = new Vector<>();

    public void startServer() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(5000));
            System.out.println("서버 연결 대기 중...");

            // 클라이언트 연결을 수락하는 스레드
            executorService.submit(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("연결 수락: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName());
                        Client client = new Client(socket);
                        connections.add(client);
                        System.out.println("연결 개수: " + connections.size());
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            for (Client client : connections) {
                client.close();
            }
            connections.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Client {
        private final Socket socket;
        private String userName;

        Client(Socket socket) {
            this.socket = socket;
            receive();
        }

        void receive() {
            executorService.submit(() -> {
                byte[] buffer = new byte[1024];
                try (InputStream inputStream = socket.getInputStream()) {
                    while (true) {
                        int readBytes = inputStream.read(buffer);
                        if (readBytes == -1) throw new IOException("연결 끊김");

                        String receivedMessage = new String(buffer, 0, readBytes, "UTF-8");
                        String[] parts = receivedMessage.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[0];
                            String receiver = parts[1];
                            String message = parts[2];
                            userName = sender;

                            System.out.println("요청 처리 중: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName());

                            send(receivedMessage); // 발신자에게 확인 메시지 전송

                            for (Client client : connections) {
                                if (client.userName != null && client.userName.equals(receiver) && !sender.equals(receiver)) {
                                    client.send(receivedMessage);
                                }
                            }
                        } else {
                            System.out.println("잘못된 메시지 형식: " + receivedMessage);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("클라이언트 연결 종료: " + socket.getRemoteSocketAddress());
                    connections.remove(this);
                    close();
                }
            });
        }

        void send(String message) {
            executorService.submit(() -> {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes("UTF-8"));
                    outputStream.flush();
                    System.out.println("메시지 전송: " + message);
                } catch (IOException e) {
                    System.out.println("메시지 전송 실패");
                    connections.remove(this);
                    close();
                }
            });
        }

        void close() {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
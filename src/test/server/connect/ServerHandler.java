package test.server.connect;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerHandler {
    private ServerSocket serverSocket;
    private List<ClientSocket> clients;
    private boolean running;

    public ServerHandler() {
        clients = new ArrayList<>();
        running = false;
    }

    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("서버 시작됨");

        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientSocket client = new ClientSocket(clientSocket);
                    clients.add(client);
                    System.out.println("클라이언트 연결됨");
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopServer() throws IOException {
        running = false;
        for (ClientSocket client : clients) {
            client.close();
        }
        clients.clear();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("서버 중지됨");
    }

    public int getClientCount() {
        return clients.size();
    }

    public boolean isRunning() {
        return running;
    }
}
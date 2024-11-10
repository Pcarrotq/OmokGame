package test.personalChat.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import test.personalChat.frame.ChatWindowPanel;

public class ServerSocket {
    private Socket socket;
    private ChatWindowPanel chatWindowPanel;
    private String userName;
    private java.net.ServerSocket serverSocket;
    private Socket clientSocket;
    
    // 서버에 연결하는 메서드
    public void connect(InetSocketAddress address) throws IOException {
        socket = new Socket(); // 새로운 소켓 생성
        socket.connect(address); // 서버 주소에 연결
        System.out.println("서버에 연결되었습니다: " + address);
    }

    public void setChatWindowPanel(ChatWindowPanel chatWindowPanel) {
        this.chatWindowPanel = chatWindowPanel;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void startClient() {
        Thread thread = new Thread(() -> {
            try {
                socket = new Socket(); // 소켓 생성
                socket.connect(new InetSocketAddress("localhost", 5000)); // 연결 요청
                System.out.println("연결 요청");
            } catch (IOException e) {
                System.out.println("서버 통신 안됨");
                e.printStackTrace();
            }
            receive();
        });

        thread.start();
    }

    public void stopClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 서버에서 보낸 데이터를 받는 역할
    public String receive() {
        byte[] recvBuffer = new byte[1024];
        try {
            InputStream inputStream = socket.getInputStream();
            int readByteCount = inputStream.read(recvBuffer);
            if (readByteCount == -1) {
                throw new IOException();
            }
            return new String(recvBuffer, 0, readByteCount, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 서버로 메시지를 보내는 역할
    public void send(String sender, String receiver, String message) {
        new Thread(() -> {
            String formattedMessage = sender + ":" + receiver + ":" + message;
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(formattedMessage.getBytes("UTF-8"));
                outputStream.flush();
                System.out.println("서버로 메시지 전송 완료!");
            } catch (IOException e) {
                System.out.println("서버로 메시지 전송 실패");
                e.printStackTrace();
            }
        }).start();
    }
    
    // 서버 소켓을 특정 포트에 바인딩
    public void bind(InetSocketAddress address) throws IOException {
        if (serverSocket == null) {
            serverSocket = new java.net.ServerSocket();
        }
        serverSocket.bind(address);
        System.out.println("서버가 " + address.getPort() + " 포트에 바인딩되었습니다.");
    }

    // 클라이언트 연결을 수락
    public Socket accept() throws IOException {
        if (serverSocket != null) {
            clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결 수락: " + clientSocket.getRemoteSocketAddress());
            return clientSocket;
        } else {
            throw new IOException("서버 소켓이 바인딩되지 않았습니다.");
        }
    }

    // 서버 소켓이 닫혀 있는지 확인
    public boolean isClosed() {
        return serverSocket == null || serverSocket.isClosed();
    }

    // 서버 소켓을 닫음
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("서버 소켓이 닫혔습니다.");
        }
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
            System.out.println("클라이언트 소켓이 닫혔습니다.");
        }
    }
}
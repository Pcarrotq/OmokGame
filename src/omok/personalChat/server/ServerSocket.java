package omok.personalChat.server;

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
    public void receive() {
        while (true) {
            byte[] recvBuffer = new byte[1024];
            try {
                InputStream inputStream = socket.getInputStream();
                int readByteCount = inputStream.read(recvBuffer);
                if (readByteCount == -1) {
                    throw new IOException();
                }
                // 받은 데이터를 문자열로 변환하고 분해하여 사용
                String receivedMessage = new String(recvBuffer, 0, readByteCount, "UTF-8");
                String[] parts = receivedMessage.split(":", 3);
                if (parts.length == 3) {
                    String sender = parts[0];
                    String receiver = parts[1];
                    String message = parts[2];
                    boolean isUserMessage = sender.equals(userName);
                    if (chatWindowPanel != null) {
                        chatWindowPanel.displayComment(message, isUserMessage); // 메시지 표시
                    }
                } else {
                    System.out.println("잘못된 메시지 형식: " + receivedMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    // 서버로 메시지를 보내는 역할
    public void send(String sender, String receiver, String message) {
        Thread thread = new Thread(() -> {
            // 메시지를 문자열 형식으로 변환하여 전송
            String formattedMessage = sender + ":" + receiver + ":" + message;
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(formattedMessage.getBytes("UTF-8"));
                outputStream.flush();
                System.out.println("서버로 보내기 완료!");
            } catch (IOException e) {
                System.out.println("서버로 통신 안됨");
                e.printStackTrace();
            }
        });

        thread.start();
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
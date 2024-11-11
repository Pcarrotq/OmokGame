package test.personalChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import test.personalChat.server.ServerSocket;

public class Controller {
    private static Controller instance;
    private ServerSocket serverSocket;
    public String username;

    private Controller() {
        serverSocket = new ServerSocket();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    // ServerSocket을 특정 주소에 바인딩
    public void bindServerSocket(InetSocketAddress address) throws IOException {
        serverSocket.bind(address);
        System.out.println("서버 소켓이 " + address + "에 바인딩되었습니다.");
    }

    // 클라이언트 연결 수락
    public void acceptClient() throws IOException {
        serverSocket.accept();
        System.out.println("클라이언트 연결 수락됨.");
    }

    // 서버 소켓이 닫혀 있는지 확인
    public boolean isServerSocketClosed() {
        return serverSocket.isClosed();
    }

    // 서버 소켓을 닫기
    public void closeServerSocket() throws IOException {
        serverSocket.close();
        System.out.println("서버 소켓이 닫혔습니다.");
    }

    // 메시지를 서버로 전송
    public void sendMessage(Message message) throws IOException {
        serverSocket.send(message.getSendUserName(), message.getReceiveFriendName(), message.getSendComment());
    }
}
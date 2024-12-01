package omok.server;

import omok.game.board.server.GameServer;
import omok.game.lobby.server.LobbyServer;
import omok.chat.server.ChatServer;

public class ServerMain {
    public static void main(String[] args) {
        // 로비 서버 시작
        Thread lobbyServerThread = new Thread(() -> {
            LobbyServer lobbyServer = new LobbyServer();
            lobbyServer.giveAndTake();
        });

        // 게임 서버 시작
        Thread gameServerThread = new Thread(() -> {
            GameServer gameServer = new GameServer();
            gameServer.start();
        });

        // 채팅 서버 시작
        Thread chatServerThread = new Thread(() -> {
            ChatServer chatServer = new ChatServer();
            chatServer.giveAndTake();
        });

        // 스레드 실행
        lobbyServerThread.start();
        gameServerThread.start();
        chatServerThread.start();
    }
}
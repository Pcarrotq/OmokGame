package omok.server;

import omok.game.board.server.GameServer;
import omok.game.lobby.server.LobbyServer;

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

        // 스레드 실행
        lobbyServerThread.start();
        gameServerThread.start();
    }
}
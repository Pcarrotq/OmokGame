package test.chat.controller;

import test.chat.client.ClientSocket;

public class Controller {
	private static Controller singleton = new Controller();
	public String username = null;
	public ClientSocket clientSocket;

	private Controller() {
        clientSocket = new ClientSocket(); // ClientSocket 초기화
        clientSocket.startClient(); // 소켓 연결 시작
	}

	public static Controller getInstance() {
		return singleton;
	}
}
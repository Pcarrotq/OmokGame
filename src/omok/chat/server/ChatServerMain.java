package omok.chat.server;

public class ChatServerMain {
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.giveAndTake();
	}
}
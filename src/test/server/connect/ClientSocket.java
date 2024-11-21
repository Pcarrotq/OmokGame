package test.server.connect;

import java.io.*;
import java.net.*;

public class ClientSocket {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    public String readMessage() throws IOException {
        return input.readLine();
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void close() throws IOException {
        socket.close();
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("클라이언트 소켓 닫힘");
            }
        } finally {
            super.finalize();
        }
    }

	public void send(Message message) {
		// TODO Auto-generated method stub
		
	}
}
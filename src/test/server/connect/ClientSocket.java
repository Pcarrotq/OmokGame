package test.server.connect;

import java.io.*;
import java.net.*;

public class ClientSocket {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

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

    public void close() throws IOException {
        socket.close();
    }
}
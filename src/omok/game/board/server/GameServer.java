package omok.game.board.server;

import java.io.*;
import java.net.*;
import java.util.*;
import omok.game.board.frame.Map;

public class GameServer {
    private static final int PORT = 8080;
    private Map map = new Map();
    private boolean blackTurn = true;
    private List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean placeStone(int x, int y) {
        if (map.getXY(y, x) == 0) {
            map.setMap(y, x);
            blackTurn = !blackTurn;
            return true;
        }
        return false;
    }

    private synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(" ");
                    if (parts[0].equals("PLACE")) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);

                        if (placeStone(x, y)) {
                            broadcast("UPDATE " + x + " " + y + " " + (blackTurn ? "WHITE" : "BLACK"));
                        } else {
                            out.println("INVALID");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
package test.server;

import test.server.connect.ServerGUI;

public class ServerLauncher {
    public static void main(String[] args) {
        System.out.println("Starting Server GUI...");
        try {
            new ServerGUI();
            System.out.println("Server GUI started successfully.");
        } catch (Exception e) {
            System.err.println("Error starting Server GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
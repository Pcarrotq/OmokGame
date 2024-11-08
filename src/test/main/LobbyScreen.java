package test.main;

import javax.swing.*;

import test.game.GUI;
import test.retouch.ui.CharacterSelectionScreen;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class LobbyScreen extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JList<String> roomList;
    private DefaultListModel<String> roomListModel;
    private JTextArea chatArea;
    private JTextField chatInput;

    public LobbyScreen(Socket socket, GameStartScreen mainScreen) {
        this.socket = socket;

        setTitle("Game Lobby");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);

        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.addActionListener(e -> promptForRoomName());

        JButton joinRoomButton = new JButton("방 입장");
        joinRoomButton.addActionListener(e -> joinSelectedRoom());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createRoomButton);
        buttonPanel.add(joinRoomButton);

        add(new JScrollPane(roomList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 채팅 UI 추가
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatInput = new JTextField();
        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(e -> sendMessage(chatInput.getText()));

        chatInput.addActionListener(e -> sendMessage(chatInput.getText()));
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.EAST);

        connectToServer();
    }

    private void connectToServer() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::listenForMessages).start();
            out.println("GET_ROOMS"); // 초기 방 목록 요청
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버에 연결할 수 없습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void promptForRoomName() {
        String roomName = JOptionPane.showInputDialog(this, "방 이름을 입력하세요:", "방 만들기", JOptionPane.PLAIN_MESSAGE);
        if (roomName != null && !roomName.trim().isEmpty()) {
            out.println("CREATE_ROOM " + roomName.trim());
        } else {
            JOptionPane.showMessageDialog(this, "방 이름을 입력해 주세요.");
        }
    }

    private void joinSelectedRoom() {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom != null) {
            out.println("JOIN_ROOM " + selectedRoom);
            openCharacterSelection(); // 캐릭터 선택 창으로 전환
        } else {
            JOptionPane.showMessageDialog(this, "방을 선택해주세요.");
        }
    }

    private void openCharacterSelection() {
        SwingUtilities.invokeLater(() -> {
            CharacterSelectionScreen characterSelection = new CharacterSelectionScreen(selectedCharacter -> {
                out.println("SET_CHARACTER " + selectedCharacter); // 서버에 캐릭터 전송
                startGameWithCharacter(selectedCharacter); // 캐릭터 선택 후 게임 시작
            });
            characterSelection.setVisible(true);
        });
    }

    private void startGameWithCharacter(String selectedCharacter) {
        GUI gameGui = new GUI("오목 게임");
        gameGui.setPreferredSize(new Dimension(1500, 1000));
        gameGui.setPlayer1Profile(selectedCharacter);

        // 상대방의 캐릭터, 닉네임, 돌 놓기 등의 메시지를 수신
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("OPPONENT_PROFILE")) {
                        String opponentProfile = message.split(" ", 2)[1];
                        SwingUtilities.invokeLater(() -> gameGui.setPlayer2Profile(opponentProfile));
                    } else if (message.startsWith("OPPONENT_NICKNAME")) {
                        String opponentNickname = message.split(" ", 2)[1];
                        SwingUtilities.invokeLater(() -> gameGui.setOpponentNickname(opponentNickname));
                    } else if (message.startsWith("CHAT")) {
                        String chatMessage = message.substring(5);
                        handleChatMessage(chatMessage);
                    } else if (message.startsWith("MOVE")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        SwingUtilities.invokeLater(() -> gameGui.placeOpponentStone(x, y));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        dispose(); // 로비 창 닫기

        JFrame gameFrame = new JFrame("Omok Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(gameGui);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("ROOM_LIST")) {
                    updateRoomList(message);
                } else if (message.startsWith("START_GAME")) {
                    openCharacterSelection();
                } else if (message.startsWith("CHAT")) {
                    String chatMessage = message.substring(5);
                    handleChatMessage(chatMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버와의 연결이 끊어졌습니다.", "연결 끊김", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void sendMessage(String message) {
        if (!message.trim().isEmpty()) {
            out.println("CHAT " + message);
            chatInput.setText(""); // 입력 필드 초기화
        }
    }

    private void handleChatMessage(String message) {
        chatArea.append(message + "\n"); // 채팅 창에 메시지 추가
    }

    private void updateRoomList(String message) {
        roomListModel.clear();
        String[] rooms = message.split(" ");
        for (int i = 1; i < rooms.length; i++) {
            roomListModel.addElement(rooms[i]);
        }
    }
}
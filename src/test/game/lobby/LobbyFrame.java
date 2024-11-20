package test.game.lobby;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import test.game.gui.GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LobbyFrame extends JFrame implements Runnable {
    private JTextArea chatArea;
    private JTextField chatInputField;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JList<String> gameRoomList;
    private DefaultListModel<String> roomListModel;

    public LobbyFrame(String ip, int port) {
        setTitle("Lobby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10)); // 컴포넌트 간 10px 간격 설정

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)); // 내부 컴포넌트 간 간격
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 프레임과 컴포넌트 간 간격
        add(mainPanel);

        // **중앙 패널: 게임 방 리스트**
        JPanel centerPanel = new JPanel(new BorderLayout());
        roomListModel = new DefaultListModel<>();
        gameRoomList = new JList<>(roomListModel);
        JScrollPane gameRoomScrollPane = new JScrollPane(gameRoomList);
        centerPanel.add(gameRoomScrollPane, BorderLayout.CENTER);
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // 내부 여백
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // **오른쪽 패널: 버튼과 친구 목록**
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5)); // 버튼과 목록 간 간격
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 패널 자체 여백

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // 버튼 간 간격
        JButton createGameButton = new JButton("게임 생성 버튼");
        createGameButton.addActionListener(e -> createGame());
        JButton joinGameButton = new JButton("게임 입장 버튼");
        buttonPanel.add(createGameButton);
        buttonPanel.add(joinGameButton);
        rightPanel.add(buttonPanel, BorderLayout.NORTH);

        JList<String> friendList = new JList<>(new String[]{"Friend 1", "Friend 2", "Friend 3"});
        JScrollPane friendScrollPane = new JScrollPane(friendList);
        rightPanel.add(friendScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // **하단 패널: 채팅 영역**
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5)); // 채팅창과 입력창 간 간격
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 내부 여백

        // 채팅 출력 창
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(800, 200)); // 높이 설정
        bottomPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 채팅 입력 영역
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // 입력창과 출력창 간 간격
        chatInputField = new JTextField();
        JButton sendButton = new JButton("전송");
        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(chatInputPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // **통신 초기화**
        initNet(ip, port);

        // **이벤트 설정**
        sendButton.addActionListener(e -> sendMessage());
        chatInputField.addActionListener(e -> sendMessage());
        createGameButton.addActionListener(e -> createRoom());
        joinGameButton.addActionListener(e -> joinRoom());

        setVisible(true);

        // 쓰레드 실행
        new Thread(this).start();
    }

    private void initNet(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버에 연결할 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void createGame() {
        String roomName = JOptionPane.showInputDialog(this, "방 이름을 입력하세요:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                JFrame gameFrame = new JFrame("오목 게임"); // 새로운 JFrame 생성
                GUI gameGui = new GUI("방 이름: " + roomName); // GUI 생성
                
                gameGui.setPlayer1Profile("Your Character"); // 기본 캐릭터 설정 예제
                
                gameFrame.add(gameGui); // GUI 추가
                gameFrame.setSize(1500, 1000); // JFrame 크기 설정
                gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                gameFrame.setTitle("방 이름: " + roomName); // 제목 설정
                gameFrame.setVisible(true); // JFrame 보이기
            });
        }
    }

    private void sendMessage() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty()) {
            out.println("/chat " + message); // "/chat" 명령어 추가
            chatInputField.setText("");
        }
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "방 이름을 입력하세요:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            out.println("/create_room " + roomName.trim());
        }
    }

    private void joinRoom() {
        String selectedRoom = gameRoomList.getSelectedValue();
        if (selectedRoom != null) {
            out.println("/join_room " + selectedRoom);
        } else {
            JOptionPane.showMessageDialog(this, "입장할 방을 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/room ")) {
                    updateRoomList(message.substring(6));
                } else {
                    chatArea.append(message + "\n");
                }
            }
        } catch (IOException e) {
            chatArea.append("서버 연결이 종료되었습니다.\n");
        }
    }

    private void updateRoomList(String roomList) {
        SwingUtilities.invokeLater(() -> {
            roomListModel.clear();
            for (String room : roomList.split(",")) {
                roomListModel.addElement(room.trim());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LobbyFrame("127.0.0.1", 8080));
    }
}

package test.game.lobby;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyFrame extends JFrame {

    public LobbyFrame() {
        setTitle("Game Room UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10)); // 컴포넌트 간 10px 간격 설정

        // 프레임 전체 테두리 간격 설정
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)); // 내부 컴포넌트 간 간격
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 프레임과 컴포넌트 간 간격
        add(mainPanel);

        // 중앙 패널: 게임 방 리스트
        JPanel centerPanel = new JPanel(new BorderLayout());
        JList<String> gameRoomList = new JList<>(new String[]{"Room 1", "Room 2", "Room 3"});
        JScrollPane gameRoomScrollPane = new JScrollPane(gameRoomList);
        centerPanel.add(gameRoomScrollPane, BorderLayout.CENTER);
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // 내부 여백
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 오른쪽 패널: 버튼과 친구 목록
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5)); // 버튼과 목록 간 간격
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 패널 자체 여백

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // 버튼 간 간격
        JButton createGameButton = new JButton("게임 생성 버튼");
        JButton joinGameButton = new JButton("게임 입장 버튼");
        buttonPanel.add(createGameButton);
        buttonPanel.add(joinGameButton);
        rightPanel.add(buttonPanel, BorderLayout.NORTH);

        JList<String> friendList = new JList<>(new String[]{"Friend 1", "Friend 2", "Friend 3"});
        JScrollPane friendScrollPane = new JScrollPane(friendList);
        rightPanel.add(friendScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // 하단 패널: 채팅 영역
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5)); // 채팅창과 입력창 간 간격
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 내부 여백

        // 채팅 출력 창
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(800, 200)); // 높이 설정
        bottomPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 채팅 입력 영역
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // 입력창과 출력창 간 간격
        JTextField chatInputField = new JTextField();
        JButton sendButton = new JButton("전송");
        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(chatInputPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 버튼 이벤트
        createGameButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "게임 생성 버튼 클릭됨"));
        joinGameButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "게임 입장 버튼 클릭됨"));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = chatInputField.getText().trim();
                if (!message.isEmpty()) {
                    chatArea.append("나: " + message + "\n");
                    chatInputField.setText("");
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LobbyFrame::new);
    }
}
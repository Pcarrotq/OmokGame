package test.personalChat.frame;

import javax.swing.*;
import javax.swing.text.*;

import test.personalChat.enums.AlignEnum;
import test.personalChat.server.ServerSocket;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatWindowPanel extends JPanel {
    private String panelName;
    private JTextArea textArea;
    private JButton sendButton;
    private JButton imgFileButton;
    private JTextPane jtp;
    private StyledDocument document;
    private static String userName;
    private ServerSocket serverSocket;

    public ChatWindowPanel(Icon icon, String friendName) {
        userName = "User"; // 기본 사용자 이름 설정
        panelName = friendName;

        // 서버와 연결 설정
        serverSocket = new ServerSocket();
        try {
            serverSocket.connect(new InetSocketAddress("localhost", 5000)); // 서버에 연결
            System.out.println("서버와 연결됨");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(null);
        showFriendInfo(icon, friendName); // 친구 정보 표시
        writeMessageArea(); // 메시지 입력 영역 설정
        showContentArea(); // 메시지 출력 영역 설정

        imgFileButton = showImgFileButton();
        add(imgFileButton);

        sendButton = showSendButton();
        add(sendButton);

        // 전송 버튼 이벤트 리스너
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textArea.getText();
                if (!message.isEmpty()) {
                    // 메시지를 서버로 전송
                    serverSocket.send(userName, panelName, message);
                    displayComment(message, true); // 본인 메시지로 표시
                    textArea.setText(""); // 입력 필드 초기화
                }
            }
        });

        // 서버에서 메시지를 수신
        receiveMessages();
    }

    private void showFriendInfo(Icon icon, String friendName) {
        JLabel friendInfolabel = new JLabel(icon);
        friendInfolabel.setOpaque(true);
        friendInfolabel.setText(friendName);
        friendInfolabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        friendInfolabel.setBounds(0, 0, 400, 80);
        friendInfolabel.setBackground(Color.WHITE);
        add(friendInfolabel);
    }

    private JButton showImgFileButton() {
        JButton imgFileButton = new JButton();
        imgFileButton.setBackground(Color.LIGHT_GRAY);
        imgFileButton.setFocusPainted(false);
        imgFileButton.setBounds(0, 460, 60, 40);
        return imgFileButton;
    }

    private JButton showSendButton() {
        JButton sendButton = new JButton("전송");
        sendButton.setBackground(Color.GRAY);
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        sendButton.setFocusPainted(false);
        sendButton.setBounds(320, 500, 68, 65);
        return sendButton;
    }

    private void writeMessageArea() {
        textArea = new JTextArea(3, 20);
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setBounds(0, 500, 321, 65);
        add(scroller);
    }

    private void showContentArea() {
        document = new DefaultStyledDocument();
        jtp = new JTextPane(document);
        jtp.setEditable(false);
        JScrollPane scroller2 = new JScrollPane(jtp);
        scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller2.setBounds(0, 80, 389, 380);
        add(scroller2);
    }

    public void displayComment(String message, boolean isUserMessage) {
        System.out.println("Displaying message: " + message); // 디버그 출력
        AlignEnum align = isUserMessage ? AlignEnum.RIGHT : AlignEnum.LEFT;
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("aHH:mm"));
        String userDisplayName = isUserMessage ? userName : panelName;

        textPrint(timestamp + "  <" + userDisplayName + ">", align);
        textPrint(message, align);
    }

    private void textPrint(String string, AlignEnum alignEnum) {
        try {
            SimpleAttributeSet alignment = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignment, alignEnum == AlignEnum.RIGHT ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

            document.setParagraphAttributes(document.getLength(), document.getLength() + 1, alignment, true);
            document.insertString(document.getLength(), string + "\n", alignment);
            System.out.println("Message printed: " + string); // 디버그 출력
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // 서버에서 메시지를 수신하고 화면에 표시
    private void receiveMessages() {
        new Thread(() -> {
            while (true) {
                String receivedMessage = serverSocket.receive();
                System.out.println("Received message: " + receivedMessage); // 디버그 출력
                if (receivedMessage != null && !receivedMessage.isEmpty()) {
                    String[] parts = receivedMessage.split(":");
                    if (parts.length == 3) { // 메시지 형식이 올바른지 확인
                        String sender = parts[0];
                        String receiver = parts[1];
                        String messageContent = parts[2];

                        // 메시지가 자신에게 온 것인지 확인
                        if (receiver.equals(userName)) {
                            displayComment(messageContent, false);
                        }
                    } else {
                        System.out.println("Invalid message format: " + receivedMessage);
                    }
                }
            }
        }).start();
    }
}
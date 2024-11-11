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
    private JTextPane jtp;
    private StyledDocument document;
    private static String userName;
    private ServerSocket serverSocket; // 서버 소켓 객체 추가

    public ChatWindowPanel(Icon icon, String friendName, ServerSocket serverSocket) {
        userName = "User";
        panelName = friendName;
        this.serverSocket = serverSocket; // 서버 소켓 객체 초기화

        setLayout(null);
        showFriendInfo(icon, friendName); // 친구 정보 표시
        writeMessageArea(); // 메시지 입력 영역 설정
        showContentArea(); // 메시지 출력 영역 설정

        sendButton = showSendButton();
        add(sendButton);

        // Send button action listener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textArea.getText().trim();
                if (!message.isEmpty()) {
                    displayComment(message, true);
                    textArea.setText(""); // 텍스트 입력창 초기화
                    
                    // 서버로 메시지 전송
                    serverSocket.send(userName, panelName, message);
                }
            }
        });
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
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
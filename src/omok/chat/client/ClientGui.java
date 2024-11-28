package omok.chat.client;

import test.chat.enums.AlignEnum; // AlignEnum 임포트

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientGui extends JFrame {
    private JTextArea textArea;
    private JButton sendButton;
    private JButton fileButton;
    private JTextPane jtp;
    private StyledDocument document;
    private String username;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientGui(String ip, int port, String username) {
        this.username = username;

        setTitle(username + "의 Chatting");
        setBounds(1200, 250, 405, 605);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        // UI 초기화
        initUI();

        // 네트워크 초기화
        initNetwork(ip, port);

        // 메시지 수신 스레드 시작
        startListening();

        setVisible(true);
    }

    private void initUI() {
        setBackground(Color.LIGHT_GRAY);
        showFriendInfo("Server");
        writeMessageArea();
        showContentArea();

        fileButton = showFileButton();
        add(fileButton);

        sendButton = showSendButton();
        add(sendButton);

        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                textArea.setText(file.toString());
            }
        });

        sendButton.addActionListener(e -> sendMessage());
    }

    private void initNetwork(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(username); // 서버에 사용자 이름 전송
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // 메시지 타입 확인
                    if (message.startsWith("[Server][System]")) {
                        // 시스템 메시지 처리
                        int start = message.indexOf("]") + 2; // "[Server][System]" 다음의 시작 위치
                        String systemMessage = message.substring(start);
                        displayMessage("System", systemMessage, AlignEnum.LEFT);
                    } else if (message.startsWith("[Server][Message]")) {
                        // 클라이언트 메시지 처리
                        int senderStart = message.indexOf("[", 15) + 1; // 송신자 이름의 시작 위치
                        int senderEnd = message.indexOf("]", senderStart); // 송신자 이름의 끝 위치
                        if (senderStart < 0 || senderEnd < 0) {
                            continue; // 메시지 포맷이 잘못된 경우 무시
                        }

                        String sender = message.substring(senderStart, senderEnd);
                        String msgContent = message.substring(message.indexOf("] ", senderEnd) + 2); // 메시지 내용

                        // 본인의 메시지는 처리하지 않고, 다른 사용자의 메시지만 출력
                        if (!sender.equals(username)) {
                            displayMessage(sender, msgContent, AlignEnum.LEFT);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showFriendInfo(String friendName) {
        JLabel friendInfoLabel = new JLabel(friendName);
        friendInfoLabel.setOpaque(true);
        friendInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        friendInfoLabel.setBounds(0, 0, 400, 80);
        friendInfoLabel.setBackground(Color.WHITE);
        add(friendInfoLabel);
    }

    private JButton showFileButton() {
        JButton fileButton = new JButton("파일");
        fileButton.setBounds(0, 460, 60, 40);
        fileButton.setFocusPainted(false);
        return fileButton;
    }

    private JButton showSendButton() {
        JButton sendButton = new JButton("전송");
        sendButton.setBounds(320, 500, 68, 65);
        sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        sendButton.setFocusPainted(false);
        return sendButton;
    }

    private void writeMessageArea() {
        textArea = new JTextArea(20, 20);
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBounds(0, 500, 321, 65);
        add(scroller);

        // 엔터키 동작 정의
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        // Shift + Enter: 줄 바꿈 추가
                        textArea.append("\n");
                    } else {
                        // Enter: 메시지 전송
                        e.consume(); // 기본 Enter 동작 방지
                        sendMessage();
                    }
                }
            }
        });
    }
    
    private void sendMessage() {
        String message = textArea.getText().trim(); // 텍스트의 앞뒤 공백 및 줄 바꿈 제거
        if (!message.isEmpty()) {
            message = message.replace("\n", " "); // 줄 바꿈 문자를 공백으로 변환
            out.println(message); // 서버로 메시지 전송
            textArea.setText(""); // 텍스트 필드 초기화
            displayMessage(username, message, AlignEnum.RIGHT); // 본인 메시지를 우측 정렬로 출력
        }
    }

    private void showContentArea() {
        StyleContext context = new StyleContext();
        document = new DefaultStyledDocument(context);
        jtp = new JTextPane(document);
        jtp.setEditable(false);
        JScrollPane scroller = new JScrollPane(jtp);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBounds(0, 80, 389, 380);
        add(scroller);
    }

    private void displayMessage(String sender, String message, AlignEnum align) {
        try {
            document = jtp.getStyledDocument();
            SimpleAttributeSet alignAttr = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignAttr, align == AlignEnum.RIGHT ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            document.setParagraphAttributes(document.getLength(), document.getLength() + 1, alignAttr, false);
            document.insertString(document.getLength(), "[" + sender + "] " + message + "\n", alignAttr);
            if (sender.equals("System")) {
                StyleConstants.setForeground(alignAttr, Color.GRAY); // 시스템 메시지를 회색으로 표시
            } else {
                StyleConstants.setForeground(alignAttr, Color.BLACK); // 기본 메시지는 검정색
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
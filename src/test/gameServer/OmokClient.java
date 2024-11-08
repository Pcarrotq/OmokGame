package test.gameServer;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class OmokClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextArea chatArea;
    private JTextField inputField;
    private JLabel opponentProfileLabel;
    private JLabel opponentLabel;
    private String playerName;
    private boolean isMyTurn;

    // 바둑판 크기 및 상태 관리
    private static final int BOARD_SIZE = 15;
    private JPanel boardPanel;
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

    public OmokClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            setupGUI();
            new Thread(this::listenForMessages).start();
            log("서버에 연결되었습니다.");
        } catch (IOException e) {
            showError("서버에 연결할 수 없습니다.");
        }
    }

    private void setupGUI() {
        frame = new JFrame("Omok Client");

        // 메시지 영역
        messageArea = new JTextArea(10, 40);
        messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.NORTH);

        // 채팅 영역
        chatArea = new JTextArea(10, 40);
        chatArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.WEST);

        // 상대방 프로필 및 닉네임
        opponentProfileLabel = new JLabel("Opponent: ");
        opponentLabel = new JLabel("Nickname: ");
        JPanel opponentPanel = new JPanel();
        opponentPanel.add(opponentProfileLabel);
        opponentPanel.add(opponentLabel);
        frame.getContentPane().add(opponentPanel, BorderLayout.EAST);

        // 메시지 입력 및 전송 버튼
        inputField = new JTextField(40);
        inputField.addActionListener(e -> sendMessage(inputField.getText()));
        frame.getContentPane().add(inputField, BorderLayout.SOUTH);

        // 바둑판 설정
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(600, 600));
        frame.getContentPane().add(boardPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void drawBoard(Graphics g) {
        int cellSize = boardPanel.getWidth() / BOARD_SIZE;

        // 그리드 그리기
        for (int i = 0; i < BOARD_SIZE; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, boardPanel.getHeight());
            g.drawLine(0, i * cellSize, boardPanel.getWidth(), i * cellSize);
        }

        // 돌 그리기
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (board[x][y] == 1) {
                    g.setColor(Color.BLACK);  // 내 돌 색상
                } else if (board[x][y] == 2) {
                    g.setColor(Color.WHITE);  // 상대방 돌 색상
                } else {
                    continue;
                }
                g.fillOval(x * cellSize + cellSize / 4, y * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
            }
        }
    }

    private void sendMessage(String message) {
        if (message.trim().isEmpty()) {
            showError("메시지를 입력하세요.");
            return;
        }
        if (message.startsWith("/stone") && isMyTurn) {
            out.println("PLACE_STONE " + message.substring(7).trim());
            isMyTurn = false; // 턴 종료
            log("You placed a stone.");
        } else if (message.startsWith("/nick")) {
            playerName = message.substring(6).trim();
            setNickname(playerName);
        } else {
            out.println("CHAT " + message);
            chatArea.append("Me: " + message + "\n");
            log("Me: " + message);
        }
        inputField.setText("");
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processServerMessage(message);
            }
        } catch (IOException e) {
            showError("서버와의 연결이 끊어졌습니다.");
        } finally {
            closeResources();
        }
    }

    private void processServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("CHAT")) {
                handleChatMessage(message.substring(5));
            } else if (message.startsWith("OPPONENT_PROFILE")) {
                handleOpponentProfile(message.substring(16));
            } else if (message.startsWith("OPPONENT_NICKNAME")) {
                handleOpponentNickname(message.substring(17));
            } else if (message.startsWith("MOVE")) {
                String[] parts = message.split(" ");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                handleOpponentMove(x, y);
            } else if (message.startsWith("YOUR_TURN")) {
                isMyTurn = true;
                messageArea.append("Your turn\n");
            } else if (message.startsWith("NOT_YOUR_TURN")) {
                isMyTurn = false;
                messageArea.append("Opponent's turn\n");
            } else if (message.startsWith("YOUR_NICKNAME")) {
                handleYourNickname(message.substring(14));
            }
        });
    }

    private void handleYourNickname(String nickname) {
        playerName = nickname;
        opponentLabel.setText("Nickname: " + playerName);
    }

    private void handleChatMessage(String message) {
        chatArea.append("Opponent: " + message + "\n");
    }

    private void handleOpponentProfile(String profileInfo) {
        opponentProfileLabel.setText("Opponent: " + profileInfo);
    }

    private void handleOpponentNickname(String nickname) {
        opponentLabel.setText("Nickname: " + nickname);
    }

    private void handleOpponentMove(int x, int y) {
        board[x][y] = 2; // 상대방의 돌을 백돌로 표시
        boardPanel.repaint(); // 바둑판 업데이트
        messageArea.append("Opponent placed a stone at (" + x + ", " + y + ")\n");
    }

    private void setNickname(String nickname) {
        out.println("SET_NICKNAME " + nickname);
        messageArea.append("Your nickname is set to " + nickname + "\n");
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String error) {
        JOptionPane.showMessageDialog(frame, error);
    }

    private void log(String message) {
        messageArea.append(message + "\n");
    }

    public static void main(String[] args) {
        String serverAddress = "localhost"; // 서버 주소
        int port = 12345; // 서버 포트
        new OmokClient(serverAddress, port);
    }
}
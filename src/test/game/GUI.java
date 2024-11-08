package test.game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;

import test.main.GameStartScreen;
import test.retouch.ui.EmojiMap;

@SuppressWarnings("serial")
public class GUI extends JPanel {
    private Container c;
    private Map map;
    private DrawBoard d;
    private JTextPane txtDisplay;
    private JPanel pSouth;
    private JTextField txtInput;
    private JButton btnSend;
    private JButton btnExit;  
    private JButton btnEmoji; 
    private JLabel player1Profile;  
    private JLabel player2Profile;  
    private JLabel player1Label;    
    private JLabel player2Label;    
    private JLabel turnDisplay;  
    private EmojiMap emojiMap;
    
    // 네트워크 소켓 통신을 위한 필드
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private boolean isMyTurn = true; // 초기 설정

    public GUI(String title) {
        setLayout(new BorderLayout());
        setSize(1500, 1000);

        map = new Map();
        d = new DrawBoard(map);
        add(d, BorderLayout.CENTER);

        // 우측 패널 설정
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 1000));
        add(rightPanel, BorderLayout.EAST);

        turnDisplay = new JLabel("흑돌의 차례입니다.", JLabel.CENTER);
        rightPanel.add(turnDisplay, BorderLayout.NORTH);

        btnExit = new JButton("나가기");
        btnExit.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(GUI.this);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
        });
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.add(btnExit);
        rightPanel.add(exitPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel profilePanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Label = new JLabel("You", JLabel.CENTER);
        player1Label.setFont(new Font("Serif", Font.BOLD, 16));
        player1Profile = new JLabel();
        player1Profile.setHorizontalAlignment(JLabel.CENTER);
        player1Profile.setVerticalAlignment(JLabel.CENTER);
        player1Profile.setPreferredSize(new Dimension(150, 150));
        player1Panel.add(player1Label, BorderLayout.NORTH);
        player1Panel.add(player1Profile, BorderLayout.CENTER);
        profilePanel.add(player1Panel);

        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Label = new JLabel("Opponent", JLabel.CENTER);
        player2Label.setFont(new Font("Serif", Font.BOLD, 16));
        player2Profile = new JLabel();
        player2Profile.setHorizontalAlignment(JLabel.CENTER);
        player2Profile.setVerticalAlignment(JLabel.CENTER);
        player2Profile.setPreferredSize(new Dimension(150, 150));
        player2Panel.add(player2Label, BorderLayout.NORTH);
        player2Panel.add(player2Profile, BorderLayout.CENTER);
        profilePanel.add(player2Panel);

        bottomPanel.add(profilePanel, BorderLayout.NORTH);
        emojiMap = new EmojiMap();

        btnEmoji = new JButton("+");
        btnEmoji.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEmojiPicker();
            }
        });

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(300, 300));
        chatPanel.add(new JScrollPane(getTxtDisplay()), BorderLayout.CENTER);
        chatPanel.add(getPSouth(), BorderLayout.SOUTH);
        bottomPanel.add(chatPanel, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        connectToServer();

        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage("CHAT " + txtInput.getText());
                txtInput.setText("");
            }
        });

        d.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (!isMyTurn) {
                    JOptionPane.showMessageDialog(null, "상대방의 차례입니다.");
                    return;
                }

                int boardSize = Math.min(d.getWidth(), d.getHeight()) - 20;
                int cellSize = boardSize / map.getSize();

                int x = (arg0.getX() - (d.getWidth() - boardSize) / 2) / cellSize;
                int y = (arg0.getY() - (d.getHeight() - boardSize) / 2) / cellSize;

                if (x < 0 || x >= map.getSize() || y < 0 || y >= map.getSize() || 
                    map.getXY(y, x) == map.getBlack() || map.getXY(y, x) == map.getWhite()) {
                    return;
                }

                sendMessage("MOVE " + x + " " + y); // 서버로 돌 위치 전송
            }
        });
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        processServerMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeResources();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (out != null && !message.trim().isEmpty()) {
            out.println(message); // 서버로 메시지 전송
        }
    }

    private void processServerMessage(String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "MOVE":
                handleOpponentMove(content); // 상대방의 돌을 화면에 표시
                isMyTurn = true;
                turnDisplay.setText("흑돌의 차례입니다."); // 자신의 차례로 표시
                break;
            case "CHAT":
                appendMessage(content);
                break;
            case "CHARACTER":
                setPlayer1Profile(content);
                break;
            case "OPPONENT_CHARACTER":
                setPlayer2Profile(content);
                break;
            case "START_GAME":
                JOptionPane.showMessageDialog(this, "상대방이 입장했습니다. 게임을 시작합니다!");
                break;
            case "WAITING_FOR_OPPONENT":
                JOptionPane.showMessageDialog(this, "상대방을 기다리고 있습니다. 상대방이 입장하면 게임이 시작됩니다.");
                break;
        }
    }

    public JTextPane getTxtDisplay() {
        if (txtDisplay == null) {
            txtDisplay = new JTextPane();
            txtDisplay.setEditable(false);
            txtDisplay.setContentType("text/html");
        }
        return txtDisplay;
    }

    public JPanel getPSouth() {
        if (pSouth == null) {
            pSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            pSouth.add(getTxtInput());
            pSouth.add(btnEmoji);
            pSouth.add(getBtnSend());
        }
        return pSouth;
    }

    public JTextField getTxtInput() {
        if (txtInput == null) {
            txtInput = new JTextField();
            txtInput.setPreferredSize(new Dimension(170, 25));
            txtInput.addActionListener(e -> {
                sendMessage("CHAT " + txtInput.getText());
                txtInput.setText("");
            });
        }
        return txtInput;
    }

    public JButton getBtnSend() {
        if (btnSend == null) {
            btnSend = new JButton("전송");
            btnSend.addActionListener(e -> {
                sendMessage("CHAT " + txtInput.getText());
                txtInput.setText("");
            });
        }
        return btnSend;
    }

    private void appendMessage(String message) {
        try {
            StyledDocument doc = txtDisplay.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            doc.insertString(doc.getLength(), message + "\n", attrs);
            txtDisplay.setCaretPosition(doc.getLength()); // 스크롤 맨 아래로 이동
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmojiPicker() {
        JPopupMenu emojiMenu = new JPopupMenu();
        for (String key : emojiMap.getAllEmojis().keySet()) {
            JMenuItem item = new JMenuItem(key, emojiMap.getEmoji(key));
            item.addActionListener(e -> {
                txtInput.setText(key);
                sendMessage("CHAT " + key);
            });
            emojiMenu.add(item);
        }
        emojiMenu.show(btnEmoji, btnEmoji.getWidth(), btnEmoji.getHeight());
    }

    public void setPlayer1Profile(String characterName) {
        player1Profile.setIcon(createCharacterIcon(characterName));
        revalidate();
        repaint();
    }

    public void setPlayer2Profile(String characterName) {
        player2Profile.setIcon(createCharacterIcon(characterName));
        revalidate();
        repaint();
    }

    private ImageIcon createCharacterIcon(String characterName) {
        int size = 100;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        switch (characterName) {
            case "Character 1":
                g2d.setColor(Color.RED);
                g2d.fillOval(10, 10, size - 20, size - 20);
                break;
            case "Character 2":
                g2d.setColor(Color.BLUE);
                g2d.fillRect(10, 10, size - 20, size - 20);
                break;
            case "Character 3":
                g2d.setColor(Color.GREEN);
                g2d.fillRoundRect(10, 10, size - 20, size - 20, 30, 30);
                break;
            case "Character 4":
                g2d.setColor(Color.ORANGE);
                g2d.fillPolygon(new int[]{size / 2, 10, size - 10}, new int[]{10, size - 10, size - 10}, 3);
                break;
        }
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void placeStoneOnBoard(int x, int y, Color color) {
        int boardSize = Math.min(d.getWidth(), d.getHeight()) - 20;
        int cellSize = boardSize / map.getSize();
        Graphics g = d.getGraphics();

        // 중심점 계산
        int xPos = (d.getWidth() - boardSize) / 2 + x * cellSize + cellSize / 2;
        int yPos = (d.getHeight() - boardSize) / 2 + y * cellSize + cellSize / 2;

        g.setColor(color);
        g.fillOval(xPos - cellSize / 3, yPos - cellSize / 3, cellSize * 2 / 3, cellSize * 2 / 3);
        g.dispose();
    }
    
    private void placeStone(int x, int y) {
        if (isMyTurn) {
            out.println("PLACE_STONE " + x + " " + y); // 서버에 돌 놓기 전송
            isMyTurn = false; // 턴 전환
            placeStoneOnBoard(x, y, Color.BLACK); // 내 돌을 화면에 표시
        } else {
            JOptionPane.showMessageDialog(this, "상대방의 차례입니다.");
        }
    }

    private void handleOpponentMove(String moveInfo) {
        String[] parts = moveInfo.split(" ");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        placeStoneOnBoard(x, y, Color.WHITE); // 상대방 돌을 화면에 표시
        isMyTurn = true; // 자신의 차례로 전환
    }
    
    public void setOpponentNickname(String nickname) {
        player2Label.setText(nickname);
    }
    
    public void placeOpponentStone(int x, int y) {
        placeStoneOnBoard(x, y, Color.WHITE); // 상대방의 돌을 백돌로 설정
    }

    public void showPopUp(String message) {
        JOptionPane.showMessageDialog(this, message, "", JOptionPane.INFORMATION_MESSAGE);
    }
}
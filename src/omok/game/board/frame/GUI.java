package omok.game.board.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

import omok.additional.EmojiMap;
import omok.main.GameStartScreen;
import omok.main.function.login.Login;
import omok.member.db.DBConnection;

@SuppressWarnings("serial")
public class GUI extends JPanel {
    private Container c;
    private Map map;
    private DrawBoard d;
    private JTextPane txtDisplay;
    private JTextField txtInput;
    private JButton btnSend;
    private JButton btnExit, startButton, readyButton;
    private JLabel player1Profile, player1Label, player2Profile, player2Label;
    private JPanel player1Panel, player2Panel;
    private JLabel turnDisplay, timerLabel, elapsedTimeLabel;
    private Timer turnTimer;
    private int remainingTime = 60;
    private boolean isSpectatorMode = false;
    private String roomCreator;
    private boolean isPlayer1Ready = false; // 플레이어 1 레디 상태
    private boolean isPlayer2Ready = false; // 플레이어 2 레디 상태

    public GUI(String title) {
    	System.out.println("GUI 생성됨: " + title);
    	
        setLayout(new BorderLayout());
        setSize(1500, 1000);

        map = new Map();
        d = new DrawBoard(map);
        add(d, BorderLayout.CENTER);
        
        turnTimer = new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                timerLabel.setText("남은 시간: " + remainingTime + "초");
            } else {
                // 시간이 다 되면 턴을 강제로 넘김
                JOptionPane.showMessageDialog(this, "시간 초과! 턴이 넘어갑니다.", "시간 종료", JOptionPane.WARNING_MESSAGE);
                turnTimer.stop();
                changeTurn(); // 턴 변경
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();

        // 우측 패널 설정
        JPanel rightPanel = new JPanel(new GridBagLayout());
        add(rightPanel, BorderLayout.EAST);
        
        // Timer and Turn Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1, 5, 5));
        timerLabel = new JLabel("남은 시간: " + remainingTime + "초", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnDisplay = new JLabel("흑돌의 차례입니다.", SwingConstants.CENTER);
        turnDisplay.setFont(new Font("SansSerif", Font.BOLD, 16));
        infoPanel.add(timerLabel);
        infoPanel.add(turnDisplay);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        rightPanel.add(infoPanel, gbc);
        
        roomCreator = Login.getLoggedInUserId();
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        
        // Add 'Start' Button
        startButton = new JButton("시작");
        startButton.setEnabled(false); // Only room creator can enable
        startButton.setEnabled(Login.getLoggedInUserId().equals(roomCreator));
        startButton.addActionListener(e -> {
            if (!Login.getLoggedInUserId().equals(roomCreator)) {
                JOptionPane.showMessageDialog(this, "방장만 시작 버튼을 누를 수 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            startGame(); // 게임 시작 로직 호출
        });
        
        // Add 'Ready' Button
        readyButton = new JButton("레디");
        readyButton.addActionListener(e -> toggleReadyState());

        // Add 'Exit' Button
        btnExit = new JButton("나가기");
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT); // Align center
        btnExit.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) parentFrame.dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(btnExit);
        gbc.gridy = 1;
        gbc.weighty = 0.2;
        rightPanel.add(buttonPanel, gbc);

        // Profiles (You and Opponent)
        JPanel profilePanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1행 2열
        profilePanel.setPreferredSize(new Dimension(300, 150)); // 전체 프로필 패널 크기 고정

        player1Panel = createProfilePanel("You");
        profilePanel.add(player1Panel);
        player2Panel = createProfilePanel("Opponent");
        profilePanel.add(player2Panel);
        gbc.gridy = 2;
        gbc.weighty = 0.6; // 프로필에 충분한 공간 할당
        rightPanel.add(profilePanel, gbc);

        // 하단 영역: 채팅 및 전송 버튼
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        // 채팅 출력 창
        JPanel chatPanel = new JPanel(new BorderLayout());
        JScrollPane chatScrollPane = new JScrollPane(getTxtDisplay());
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        
        // 채팅 출력 패널 추가
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.8; // 상단 채팅 출력 창이 80% 차지
        gbc.fill = GridBagConstraints.BOTH;
        bottomPanel.add(chatPanel, gbc);

        // 채팅 입력 및 전송 버튼
        JPanel inputPanel = new JPanel(new GridBagLayout());
        txtInput = getTxtInput();
        btnSend = new JButton("전송");
        btnSend.addActionListener(e -> {
            String message = txtInput.getText().trim();
            if (!message.isEmpty()) {
                // 닉네임 가져오기
                String nickname = "닉네임"; // DB 또는 로그인 정보에서 닉네임을 가져옵니다.

                // 관전 모드에 따라 메시지 스타일 다르게 설정
                sendChatMessage(nickname, message);
                txtInput.setText("");
            }
        });
        
        // 입력 필드 레이아웃
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.9; // 입력 필드가 90% 차지
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(txtInput, gbc);

        // 전송 버튼 레이아웃
        gbc.gridx = 1;
        gbc.weightx = 0.1; // 전송 버튼이 10% 차지
        inputPanel.add(btnSend, gbc);

        // 입력 패널 추가
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0.2; // 입력 영역이 20% 차지
        gbc.fill = GridBagConstraints.BOTH;
        bottomPanel.add(inputPanel, gbc);

        // 하단 패널을 우측 패널에 추가
        gbc.gridx = 0;
        gbc.gridy = 3; // 마지막 영역
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        rightPanel.add(bottomPanel, gbc);

        d.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                int boardSize = Math.min(d.getWidth(), d.getHeight()) - 20;
                int cellSize = boardSize / map.getSize();

                int x = (arg0.getX() - (d.getWidth() - boardSize) / 2) / cellSize;
                int y = (arg0.getY() - (d.getHeight() - boardSize) / 2) / cellSize;

                // 돌이 이미 있거나 좌표가 유효하지 않은 경우 무시
                if (x < 0 || x >= map.getSize() || y < 0 || y >= map.getSize() ||
                    map.getXY(y, x) == map.getBlack() || map.getXY(y, x) == map.getWhite()) {
                    return;
                }

                // 맵에 돌 배치
                map.setMap(y, x);

                // 턴 변경
                map.changeCheck();

                // 타이머 리셋
                turnTimer.stop();
                startTurnTimer();

                // 돌 출력 및 승리 조건 확인
                d.repaint();
                SwingUtilities.invokeLater(() -> {
                    if (map.winCheck(x, y)) {
                        JOptionPane.showMessageDialog(null, (map.getCheck() ? "백" : "흑") + " 승리!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                        map.reset(); // 게임 재시작
                        d.repaint(); // 보드 초기화
                    } else {
                        // 차례 변경 메시지 업데이트
                        String currentTurn = map.getCheck() ? "흑돌의 차례입니다." : "백돌의 차례입니다.";
                        turnDisplay.setText(currentTurn);
                    }
                });
            }
        });
    }
    
    public void setSpectatorMode(boolean spectatorMode) {
        this.isSpectatorMode = spectatorMode;
        if (spectatorMode) {
            // 관전 모드일 경우 바둑판 클릭 이벤트 비활성화
            d.removeMouseListener(d.getMouseListeners()[0]);
        }
    }
    
    private void appendMessage(String message, boolean isSpectator) {
        try {
            if (txtDisplay == null) {
                txtDisplay = getTxtDisplay();
            }

            StyledDocument doc = txtDisplay.getStyledDocument();
            SimpleAttributeSet style = new SimpleAttributeSet();

            if (isSpectator) {
                // 관전자 메시지 스타일
                StyleConstants.setForeground(style, Color.BLUE);
            } else {
                // 일반 메시지 스타일
                StyleConstants.setForeground(style, Color.BLACK);
            }

            doc.insertString(doc.getLength(), message + "\n", style);
            txtDisplay.setCaretPosition(doc.getLength()); // 스크롤 맨 아래로 이동
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(String nickname, String message) {
        if (isSpectatorMode) {
            appendMessage("[관전] " + nickname + ": " + message, true);
        } else {
            appendMessage(nickname + ": " + message, false);
        }
    }

    public JTextPane getTxtDisplay() {
        if (txtDisplay == null) {
            txtDisplay = new JTextPane();
            txtDisplay.setEditable(false);
            txtDisplay.setPreferredSize(new Dimension(300, 400));
        }
        return txtDisplay;
    }

    public JTextField getTxtInput() {
        if (txtInput == null) {
            txtInput = new JTextField();
            txtInput.setPreferredSize(new Dimension(170, 25));
            txtInput.addActionListener(e -> {
                String message = txtInput.getText().trim();
                if (!message.isEmpty()) {
                    String nickname = "닉네임"; // 닉네임을 가져옵니다.
                    sendChatMessage(nickname, message);
                    txtInput.setText("");
                }
            });
        }
        return txtInput;
    }

    public JButton getBtnSend() {
        if (btnSend == null) {
            btnSend = new JButton("전송");
            btnSend.addActionListener(e -> {
                String message = txtInput.getText().trim();
                if (!message.isEmpty()) {
                    // DB에서 로그인한 사용자의 닉네임 가져오기
                    DBConnection dbConnection = new DBConnection();
                    String nickname = dbConnection.getNickname(); // 로그인된 사용자의 닉네임을 가져옵니다.

                    if (nickname == null || nickname.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "닉네임을 가져올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 관전 모드에 따라 메시지 스타일 다르게 설정
                    sendChatMessage(nickname, message);
                    txtInput.setText("");
                }
            });
        }
        return btnSend;
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

        int xPos = (d.getWidth() - boardSize) / 2 + x * cellSize + cellSize / 2;
        int yPos = (d.getHeight() - boardSize) / 2 + y * cellSize + cellSize / 2;

        g.setColor(color);
        g.fillOval(xPos - cellSize / 3, yPos - cellSize / 3, cellSize * 2 / 3, cellSize * 2 / 3);
        g.dispose();
    }

    public void setPlayer1Profile(String characterName) {
        player1Profile.setIcon(createCharacterIcon(characterName));
        revalidate();
        repaint();
    }

    public void showPopUp(String message) {
        JOptionPane.showMessageDialog(this, message, "", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startTurnTimer() {
        if (turnTimer.isRunning()) {
            turnTimer.stop(); // 이미 실행 중인 타이머가 있으면 중지
        }
        remainingTime = 60;
        timerLabel.setText("남은 시간: " + remainingTime + "초");

        turnTimer = new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                timerLabel.setText("남은 시간: " + remainingTime + "초");
            } else {
                // 시간이 다 되었을 때 상대방 승리 처리
                turnTimer.stop();
                String winner = map.getCheck() ? "백돌" : "흑돌"; // 상대방의 승리
                JOptionPane.showMessageDialog(this, "시간 초과! " + winner + " 승리!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
                
                // 게임 종료 및 맵 초기화
                map.reset();
                d.repaint();
            }
        });
        turnTimer.start();
    }

    private void changeTurn() {
        // 턴 변경 로직
        map.changeCheck();

        // 턴 정보 업데이트
        String currentTurn = map.getCheck() ? "흑돌의 차례입니다." : "백돌의 차례입니다.";
        turnDisplay.setText(currentTurn);

        // 타이머 초기화 및 재시작
        startTurnTimer();
    }
    
    public void mousePressed(MouseEvent e) {
        int boardSize = Math.min(d.getWidth(), d.getHeight()) - 20;
        int cellSize = boardSize / map.getSize();

        int x = (e.getX() - (d.getWidth() - boardSize) / 2) / cellSize;
        int y = (e.getY() - (d.getHeight() - boardSize) / 2) / cellSize;

        // 유효하지 않은 좌표나 이미 돌이 있는 경우 처리 중단
        if (x < 0 || x >= map.getSize() || y < 0 || y >= map.getSize() ||
            map.getXY(y, x) == map.getBlack() || map.getXY(y, x) == map.getWhite()) {
            return;
        }

        // 맵에 돌 배치
        map.setMap(y, x);

        // 승리 조건 확인
        if (map.winCheck(x, y)) {
            turnTimer.stop(); // 타이머 정지
            JOptionPane.showMessageDialog(this, (map.getCheck() ? "백" : "흑") + " 승리!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
            map.reset(); // 맵 초기화
            d.repaint(); // 보드 다시 그리기
        } else {
            // 턴 변경
            turnTimer.stop(); // 현재 타이머 정지
            changeTurn(); // 턴 변경 및 타이머 초기화
        }

        d.repaint(); // 보드 다시 그리기
    }

    private JPanel createProfilePanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(150, 150)); // 정사각형 크기 설정
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 16));

        JLabel profile = new JLabel();
        profile.setPreferredSize(new Dimension(100, 100));
        profile.setHorizontalAlignment(SwingConstants.CENTER);
        profile.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.NORTH);
        panel.add(profile, BorderLayout.CENTER);
        return panel;
    }
    
    private void startGame() {
        if (turnTimer.isRunning()) {
            JOptionPane.showMessageDialog(this, "게임이 이미 시작되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!isPlayer1Ready || !isPlayer2Ready) {
            JOptionPane.showMessageDialog(this, "모든 플레이어가 준비되지 않았습니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "게임을 시작합니다!", "알림", JOptionPane.INFORMATION_MESSAGE);

        map.reset();
        d.repaint();

        map.resetCheck(); // 차례 초기화
        turnDisplay.setText("흑돌의 차례입니다.");
        remainingTime = 60;
        timerLabel.setText("남은 시간: " + remainingTime + "초");

        startTurnTimer();

        startButton.setEnabled(false);
        readyButton.setEnabled(false);
    }
    
    private void toggleReadyState() {
        if (Login.getLoggedInUserId().equals(roomCreator)) {
            // 방장의 레디 상태 토글
            isPlayer1Ready = !isPlayer1Ready;
            player1Panel.setBackground(isPlayer1Ready ? Color.YELLOW : null); // 배경색 변경
            readyButton.setText(isPlayer1Ready ? "취소" : "레디");
        } else {
            // 상대방의 레디 상태 토글
            isPlayer2Ready = !isPlayer2Ready;
            player2Panel.setBackground(isPlayer2Ready ? Color.YELLOW : null); // 배경색 변경
            readyButton.setText(isPlayer2Ready ? "취소" : "레디");
        }

        // 두 플레이어가 모두 레디 상태인지 확인
        updateStartButtonState();
    }
    
    private void updateStartButtonState() {
        // 방장과 상대방이 모두 레디 상태인 경우에만 시작 버튼 활성화
        if (isPlayer1Ready && isPlayer2Ready) {
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }
    }
}
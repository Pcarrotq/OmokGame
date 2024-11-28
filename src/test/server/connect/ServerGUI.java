package test.server.connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ServerGUI extends JFrame {
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private ServerHandler serverHandler;

    public ServerGUI() {
        serverHandler = new ServerHandler();
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Server Manager");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // **상태 패널 (좌측)**
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 여백 추가

        // 상태 레이블
        statusLabel = new JLabel("서버 중지");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        statusLabel.setForeground(Color.RED); // 초기 상태는 빨간색
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(statusLabel);

        // 접속자 수 레이블
        clientCountLabel = new JLabel("접속자: -명");
        clientCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        clientCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(Box.createVerticalStrut(20)); // 간격 추가
        statusPanel.add(clientCountLabel);

        add(statusPanel, BorderLayout.CENTER); // 중앙에 상태 패널 추가

        // **버튼 패널 (우측)**
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 여백 추가

        Dimension buttonSize = new Dimension(150, 40);

        JButton startButton = new JButton("서버 시작");
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton stopButton = new JButton("서버 중지");
        stopButton.setPreferredSize(buttonSize);
        stopButton.setMaximumSize(buttonSize);
        stopButton.setEnabled(false);
        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButton = new JButton("창 닫기");
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼 추가 및 간격 설정
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // 버튼 간 간격
        buttonPanel.add(stopButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.EAST); // 우측에 버튼 패널 추가

        // **버튼 이벤트**
        startButton.addActionListener(e -> startServer(startButton, stopButton));
        stopButton.addActionListener(e -> stopServer(startButton, stopButton));
        closeButton.addActionListener(e -> closeApp());

        setVisible(true); // 프레임 보이기
    }

    private void startServer(JButton startButton, JButton stopButton) {
        serverHandler.startServer(8080);
		statusLabel.setText("서버 실행 중");
		statusLabel.setForeground(Color.BLUE); // 서버 실행 중일 때 파란색
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
    }

    private void stopServer(JButton startButton, JButton stopButton) {
        serverHandler.stopServer();
		statusLabel.setText("서버 중지");
		statusLabel.setForeground(Color.RED); // 서버 중지일 때 빨간색
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
    }

    private void closeApp() {
        if (serverHandler.isRunning()) {
		    serverHandler.stopServer();
		}
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
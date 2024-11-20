package test.server.connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ServerGUI extends JFrame {
    private JFrame frame;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton closeButton;

    private ServerHandler serverHandler;

    public ServerGUI() {
        serverHandler = new ServerHandler();
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Server Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout()); // 기본 레이아웃 변경

        // 중앙에 위치할 텍스트 패널 생성
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // 여백 추가
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 상태 레이블
        statusLabel = new JLabel("서버 중지", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(statusLabel);

        // 접속자 수 레이블
        clientCountLabel = new JLabel("접속자: 0명", SwingConstants.CENTER);
        clientCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        clientCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(Box.createVerticalStrut(20)); // 상태 레이블과의 간격
        textPanel.add(clientCountLabel);

        // 오른쪽에 위치할 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20)); // 여백 추가

        // 버튼 생성 및 추가
        Dimension buttonSize = new Dimension(150, 50);
        startButton = new JButton("서버 시작 버튼");
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        stopButton = new JButton("서버 중지 버튼");
        stopButton.setPreferredSize(buttonSize);
        stopButton.setMaximumSize(buttonSize);
        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        stopButton.setEnabled(false);

        closeButton = new JButton("창 닫기");
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼들 추가 및 간격 설정
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(20)); // 버튼 간 간격
        buttonPanel.add(stopButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(closeButton);

        // 프레임에 컴포넌트 추가
        frame.add(textPanel, BorderLayout.CENTER); // 텍스트는 중앙
        frame.add(buttonPanel, BorderLayout.EAST); // 버튼은 오른쪽

        // 버튼 리스너 추가
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        closeButton.addActionListener(e -> closeApp());

        frame.setVisible(true);
    }

    private void startServer() {
        try {
            serverHandler.startServer(8080);
            statusLabel.setText("서버 시작됨");
            statusLabel.setForeground(Color.GREEN);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "서버 시작 실패: " + e.getMessage());
        }
    }

    private void stopServer() {
        try {
            serverHandler.stopServer();
            statusLabel.setText("서버 중지");
            statusLabel.setForeground(Color.RED);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "서버 중지 실패: " + e.getMessage());
        }
    }

    private void closeApp() {
        try {
            if (serverHandler.isRunning()) {
                serverHandler.stopServer();
            }
        } catch (IOException e) {
            // Ignore
        }
        frame.dispose();
    }
}
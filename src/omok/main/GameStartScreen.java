package omok.main;

import javax.swing.*;

import omok.member.SignUp;
import omok.member.Login;
import omok.game.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameStartScreen extends JFrame {
    public GameStartScreen() {
        // JFrame 설정
        setTitle("게임 시작 화면");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 배치

        // JPanel 생성 및 레이아웃 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Y축으로 구성 요소 배치

        // JLabel: 게임 제목
        JLabel titleLabel = new JLabel("Omok Game", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        panel.add(Box.createVerticalStrut(50)); // 여백 추가
        panel.add(titleLabel);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        loginButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(null, "로그인");
                Login login = new Login();
                login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 로그인 창만 닫힘
                login.setVisible(true);
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(loginButton);
        
        // 회원가입 버튼
        JButton signUpButton = new JButton("회원가입");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        signUpButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(null, "회원가입");
                SignUp signUp = new SignUp();
                signUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 회원가입 창만 닫힘
                signUp.setVisible(true);
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(signUpButton);

        // JButton: 종료 버튼
        JButton exitButton = new JButton("종료");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
        exitButton.setMaximumSize(new Dimension(100, 30)); // 버튼 크기 설정
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 프로그램 종료
                System.exit(0);
            }
        });
        panel.add(Box.createVerticalStrut(20)); // 여백 추가
        panel.add(exitButton);

        // 패널을 프레임에 추가
        add(panel);

        // 화면 표시
        setVisible(true);
    }

    public static void main(String[] args) {
        // 시작 화면 생성
        new GameStartScreen();
    }
}

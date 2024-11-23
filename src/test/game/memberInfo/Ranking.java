package test.game.memberInfo;

import javax.swing.*;
import java.awt.*;

public class Ranking {
    public static void main(String[] args) {
        // 메인 프레임 생성
        JFrame frame = new JFrame("랭킹");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600); // 창 크기 설정
        frame.setLayout(new BorderLayout());

        // 상단 랭킹 표시 영역
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null); // 자유 배치
        topPanel.setPreferredSize(new Dimension(400, 200));

        // 2위 패널
        JPanel secondPanel = new JPanel();
        secondPanel.setBounds(50, 70, 80, 100);
        secondPanel.setBorder(BorderFactory.createTitledBorder("2위"));
        secondPanel.add(new JLabel("(닉네임)"));

        // 1위 패널
        JPanel firstPanel = new JPanel();
        firstPanel.setBounds(150, 20, 100, 150);
        firstPanel.setBorder(BorderFactory.createTitledBorder("1위"));
        firstPanel.add(new JLabel("(닉네임)"));

        // 3위 패널
        JPanel thirdPanel = new JPanel();
        thirdPanel.setBounds(270, 100, 80, 70);
        thirdPanel.setBorder(BorderFactory.createTitledBorder("3위"));
        thirdPanel.add(new JLabel("(닉네임)"));

        // 패널 추가
        topPanel.add(secondPanel);
        topPanel.add(firstPanel);
        topPanel.add(thirdPanel);

        // 하단 랭킹 목록
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(6, 3, 10, 10));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 1; i <= 6; i++) {
            listPanel.add(new JLabel(i + "위"));
            listPanel.add(new JTextField("(닉네임)"));
            listPanel.add(new JTextField("(점수)"));
        }

        // 프레임에 추가
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(listPanel, BorderLayout.CENTER);

        // 창 표시
        frame.setVisible(true);
    }
}
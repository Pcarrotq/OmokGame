/*
 * 유저 기본 정보 창
 * GUI에서 상대방 프로필(캐릭터)를 누르면 이 창이 뜬다.
*/


package test.game.memberInfo;

import javax.swing.*;
import java.awt.*;

public class ProfileWindow {
    public static void main(String[] args) {
        // 메인 프레임 생성
        JFrame frame = new JFrame("정보");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600); // 창 크기 설정
        frame.setLayout(new BorderLayout());

        // 프로필 사진 영역
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("프로필 사진"));
        
        JLabel profileLabel = new JLabel("프로필 사진", SwingConstants.CENTER);
        profileLabel.setPreferredSize(new Dimension(350, 250));
        profilePanel.add(profileLabel, BorderLayout.CENTER);

        // 정보 입력 영역
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3행 2열 그리드 레이아웃
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nicknameLabel = new JLabel("닉네임");
        JTextField nicknameField = new JTextField();

        JLabel rankLabel = new JLabel("순위");
        JTextField rankField = new JTextField();

        JLabel introLabel = new JLabel("한 줄 소개");
        JTextField introField = new JTextField();

        // 패널에 컴포넌트 추가
        infoPanel.add(nicknameLabel);
        infoPanel.add(nicknameField);
        infoPanel.add(rankLabel);
        infoPanel.add(rankField);
        infoPanel.add(introLabel);
        infoPanel.add(introField);

        // 프레임에 패널 추가
        frame.add(profilePanel, BorderLayout.NORTH);
        frame.add(infoPanel, BorderLayout.CENTER);

        // 창 표시
        frame.setVisible(true);
    }
}
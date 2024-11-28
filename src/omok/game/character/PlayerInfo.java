package omok.game.character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import omok.member.UserProfile;
import omok.member.db.DBConnection;

public class PlayerInfo extends JPanel {
    private JPanel photoPanel;
    private JLabel nicknameLabel;
    private JLabel scoreLabel;
    private JTextField scoreField;
    private JLabel rankLabel;
    private JTextField rankField;
    private JLabel introLabel;
    private JTextField introField;
    private JButton confirmButton;

    private DBConnection dbConnection;
    private String userId; // 로그인된 사용자 ID
    private UserProfile userProfile;

    public PlayerInfo(String userId) {
        this.userId = userId;
        this.dbConnection = new DBConnection();
        this.userProfile = dbConnection.getUserProfile(userId);

        // 메인 프레임 생성
        JFrame frame = new JFrame("내 정보");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창만 닫힘
        frame.setSize(400, 550);
        frame.setLayout(null); // 절대 위치 지정
        frame.setResizable(false); // 창 크기 조절 불가

        // 창을 화면 중앙에 배치
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        // 프로필 사진 패널 생성
        photoPanel = new JPanel();
        photoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel photoLabel = new JLabel("프로필 사진", SwingConstants.CENTER);
        if (userProfile != null && userProfile.getProfileImage() != null) {
            ImageIcon profileImage = new ImageIcon(userProfile.getProfileImage());
            photoLabel.setIcon(new ImageIcon(profileImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        }
        photoPanel.setLayout(new BorderLayout());
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.setBounds(50, 30, 300, 150);

        // 닉네임 출력
        String nickname = (userProfile != null && userProfile.getNickname() != null) 
                ? userProfile.getNickname() 
                : "알 수 없음";
        nicknameLabel = new JLabel("닉네임: " + nickname, SwingConstants.CENTER);
        nicknameLabel.setBounds(50, 218, 300, 30);

        // 점수 필드
        scoreLabel = new JLabel("점수");
        scoreLabel.setBounds(50, 280, 50, 30);
        scoreField = new JTextField(String.valueOf(userProfile != null ? userProfile.getScore() : 0));
        scoreField.setEditable(false);
        scoreField.setBounds(115, 280, 235, 30);
        scoreField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scoreField.setBackground(Color.WHITE);

        // 순위 필드
        rankLabel = new JLabel("순위");
        rankLabel.setBounds(50, 320, 50, 30);
        rankField = new JTextField(String.valueOf(userProfile != null ? userProfile.getRank() : 0));
        rankField.setEditable(false);
        rankField.setBounds(115, 320, 235, 30);
        rankField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        rankField.setBackground(Color.WHITE);

        // 한 줄 소개 필드
        introLabel = new JLabel("한 줄 소개");
        introLabel.setBounds(50, 360, 70, 30);
        String existingIntro = (userProfile != null && userProfile.getIntro() != null) 
                ? userProfile.getIntro() 
                : "";
        introField = new JTextField(existingIntro);
        introField.setEditable(true); // 편집 가능
        introField.setBounds(115, 360, 235, 60);
        introField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        introField.setBackground(Color.WHITE);

        // 확인 버튼
        confirmButton = new JButton("확인");
        confirmButton.setBounds(150, 450, 100, 30);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newIntro = introField.getText().trim();
                if (userProfile != null && !newIntro.equals(userProfile.getIntro())) {
                    dbConnection.updateIntro(userId, newIntro); // DB에 업데이트
                    JOptionPane.showMessageDialog(frame, "한 줄 소개가 저장되었습니다.");
                } else {
                    JOptionPane.showMessageDialog(frame, "변경 사항이 없습니다.");
                }
                frame.dispose();
            }
        });

        // 컴포넌트를 프레임에 추가
        frame.add(photoPanel);
        frame.add(nicknameLabel);
        frame.add(scoreLabel);
        frame.add(scoreField);
        frame.add(rankLabel);
        frame.add(rankField);
        frame.add(introLabel);
        frame.add(introField);
        frame.add(confirmButton);

        // 프레임 보이기
        frame.setVisible(true);
    }
}

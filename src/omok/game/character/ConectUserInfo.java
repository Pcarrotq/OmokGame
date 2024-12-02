package omok.game.character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import omok.member.UserProfile;
import omok.member.db.DBConnection;

public class ConectUserInfo extends JPanel {
    private JPanel photoPanel;
    private JLabel nicknameLabel;
    private JLabel scoreLabel;
    private JTextField scoreField;
    private JLabel rankLabel;
    private JTextField rankField;
    private JLabel introLabel;
    private JTextField introField;
    private JButton confirmButton;
    private JButton chatButton;

    private DBConnection dbConnection;
    private String userId; // 로그인된 사용자 ID
    private UserProfile userProfile;

    public ConectUserInfo(String userId) {
        this.userId = userId;
        this.dbConnection = new DBConnection();
        this.userProfile = dbConnection.getUserProfile(userId);

        // 사용자 프로필 정보를 기반으로 UI 초기화
        initializeUI();
    }

    private void initializeUI() {
        // 메인 프레임 생성
        JFrame frame = new JFrame("정보");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 550);
        frame.setLayout(null); // 절대 위치 지정
        frame.setResizable(false);

        // 창을 화면 중앙에 배치
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);

        // 프로필 사진 패널
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

        // 닉네임 라벨
        String nickname = (userProfile != null && userProfile.getNickname() != null) 
                ? userProfile.getNickname() 
                : "알 수 없음";
        nicknameLabel = new JLabel("닉네임: " + nickname, SwingConstants.CENTER);
        nicknameLabel.setBounds(50, 218, 300, 30);

        // 점수 필드
        scoreLabel = new JLabel("점수");
        scoreLabel.setBounds(50, 280, 50, 30);
        scoreField = new JTextField();
        scoreField.setEditable(false);
        scoreField.setBounds(115, 280, 235, 30);
        scoreField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scoreField.setBackground(Color.WHITE);
        scoreField.setText(String.valueOf(userProfile != null ? userProfile.getScore() : 0));

        // 순위 필드
        rankLabel = new JLabel("순위");
        rankLabel.setBounds(50, 320, 50, 30);
        rankField = new JTextField();
        rankField.setEditable(false);
        rankField.setBounds(115, 320, 235, 30);
        rankField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        rankField.setBackground(Color.WHITE);
        rankField.setText(String.valueOf(userProfile != null ? userProfile.getRank() : 0));

        // 한 줄 소개 필드
        introLabel = new JLabel("한 줄 소개");
        introLabel.setBounds(50, 360, 70, 30);
        String intro = (userProfile != null && userProfile.getIntro() != null) 
                ? userProfile.getIntro() 
                : "";
        introField = new JTextField(intro);
        introField.setEditable(true);
        introField.setBounds(115, 360, 235, 60);
        introField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        introField.setBackground(Color.WHITE);

        // 확인 버튼
        confirmButton = new JButton("확인");
        confirmButton.setBounds(100, 450, 100, 30);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        // 대화 버튼
        chatButton = new JButton("대화");
        chatButton.setBounds(210, 450, 100, 30);

        // 컴포넌트 추가
        frame.add(photoPanel);
        frame.add(nicknameLabel);
        frame.add(scoreLabel);
        frame.add(scoreField);
        frame.add(rankLabel);
        frame.add(rankField);
        frame.add(introLabel);
        frame.add(introField);
        frame.add(confirmButton);
        frame.add(chatButton);

        // 프레임 보이기
        frame.setVisible(true);
    }
}
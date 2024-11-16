package omok.chat.client.frame;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import javax.swing.*;

import omok.member.DBConnection;
import omok.member.UserProfile;

@SuppressWarnings("serial")
public class IndexPanel extends JPanel {
    private JLabel jLabel;
    public static JButton userProfileButton;
    public static ArrayList<ChatWindowPanel> chatPanelName = new ArrayList<>();

    public IndexPanel() {
        setLayout(null);
        
        DBConnection db = new DBConnection();
        UserProfile myProfile = db.getUserProfile();
        List<UserProfile> allUsers = db.getAllUsers();
        
        // My Profile 설정
        meanMyProfileTitle("My Profile");
        meanMyProfile(myProfile);
        
        // Friend List 설정
        meanFriendProfileTitle("Friend List");
        showFriendList(allUsers);
    }

    private void meanMyProfileTitle(String text) {
        jLabel = new JLabel(text);
        jLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        jLabel.setBounds(30, 80, 200, 30);
        add(jLabel);
    }

    private void meanMyProfile(UserProfile profile) {
        // 로그인한 사용자의 닉네임을 가져와 설정
        String nickname = profile.getNickname();
        
        userProfileButton = new JButton(nickname); // 로그인한 사용자의 닉네임으로 버튼 텍스트 설정
        userProfileButton.setBounds(30, 120, 325, 80); // 버튼 위치 및 크기 설정
        userProfileButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트 왼쪽 정렬
        userProfileButton.setIconTextGap(10); // 아이콘과 텍스트 사이 간격 설정

        // 프로필 이미지 강제 크기 조정
        if (profile.getProfileImage() != null) {
            try {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(profile.getProfileImage()));
                Image scaledImage = originalImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // My Profile용 크기 설정 (50x50)
                userProfileButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            userProfileButton.setIcon(new ProfileIcon(nickname)); // 기본 아이콘 사용
        }

        add(userProfileButton);

        userProfileButton.addActionListener(e -> {
            if (!userProfileButton.getText().contains("대화 중")) {
                userProfileButton.setText(nickname + "       대화 중..");
                ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(nickname), nickname);
                new ChatWindowFrame(chatPanel, nickname);
                chatPanelName.add(chatPanel);
            }
        });
    }
    
    // 창이 닫힐 때 My Profile 버튼 상태를 초기화하는 메서드
    public static void resetUserProfileButtonState() {
        if (userProfileButton.getText().contains("대화 중")) {
            userProfileButton.setText("User Name");  // "대화 중" 제거
        }
    }

    private void meanFriendProfileTitle(String text) {
        jLabel = new JLabel(text);
        jLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        jLabel.setBounds(30, 220, 200, 30);
        add(jLabel);
    }

    private void showFriendList(List<UserProfile> allUsers) {
        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));

        for (UserProfile user : allUsers) {
            if (!user.getId().equals(DBConnection.loggedInUserId)) {
                String nickname = user.getNickname();
                JButton friendButton = new JButton(nickname);
                friendButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트 왼쪽 정렬
                friendButton.setIconTextGap(10); // 아이콘과 텍스트 사이의 간격 설정

                // 프로필 이미지 강제 크기 조정
                if (user.getProfileImage() != null) {
                    try {
                        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(user.getProfileImage()));
                        Image scaledImage = originalImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH); // 더 작은 크기로 설정 (24x24)
                        friendButton.setIcon(new ImageIcon(scaledImage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    friendButton.setIcon(new ProfileIcon(nickname)); // 기본 아이콘 사용
                }

                // friendButton의 너비와 높이 설정
                friendButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // 버튼 높이를 60으로 설정
                friendButton.setPreferredSize(new Dimension(friendListPanel.getWidth(), 60)); // 너비와 높이 설정

                // 버튼 클릭 시 채팅 창 열기
                friendButton.addActionListener(e -> {
                    if (!friendButton.getText().contains("대화 중")) {
                        friendButton.setText(nickname + "       대화 중..");

                        // ChatWindowPanel을 열고, 창이 닫힐 때 상태 초기화
                        ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(nickname), nickname);
                        ChatWindowFrame chatWindow = new ChatWindowFrame(chatPanel, nickname);
                        
                        chatWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                friendButton.setText(nickname); // "대화 중" 제거
                            }
                        });
                        
                        chatPanelName.add(chatPanel);
                    }
                });

                friendListPanel.add(friendButton);
            }
        }

        JScrollPane friendListScrollPane = new JScrollPane(friendListPanel);
        friendListScrollPane.setBounds(30, 250, 325, 300);
        add(friendListScrollPane);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Line2D lin = new Line2D.Float(30, 210, 350, 210);
        g2.draw(lin);
    }
}
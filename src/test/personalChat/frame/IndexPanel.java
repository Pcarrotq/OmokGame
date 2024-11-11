package test.personalChat.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import test.member.DBConnection;
import test.member.UserProfile;
import test.personalChat.server.ServerSocket;
import test.personalChat.frame.ChatWindowPanel;
import test.personalChat.frame.ProfileIcon;

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
        
        meanMyProfileTitle("My Profile");
        meanMyProfile(myProfile);
        
        meanFriendProfileTitle("Friend List");
        showFriendList(allUsers);
    }

    private void meanMyProfile(UserProfile profile) {
        String nickname = profile.getNickname();
        
        userProfileButton = new JButton(nickname);
        userProfileButton.setBounds(30, 120, 325, 80);
        userProfileButton.setHorizontalAlignment(SwingConstants.LEFT);
        userProfileButton.setIconTextGap(10);

        if (profile.getProfileImage() != null) {
            try {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(profile.getProfileImage()));
                Image scaledImage = originalImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                userProfileButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            userProfileButton.setIcon(new ProfileIcon(nickname));
        }

        add(userProfileButton);

        userProfileButton.addActionListener(e -> {
            if (!userProfileButton.getText().contains("대화 중")) {
                userProfileButton.setText(nickname + "       대화 중..");

                // ServerSocket 객체 생성 및 설정
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.setUserName("User");
                serverSocket.startClient();

                // ChatWindowPanel 생성 및 서버 소켓 설정
                ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(nickname), nickname, serverSocket);
                serverSocket.setChatWindowPanel(chatPanel);

                new ChatWindowFrame(chatPanel, nickname);
                chatPanelName.add(chatPanel);
            }
        });
    }
    
    private void meanMyProfileTitle(String text) {
        jLabel = new JLabel(text);
        jLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        jLabel.setBounds(30, 80, 200, 30); // 위치 및 크기 설정
        add(jLabel);
    }
    
    private void meanFriendProfileTitle(String text) {
        JLabel friendTitleLabel = new JLabel(text);
        friendTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        friendTitleLabel.setBounds(30, 220, 200, 30); // 위치 및 크기 설정
        add(friendTitleLabel);
    }
    
    // My Profile 버튼의 상태를 초기화하는 메서드
    public static void resetUserProfileButtonState() {
        if (userProfileButton != null && userProfileButton.getText().contains("대화 중")) {
            userProfileButton.setText(userProfileButton.getText().replace("       대화 중..", ""));
        }
    }

    private void showFriendList(List<UserProfile> allUsers) {
        JPanel friendListPanel = new JPanel();
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));

        for (UserProfile user : allUsers) {
            if (!user.getId().equals(DBConnection.loggedInUserId)) {
                String nickname = user.getNickname();
                JButton friendButton = new JButton(nickname);
                friendButton.setHorizontalAlignment(SwingConstants.LEFT);
                friendButton.setIconTextGap(10);

                if (user.getProfileImage() != null) {
                    try {
                        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(user.getProfileImage()));
                        Image scaledImage = originalImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                        friendButton.setIcon(new ImageIcon(scaledImage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    friendButton.setIcon(new ProfileIcon(nickname));
                }

                friendButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                friendButton.setPreferredSize(new Dimension(friendListPanel.getWidth(), 60));

                friendButton.addActionListener(e -> {
                    if (!friendButton.getText().contains("대화 중")) {
                        friendButton.setText(nickname + "       대화 중..");

                        ServerSocket serverSocket = new ServerSocket();
                        serverSocket.setUserName("User");
                        serverSocket.startClient();

                        ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(nickname), nickname, serverSocket);
                        serverSocket.setChatWindowPanel(chatPanel);

                        ChatWindowFrame chatWindow = new ChatWindowFrame(chatPanel, nickname);
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
}
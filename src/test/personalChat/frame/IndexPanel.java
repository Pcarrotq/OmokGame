package test.personalChat.frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

@SuppressWarnings("serial")
public class IndexPanel extends JPanel {
    private JLabel jLabel;
    public static JButton userProfileButton;
    public static ArrayList<ChatWindowPanel> chatPanelName = new ArrayList<>();  

    public IndexPanel() {
        setLayout(null);
        
        meanMyProfileTitle("My Profile");
        meanMyProfile();
        
        meanFriendProfileTitle("Friend List");
        showFriendList();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("KevotingTalk - Friend List");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 600);
            frame.add(new IndexPanel());
            frame.setVisible(true);
        });
    }

    private void meanMyProfileTitle(String text) {
        jLabel = new JLabel(text);
        jLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        jLabel.setBounds(30, 80, 200, 30);
        add(jLabel);
    }

    private void meanMyProfile() {
        userProfileButton = new JButton("User Name");
        userProfileButton.setBounds(30, 120, 325, 80);
        userProfileButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // 프로필을 나타내기 위한 단순 도형 추가
        userProfileButton.setIcon(new ProfileIcon());
        add(userProfileButton);

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userProfileButton.getText().contains("대화 중..")) {
                    // 이미 대화 중인 상태일 경우 아무 작업도 수행하지 않음
                } else {
                    userProfileButton.setText(userProfileButton.getText() + "       대화 중..");
                    ChatWindowPanel c = new ChatWindowPanel(new ProfileIcon(), "User Name");  // 직접 사용자 이름 설정
                    new ChatWindowFrame(c, "User Name");
                    chatPanelName.add(c);
                }
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

    private void showFriendList() {
        FriendListPanel jpanel = new FriendListPanel();
        JScrollPane scroller = new JScrollPane(jpanel);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setBounds(30, 250, 325, 300);
        add(scroller);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Line2D lin = new Line2D.Float(30, 210, 350, 210);
        g2.draw(lin);
    }

    // 프로필 아이콘을 나타내는 내부 클래스
    private class ProfileIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x + 5, y + 5, 50, 50); // 동그란 프로필 모양
            g2d.setColor(Color.BLACK);
            g2d.drawString("User", x + 20, y + 40); // 프로필 내에 "User" 텍스트 추가
        }

        @Override
        public int getIconWidth() {
            return 60;
        }

        @Override
        public int getIconHeight() {
            return 60;
        }
    }
}
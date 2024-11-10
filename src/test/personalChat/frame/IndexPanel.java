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
        
        userProfileButton.setIcon(new ProfileIcon("User"));
        add(userProfileButton);

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userProfileButton.getText().contains("대화 중..")) {
                    // 이미 대화 중인 상태일 경우 아무 작업도 수행하지 않음
                } else {
                    userProfileButton.setText(userProfileButton.getText() + "       대화 중..");
                    ChatWindowPanel c = new ChatWindowPanel(new ProfileIcon("User"), "User Name");  // 직접 사용자 이름 설정
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
}
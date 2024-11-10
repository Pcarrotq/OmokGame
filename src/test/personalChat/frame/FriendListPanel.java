package test.personalChat.frame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class FriendListPanel extends JPanel {
    private ArrayList<String> friends; // 친구들 이름 저장
    private ArrayList<ImageIcon> friendIcons = new ArrayList<>(); // 친구들 프로필 이미지 저장
    public static ArrayList<JButton> friendButtons = new ArrayList<>(); // 친구들 정보 버튼 저장
    private final int FRIEND_PROFILE_IMG_MAX = 8;
    private final int FRIEND_PROFILE_IMG_MIN = 1;

    public FriendListPanel() {
        setBackground(Color.LIGHT_GRAY); // 예시 배경색 설정
        friends = new ArrayList<>(); // 예시 친구 리스트
        friends.add("Friend1");
        friends.add("Friend2"); // 임시 친구 추가
        int friendNum = friends.size();
        setLayout(new GridLayout(friendNum, 0));
        
        for (int index = 0; index < friendNum; index++) {
            Random rand = new Random();
            int randomNum = rand.nextInt((FRIEND_PROFILE_IMG_MAX - FRIEND_PROFILE_IMG_MIN) + 1) + FRIEND_PROFILE_IMG_MIN;
            ImageIcon imageIcon = new ImageIcon("resources/friendProfile/profile" + randomNum + ".png");
            JButton friendButton = new JButton(friends.get(index), imageIcon);
            friendButton.setHorizontalAlignment(SwingConstants.LEFT); // 아이콘과 텍스트를 좌측 정렬
            add(friendButton);
            friendIcons.add(imageIcon);
            friendButtons.add(friendButton);
        }
        
        for (int i = 0; i < friendNum; i++) {
            friendButtons.get(i).putClientProperty("page", i);
            friendButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int idx = (Integer) ((JButton) e.getSource()).getClientProperty("page");
                    if (friendButtons.get(idx).getText().contains("대화 중..")) {
                        // 작동x
                    } else {
                        friendButtons.get(idx).setText(friendButtons.get(idx).getText() + "       대화 중..");
                        ChatWindowPanel c = new ChatWindowPanel(friendIcons.get(idx), friends.get(idx));
                        new ChatWindowFrame(c, friends.get(idx));
                    }
                }
            });
        }
    }
    
    // 창이 닫힐 때 버튼 상태를 초기화하는 메서드
    public static void resetFriendButtonState(String friendName) {
        for (JButton friendButton : friendButtons) {
            if (friendButton.getText().contains(friendName)) {
                friendButton.setText(friendName);  // "대화 중" 제거
            }
        }
    }
}
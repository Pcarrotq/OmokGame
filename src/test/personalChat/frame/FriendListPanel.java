package test.personalChat.frame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import test.personalChat.server.ServerSocket;
import test.personalChat.frame.ChatWindowPanel;
import test.personalChat.frame.ProfileIcon;

@SuppressWarnings("serial")
public class FriendListPanel extends JPanel {
    private ArrayList<String> friends; // 친구들 이름 저장
    public static ArrayList<JButton> friendButtons = new ArrayList<>(); // 친구들 정보 버튼 저장

    public FriendListPanel() {
        setBackground(Color.LIGHT_GRAY);
        
        // 친구 리스트 초기화
        friends = new ArrayList<>();
        friends.add("Friend1");
        friends.add("Friend2");

        int friendNum = friends.size();
        setLayout(new GridLayout(friendNum, 0));
        
        for (int index = 0; index < friendNum; index++) {
            final int currentIndex = index;

            JButton friendButton = new JButton(friends.get(currentIndex), new ProfileIcon(friends.get(currentIndex)));
            friendButton.setHorizontalAlignment(SwingConstants.LEFT);

            friendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentIndex < friends.size() && !friendButton.getText().contains("대화 중")) {
                        friendButton.setText(friendButton.getText() + "       대화 중..");

                        // ServerSocket 객체 생성 및 설정
                        ServerSocket serverSocket = new ServerSocket();
                        serverSocket.setUserName("User");
                        serverSocket.startClient();

                        // ChatWindowPanel 생성 및 서버 소켓 설정
                        ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(friends.get(currentIndex)), friends.get(currentIndex), serverSocket);
                        serverSocket.setChatWindowPanel(chatPanel);

                        new ChatWindowFrame(chatPanel, friends.get(currentIndex));
                    }
                }
            });

            add(friendButton);
            friendButtons.add(friendButton);
        }
    }

    public static void resetFriendButtonState(String friendName) {
        for (JButton friendButton : friendButtons) {
            if (friendButton.getText().contains(friendName)) {
                friendButton.setText(friendName);  // "대화 중" 제거
            }
        }
    }
}
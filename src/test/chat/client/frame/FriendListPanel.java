package test.chat.client.frame;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.*;

import test.chat.controller.Controller;
import test.chat.server.datacommunication.Message;

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
            final int currentIndex = index; // index 값을 복사하여 final로 선언

            Controller controller = Controller.getInstance();
            JButton friendButton = new JButton(friends.get(currentIndex), new ProfileIcon(friends.get(currentIndex)));
            friendButton.setHorizontalAlignment(SwingConstants.LEFT); // 아이콘과 텍스트를 좌측 정렬

            friendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	int idx = (Integer) ((JButton) e.getSource()).getClientProperty("page");
                    // 친구 리스트의 크기를 확인하여 IndexOutOfBoundsException 방지
                    if (currentIndex < friends.size() && !friendButton.getText().contains("대화 중")) {
                        friendButton.setText(friendButton.getText() + "       대화 중..");
                        
                        String messageType = "text";
                        Message message = new Message(controller.username, controller.username + "님이 입장하였습니다.", LocalTime.now(), messageType, friends.get(idx));

                        // 정확한 친구 이름을 사용하여 ProfileIcon 및 ChatWindowPanel 생성
                        ChatWindowPanel chatPanel = new ChatWindowPanel(new ProfileIcon(friends.get(currentIndex)), friends.get(currentIndex));
                        new ChatWindowFrame(chatPanel, friends.get(currentIndex));
                        controller.clientSocket.send(message);
                    }
                }
            });

            add(friendButton);
            friendButtons.add(friendButton);
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
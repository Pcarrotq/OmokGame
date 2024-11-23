package test.chat.client.frame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import test.chat.controller.Controller;

@SuppressWarnings("serial")
public class FriendListPanel extends JPanel {
	private ArrayList<String> friends; // 친구들 이름 저장
	public static ArrayList<JButton> friendButtons = new ArrayList<JButton>(); // 친구들 정보 버튼 저장
	private final ImageIcon defaultIcon = new ImageIcon(); // 빈 이미지 아이콘 생성

	public FriendListPanel() {
	    // 친구 리스트 초기화
	    Controller controller = Controller.getInstance();
	    friends = (ArrayList<String>) controller.friendList(); // Controller에서 친구 목록 가져오기

	    if (friends == null) {
	        friends = new ArrayList<>(); // null 방지를 위해 빈 리스트로 초기화
	    }

	    int friendNum = friends.size();

	    // 로그인된 사용자 이름 가져오기
	    String loggedInUser = Controller.getInstance().username;

	    setLayout(new GridLayout(friendNum, 0));

	    for (String friend : friends) {
	        JButton friendButton = new JButton(friend); // 친구 이름 텍스트 버튼으로 표시
	        friendButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트를 왼쪽 정렬

	        // 로그인된 사용자와 동일한 이름이면 강조
	        if (friend.equals(loggedInUser)) {
	            friendButton.setFont(friendButton.getFont().deriveFont(Font.BOLD));
	            friendButton.setForeground(Color.BLUE);
	        }

	        add(friendButton);
	        friendButtons.add(friendButton);
	    }

	    // 버튼 클릭 이벤트 설정
	    for (int i = 0; i < friendNum; i++) {
	        friendButtons.get(i).putClientProperty("page", i);
	        friendButtons.get(i).addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                int idx = (Integer) ((JButton) e.getSource()).getClientProperty("page");
	                String friendName = friends.get(idx);

	                if (friendButtons.get(idx).getText().contains("대화 중..")) {
	                    JOptionPane.showMessageDialog(null, "이미 대화 중인 친구입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
	                    return;
	                }

	                // 대화 중 상태 표시
	                friendButtons.get(idx).setText(friendButtons.get(idx).getText() + "       대화 중..");

	                // 로그인 여부 확인
	                if (Controller.getInstance().username == null) {
	                    JOptionPane.showMessageDialog(null, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
	                    friendButtons.get(idx).setText(friendButtons.get(idx).getText().replace("       대화 중..", "")); // 상태 초기화
	                    return; // 채팅 창 생성 중단
	                }

	                // 채팅 창 열기
	                ChatWindowPanel chatWindowPanel = new ChatWindowPanel(friendName);
	                new ChatWindowFrame(chatWindowPanel, friendName);

	                // 친구 이름과 채팅 창 패널 등록
	                IndexPanel.chatPanelName.add(chatWindowPanel);
	            }
	        });
	    }
	}
}

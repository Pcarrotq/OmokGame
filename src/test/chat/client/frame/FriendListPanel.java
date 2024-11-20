package test.chat.client.frame;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;

import test.chat.controller.Controller;
import test.server.connect.Message;

@SuppressWarnings("serial")
public class FriendListPanel extends JPanel {
	private ArrayList<String> friends; // 친구들 이름 저장
	public static ArrayList<JButton> friendButtons = new ArrayList<JButton>(); // 친구들 정보 버튼 저장
	private final ImageIcon defaultIcon = new ImageIcon(); // 빈 이미지 아이콘 생성

	public FriendListPanel() {
		// setBackground(ColorSet.talkBackgroundColor);

		Controller controller = Controller.getInstance();
		friends = controller.friendList();
		int friendNum = friends.size();

		setLayout(new GridLayout(friendNum, 0));

		for (int index = 0; index < friendNum; index++) {
			JButton friendButton = new JButton(friends.get(index)); // 친구 이름 텍스트 버튼으로 표시
			friendButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트를 왼쪽 정렬
			add(friendButton);
			friendButtons.add(friendButton);
		}

		for (int i = 0; i < friendNum; i++) {
			friendButtons.get(i).putClientProperty("page", i);
			friendButtons.get(i).addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int idx = (Integer) ((JButton) e.getSource()).getClientProperty("page");
					if (friendButtons.get(idx).getText().contains("대화 중..")) {
						// 이미 대화 중일 때 작동x
					} else {
						friendButtons.get(idx).setText(friendButtons.get(idx).getText() + "       대화 중..");
						String messageType = "text";
						Message message = new Message(controller.username, controller.username + "님이 입장하였습니다.",
								LocalTime.now(), messageType, friends.get(idx));
						ChatWindowPanel c = new ChatWindowPanel(friends.get(idx)); // 기본 아이콘 전달
						new ChatWindowFrame(c, friends.get(idx));
						IndexPanel.chatPanelName.add(c);

						// 로그인한 사용자의 아이디를 얻어서
						Controller controller = Controller.getInstance();
						// 사용자에게 메시지를 전달한다.
						controller.clientSocket.send(message);
					}
				}
			});
		}
	}
}
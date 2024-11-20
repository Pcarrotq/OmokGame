package test.chat.client.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.*;

import test.chat.client.util.CommonPanel;
import test.chat.controller.Controller;
import test.chat.enums.CommonWord;
import test.server.connect.Message;

@SuppressWarnings("serial")
public class IndexPanel extends CommonPanel {
	private JLabel jLabel;
	public static JButton userProfileButton; // 이미지를 사용하지 않고 기본 버튼으로 대체
	public static ArrayList<ChatWindowPanel> chatPanelName = new ArrayList<ChatWindowPanel>();
	Controller controller;

	public IndexPanel() {
	    controller = Controller.getInstance();

	    meanMyProfileTitle(CommonWord.MYPROFILE.getText());
	    meanMyProfile();
	
	    meanFriendProfileTitle(CommonWord.FRIEND.getText());
	    showFriendList();
	}

	private void meanMyProfileTitle(String text) {
	    jLabel = new JLabel(text);
	    jLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
	    jLabel.setBounds(30, 80, 200, 30);
	    add(jLabel);
	}

	private void meanMyProfile() {
		userProfileButton = new JButton(controller.username); // 텍스트 버튼으로 대체
		userProfileButton.setBounds(30, 120, 325, 80);
		add(userProfileButton);

		userProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (userProfileButton.getText().contains("대화 중..")) {
					// 이미 대화 중일 때 작동하지 않음
				} else {
					userProfileButton.setText(userProfileButton.getText() + "       대화 중..");
			        String messageType = "text";
			        Message message = new Message(controller.username, controller.username + "님이 입장하였습니다.", LocalTime.now(), messageType, controller.username);
			        ChatWindowPanel c = new ChatWindowPanel(controller.username);
			        new ChatWindowFrame(c, controller.username);
			        chatPanelName.add(c);
			
			        Controller controller = Controller.getInstance();
			        controller.clientSocket.send(message);
				}
			}
		});
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
package test.personalChat.frame;

import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ChatWindowFrame extends JFrame {
	private String frameName;
	
	public ChatWindowFrame(JPanel panel, String frameName) {
		this.frameName = frameName;
	
		setTitle(frameName + "의 Chatting");
		setBounds(1200, 250, 405, 605);
		getContentPane().add(panel);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
        // 창 닫힘 이벤트 리스너 추가
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 창이 닫힐 때, "대화 중" 텍스트 상태를 초기화하는 메서드를 호출
                FriendListPanel.resetFriendButtonState(frameName);
                IndexPanel.resetUserProfileButtonState();
            }
        });
	}
	
	public String getFrameName() {
		return frameName;
	}
	
	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}
}
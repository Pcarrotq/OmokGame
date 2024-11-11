package chatting.test2.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;

import chatting.test2.client.frame.FriendListPanel;
import chatting.test2.client.frame.IndexPanel;
import chatting.test2.controller.Controller;

public class JFrameWindowClosingEventHandler extends WindowAdapter {
  private String frameName;

  public JFrameWindowClosingEventHandler(String frameName) {
    this.frameName = frameName;
  }

  public void windowClosing(WindowEvent e) {
    JFrame frame = (JFrame) e.getWindow();
    frame.dispose();
    
    Controller controller = Controller.getInstance();
    // 본인 채팅방일때
    if (controller.username.equals(frameName)) {
      IndexPanel.userProfileButton.setText(frameName);
    }
    // 친구 채팅방일때
    for (JButton j : FriendListPanel.friendButtons) {
      if (j.getText().contains(frameName)) {
        j.setText(frameName);
      }
    }
  }
}
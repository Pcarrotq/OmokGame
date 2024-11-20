package test.chat.client.util;

import java.awt.event.*;
import javax.swing.*;

import test.chat.client.frame.*;
import test.chat.controller.Controller;

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
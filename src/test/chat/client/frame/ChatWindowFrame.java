package test.chat.client.frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import test.chat.client.util.JFrameWindowClosingEventHandler;
import test.chat.controller.Controller;

@SuppressWarnings("serial")
public class ChatWindowFrame extends JFrame {
  private String frameName;

  public ChatWindowFrame(JPanel panel, String frameName) {
    Controller controller = Controller.getInstance();
    
    this.frameName = frameName;

	setTitle(frameName + "의 Chatting");
	setBounds(1200, 250, 405, 605);
	getContentPane().add(panel);
	setResizable(false);
	setVisible(true);
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	addWindowListener(new JFrameWindowClosingEventHandler(getFrameName()));
  }

  public String getFrameName() {
    return frameName;
  }

  public void setFrameName(String frameName) {
    this.frameName = frameName;
  }
}
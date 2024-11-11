package chatting.test2.client.frame;

import javax.swing.*;

import chatting.test2.controller.Controller;
import chatting.test2.util.JFrameWindowClosingEventHandler;

@SuppressWarnings("serial")
public class ChatWindowFrame extends JFrame {
  private String frameName;

  public ChatWindowFrame(JPanel panel, String frameName) {
    Controller controller = Controller.getInstance();
    
    this.frameName = frameName;

    setTitle(controller.username + "의 Chatting");
    setBounds(1200, 250, 405, 605);
    getContentPane().add(panel);
    setResizable(false);
    setVisible(true);
    addWindowListener(new JFrameWindowClosingEventHandler(getFrameName()));
  }

  public String getFrameName() {
    return frameName;
  }

  public void setFrameName(String frameName) {
    this.frameName = frameName;
  }
}
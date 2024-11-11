package chatting.test2.client;

import chatting.test2.client.frame.MainFrame;

public class ClientLaunch {
  public static void main(String[] args) {
    try {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
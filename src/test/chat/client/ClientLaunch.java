package test.chat.client;

import test.chat.client.frame.MainFrame;

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
package test.chat.util;

import java.awt.Image;
import javax.swing.*;

@SuppressWarnings("serial")
public class CommonPanel extends JPanel {
  protected JLabel logoLabel;

  protected CommonPanel() {
    setLayout(null);

    logoLabel = new JLabel("Chatting");
    logoLabel.setBounds(-30, 0, 200, 50);
    add(logoLabel);
  }
}
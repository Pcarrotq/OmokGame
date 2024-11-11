package test.chat.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class MainPanelButton extends JButton {
  public MainPanelButton(String buttonTitle) {
    setName(buttonTitle);
    setText(buttonTitle);
    setBackground(Color.WHITE);
    setFocusPainted(false);
    setFont(new Font("맑은 고딕", Font.BOLD, 18));

    Border emptyBorder = BorderFactory.createEmptyBorder();
    setBorder(emptyBorder);
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
  }
}
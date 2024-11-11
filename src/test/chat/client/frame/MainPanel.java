package test.chat.client.frame;

import java.awt.event.*;
import javax.swing.*;

import test.chat.enums.CommonWord;
import test.chat.util.*;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
  private JLabel logoLabel;
  private MainPanelButton signUpButton;
  private MainPanelButton loginButton;
  public static MainFrame frame;

  public MainPanel(MainFrame frame) {
    MainPanel.frame = frame;
    setBackground(ColorSet.talkBackgroundColor);
    setLayout(null);
    showLogo();
    moveSignUpPanel();
    moveLogoPanel();
  }

  /*로고 아이콘을 텍스트로 대체*/
  private void showLogo() {
    logoLabel = new JLabel("My Application"); // 텍스트로 로고 대체
    logoLabel.setFont(logoLabel.getFont().deriveFont(24f)); // 로고 텍스트 크기 조정
    logoLabel.setHorizontalAlignment(JLabel.CENTER);
    logoLabel.setBounds(95, 90, 200, 200);
    add(logoLabel);
  }

  /*회원가입 버튼*/
  private void moveSignUpPanel() {
    signUpButton = new MainPanelButton(CommonWord.SIGN_UP_MEMBERSHIP.getText());
    signUpButton.setBounds(30, 370, 330, 35);
    signUpButton.setOpaque(true);
    add(signUpButton);
    signUpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JoinMembershipPanel joinMembershipPanel = new JoinMembershipPanel();
        MainPanel.frame.change(joinMembershipPanel);
      }
    });
  }

  /*로그인 버튼*/
  private void moveLogoPanel() {
    loginButton = new MainPanelButton(CommonWord.LOGIN.getText());
    loginButton.setBounds(30, 420, 330, 35);
    loginButton.setOpaque(true);
    add(loginButton);
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LoginPanel loginPanel = new LoginPanel();
        MainPanel.frame.change(loginPanel);
      }
    });
  }
}
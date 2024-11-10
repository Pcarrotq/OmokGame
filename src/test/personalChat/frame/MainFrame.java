package test.personalChat.frame;

import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("KevotingTalk");
        setBounds(800, 250, 400, 600);
        getContentPane().add(new JPanel());  // 초기 패널 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    /* 패널 바꾸기 */
    public void change(JPanel panelName) {
        getContentPane().removeAll();
        getContentPane().add(panelName);
        revalidate();
        repaint();
    }
}
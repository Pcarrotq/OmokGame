package test.chat.client;

import javax.swing.*;
import test.chat.client.frame.IndexPanel;

public class ChatLauncher {
	public static void main(String[] args) {
        // 메인 프레임 생성
        JFrame mainFrame = new JFrame("Chat Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(300, 200, 400, 600); // 창 위치와 크기 설정

        // IndexPanel 추가
        IndexPanel indexPanel = new IndexPanel();
        mainFrame.getContentPane().add(indexPanel);

        // 프레임 설정
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
	}
}
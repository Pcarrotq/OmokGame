package test.game;

import javax.swing.JFrame;

public class OmokeGameMain {

    public static void main(String[] args) {
        // 게임 GUI 창 생성
        GUI gameGui = new GUI("오목");
        
        // 게임 창을 JFrame에 추가하고 표시
        JFrame frame = new JFrame("Omok Multiplayer Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1000);
        frame.setContentPane(gameGui);
        frame.setVisible(true);
    }
}
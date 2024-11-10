package test.personalChat.frame;

import javax.swing.*;
import java.awt.*;

public class ProfileIcon implements Icon {
    private String text;

    public ProfileIcon(String text) {
        this.text = text;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(x, y, 50, 50); // 원형 배경

        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (50 - fm.stringWidth(text)) / 2;
        int textY = y + ((50 - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY); // 텍스트를 원 가운데로 배치
    }

    @Override
    public int getIconWidth() {
        return 50;
    }

    @Override
    public int getIconHeight() {
        return 50;
    }
}
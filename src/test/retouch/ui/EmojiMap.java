package test.retouch.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.HashMap;

public class EmojiMap {
    private HashMap<String, ImageIcon> emojiMap;

    public EmojiMap() {
        emojiMap = new HashMap<>();
        initializeEmojiMap();
    }

    private void initializeEmojiMap() {
        emojiMap.put("😊", createSmileEmoji());
        emojiMap.put("😂", createLaughEmoji());
        emojiMap.put("😢", createCryEmoji());
        emojiMap.put("😲", createSurprisedEmoji());
        emojiMap.put("😡", createAngryEmoji());
        emojiMap.put("😍", createLoveEmoji());
        emojiMap.put("😴", createSleepyEmoji());
    }

    public ImageIcon getEmoji(String key) {
        return emojiMap.get(key);
    }

    public HashMap<String, ImageIcon> getAllEmojis() {
        return emojiMap;
    }

    private ImageIcon createSmileEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈
        g2d.setColor(Color.BLACK);
        g2d.fillOval(12, 15, 8, 8);
        g2d.fillOval(30, 15, 8, 8);

        // 입
        g2d.drawArc(15, 25, 20, 10, 0, -180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createLaughEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈
        g2d.setColor(Color.BLACK);
        g2d.fillOval(12, 15, 8, 8);
        g2d.fillOval(30, 15, 8, 8);

        // 입과 웃는 이빨
        g2d.drawArc(15, 25, 20, 10, 0, -180);
        g2d.drawLine(15, 30, 35, 30);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createCryEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈
        g2d.setColor(Color.BLACK);
        g2d.fillOval(12, 15, 8, 8);
        g2d.fillOval(30, 15, 8, 8);

        // 눈물
        g2d.setColor(Color.CYAN);
        g2d.fillOval(10, 28, 5, 10);

        // 입
        g2d.setColor(Color.BLACK);
        g2d.drawArc(15, 30, 20, 10, 0, 180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createSurprisedEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈
        g2d.setColor(Color.BLACK);
        g2d.fillOval(12, 15, 8, 8);
        g2d.fillOval(30, 15, 8, 8);

        // 입 (동그란 놀람)
        g2d.drawOval(20, 30, 10, 10);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createAngryEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈
        g2d.setColor(Color.BLACK);
        g2d.fillOval(12, 15, 8, 8);
        g2d.fillOval(30, 15, 8, 8);

        // 입 (화남)
        g2d.drawArc(15, 35, 20, 10, 0, 180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createLoveEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 하트 모양 눈
        g2d.setColor(Color.RED);
        drawHeart(g2d, 14, 15, 12, 12); // 왼쪽 하트 눈
        drawHeart(g2d, 30, 15, 12, 12); // 오른쪽 하트 눈

        // 입 (미소)
        g2d.setColor(Color.BLACK);
        g2d.drawArc(15, 30, 20, 10, 0, -180);

        g2d.dispose();
        return new ImageIcon(image);
    }
    
	// 하트 모양을 그리는 메서드
    private void drawHeart(Graphics2D g2d, int x, int y, int width, int height) {
        // 삼각형 좌표를 첫 번째 코드처럼 왼쪽으로 이동하여 균형 맞추기
        int offset = width / 4; // 이동할 거리 조정
        int[] triangleX = {x - offset, x + width / 2 - offset, x + width - offset};
        int[] triangleY = {y + height / 4, y + height, y + height / 4};

        // 위쪽 곡선, 두 번째 코드처럼 하트의 윗부분 만들기
        g2d.fillArc(x - width / 4, y, width / 2, height / 2, 0, 180);  // 왼쪽 반원
        g2d.fillArc(x + width / 4, y, width / 2, height / 2, 0, 180);  // 오른쪽 반원

        // 하트 아래 삼각형, 첫 번째 코드처럼 균형 맞춰서 배치
        g2d.fillPolygon(triangleX, triangleY, 3);
    }

    private ImageIcon createSleepyEmoji() {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 얼굴
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, size, size);

        // 눈 (닫힌 눈)
        g2d.setColor(Color.BLACK);
        g2d.drawLine(12, 20, 20, 20);
        g2d.drawLine(30, 20, 38, 20);

        // 입 (작은 입)
        g2d.drawLine(20, 35, 30, 35);

        g2d.dispose();
        return new ImageIcon(image);
    }
}
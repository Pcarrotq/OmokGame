package omok.game.board.frame;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawBoard extends JPanel {
    private BoardMap map;
    private static final int STONE_SIZE = 35;
    private static final Color BOARD_COLOR = new Color(206, 167, 61);
    private static final Color BLACK_STONE_COLOR = Color.BLACK;
    private static final Color WHITE_STONE_COLOR = Color.WHITE;

    public DrawBoard(BoardMap map) {
        setBackground(Color.WHITE);
        setLayout(null);
        this.map = map;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int boardSize = Math.min(getWidth(), getHeight()) - 20;

        // 바둑판의 중앙 위치 계산
        int offsetX = (getWidth() - boardSize) / 2;
        int offsetY = (getHeight() - boardSize) / 2;

        // Draw board boundary
        g.setColor(Color.BLACK);
        g.drawRect(offsetX, offsetY, boardSize - 1, boardSize - 1);

        // Fill board background
        g.setColor(BOARD_COLOR);
        g.fillRect(offsetX + 1, offsetY + 1, boardSize - 2, boardSize - 2);

        drawGrid(g, boardSize, offsetX, offsetY);
        drawStones(g, boardSize, offsetX, offsetY);
    }

    // 그리드 그리기 메서드 수정
    private void drawGrid(Graphics g, int boardSize, int offsetX, int offsetY) {
        int cellSize = boardSize / map.getSize();
        int startX = offsetX + cellSize / 2; // 선 시작 위치 조정
        int startY = offsetY + cellSize / 2; // 선 시작 위치 조정

        g.setColor(Color.BLACK);
        for (int i = 0; i < map.getSize(); i++) {
            // 가로 선 그리기
            g.drawLine(startX, startY + i * cellSize, startX + (map.getSize() - 1) * cellSize, startY + i * cellSize);
            // 세로 선 그리기
            g.drawLine(startX + i * cellSize, startY, startX + i * cellSize, startY + (map.getSize() - 1) * cellSize);
        }
    }

    // 돌 그리기 메서드 수정
    private void drawStones(Graphics g, int boardSize, int offsetX, int offsetY) {
        int cellSize = boardSize / map.getSize();
        for (int y = 0; y < map.getSize(); y++) {
            for (int x = 0; x < map.getSize(); x++) {
                int stoneType = map.getXY(y, x);
                if (stoneType == map.getBlack()) {
                    drawStone(g, x, y, cellSize, BLACK_STONE_COLOR, offsetX, offsetY);
                } else if (stoneType == map.getWhite()) {
                    drawStone(g, x, y, cellSize, WHITE_STONE_COLOR, offsetX, offsetY);
                }
            }
        }
    }

    // 돌 그리기 위치 보정 추가
    private void drawStone(Graphics g, int x, int y, int cellSize, Color stoneColor, int offsetX, int offsetY) {
        int stoneX = offsetX + x * cellSize + (cellSize / 2) - (STONE_SIZE / 2);
        int stoneY = offsetY + y * cellSize + (cellSize / 2) - (STONE_SIZE / 2);
        
        g.setColor(stoneColor);
        g.fillOval(stoneX, stoneY, STONE_SIZE, STONE_SIZE);
        
        if (stoneColor.equals(WHITE_STONE_COLOR)) {
            g.setColor(Color.BLACK);
            g.drawOval(stoneX, stoneY, STONE_SIZE, STONE_SIZE);
        }
    }
}
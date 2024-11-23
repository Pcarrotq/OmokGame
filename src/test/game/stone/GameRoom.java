package test.game.stone;

import java.util.*;

public class GameRoom {
    private int[][] board;
    private boolean isStart = false;

    public GameRoom(int size) {
        board = new int[size][size];
    }

    public boolean addStone(int[] location, boolean isBlack) {
        int x = location[0];
        int y = location[1];

        if (board[x][y] != 0) {
            return false; // 이미 돌이 놓인 자리
        }

        board[x][y] = isBlack ? 1 : 2; // 1 = 흑, 2 = 백
        return checkWin(x, y, isBlack); // 승리 조건 확인
    }

    public void resetGame() {
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], 0);
        }
        isStart = false;
    }

    public boolean checkWin(int x, int y, boolean isBlack) {
        // 간단한 승리 조건 확인 (5개 연속)
        int target = isBlack ? 1 : 2;
        return checkDirection(x, y, target, 1, 0) || // 수평
               checkDirection(x, y, target, 0, 1) || // 수직
               checkDirection(x, y, target, 1, 1) || // 대각선 \
               checkDirection(x, y, target, 1, -1);  // 대각선 /
    }

    private boolean checkDirection(int x, int y, int target, int dx, int dy) {
        int count = 1;

        for (int step = 1; step < 5; step++) { // 해당 방향으로 4칸 체크
            int nx = x + dx * step;
            int ny = y + dy * step;
            if (nx < 0 || ny < 0 || nx >= board.length || ny >= board.length || board[nx][ny] != target) break;
            count++;
        }

        for (int step = 1; step < 5; step++) { // 반대 방향으로 4칸 체크
            int nx = x - dx * step;
            int ny = y - dy * step;
            if (nx < 0 || ny < 0 || nx >= board.length || ny >= board.length || board[nx][ny] != target) break;
            count++;
        }

        return count >= 5;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }
}
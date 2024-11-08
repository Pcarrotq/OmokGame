package omok.game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseEventHandler extends MouseAdapter {
	private Map map;
	private MapSize ms;
	private DrawBoard d;
	private GUI main;
	
	public MouseEventHandler(Map m, MapSize ms, DrawBoard d, GUI main) {
		map = m;
		this.ms = ms;
		this.d = d;
		this.main = main;
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		super.mousePressed(arg0);
		
		// 입력된 x, y 좌표를 Cell(30)로 나눠 나온 값에 1, 2를 빼 0~19 사이로 맞춤
		int x = (int) Math.round(arg0.getX() / (double) ms.getCell()) - 1;
		int y = (int) Math.round(arg0.getY() / (double) ms.getCell()) - 2;
		
		if (x < 0 || x > 19 || y < 0 || y > 19) {
			return;
		}
		
		if (map.getXY(y,  x) == map.getBlack() || map.getXY(y, x) == map.getWhite()) { // 이미 놓여진 자리일 경우 return
			return;
		}
		
		System.out.println(x + " " + y); // 눌린 좌표값 확인
		map.setMap(y, x);
		map.changeCheck(); // 차례를 바꿈
		d.repaint(); // 맵을 새롭게 다시 그램
		
		if (map.winCheck(x, y)) { // 승리 확인을 하고 승리한 쪽이 나올 경우 팝업창을 띄움
			if (map.getCheck() == true) {
				main.showPopUp("백돌이 승리하였습니다.");
			} else {
				main.showPopUp("흑돌이 승리하였습니다.");
			}
		}
	}
}

package test.game.gui;

public class Map {
    private short[][] map; // 맵의 배열: 1일 때 흑, -1일 때 백, 0일 때 돌이 안 놓여짐
    private static final short BLACK = 1;
    private static final short WHITE = -1;
    private final int CELL = 30;
    private final int SIZE = 20;
    private boolean checkBNW = true; // 흑 차례, 백 차례 확인

    public Map() {
        map = new short[SIZE][SIZE];
    }

    public short getBlack() {
        return BLACK;
    }

    public short getWhite() {
        return WHITE;
    }

    public short getXY(int y, int x) {
        return map[y][x];
    }

    public boolean getCheck() {
        return checkBNW;
    }

    public void changeCheck() {
        checkBNW = !checkBNW;
    }

    public void setMap(int y, int x) {
        // checkBNW를 확인해 true일 때 map에 BLACK, false일 때 WHITE 저장
        if (checkBNW) {
            map[y][x] = BLACK;
        } else {
            map[y][x] = WHITE;
        }
    }

    public int getCell() {
        return CELL;
    }

    public int getSize() {
        return SIZE;
    }

    // 맵을 초기 상태로 되돌리는 메서드 추가
    public void reset() {
        // 모든 맵 셀을 빈 칸으로 설정
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map[i][j] = 0;
            }
        }
        // 차례를 흑돌로 초기화
        checkBNW = true;
    }

    // 승리 확인 로직 (기존 메서드 유지)
    public boolean winCheck(int x, int y) {
        return winCheckL(x, y) || winCheckLD(x, y) || winCheckLU(x, y) || winCheckR(x, y) ||
               winCheckRD(x, y) || winCheckRU(x, y) || winCheckUp(x, y) || winCheckDown(x, y) ||
               winCheckOneDown(x, y) || winCheckOneL(x, y) || winCheckOneLD(x, y) || winCheckOneLU(x, y) ||
               winCheckOneR(x, y) || winCheckOneRD(x, y) || winCheckOneUp(x, y) || winCheckOneRU(x, y) ||
               winCheckCenterLU(x, y) || winCheckCenterRL(x, y) || winCheckCenterRU(x, y) || winCheckCenterUD(x, y);
    }
	
	//위쪽
	public boolean winCheckUp(int x,int y) {
		try {
			for (int i=y;i<y+5;i++){
				if (map[y][x]!=map[i][x])
					return false;
			} 
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	
	//아래쪽
	public boolean winCheckDown(int x,int y) {
		try {
			for (int i=y;i>y-5;i--) {
				if (map[y][x]!=map[i][x])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
		return false;
		}
		return true;
	}
	
	//오른쪽 위 대각선
	public boolean winCheckRU(int x,int y) {
		try {
			for (int i=y, z=x;i>y-5;i--,z++) {
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	//왼쪽 위 대각선
	public boolean winCheckLU(int x,int y) {
		try {
			for (int i=y, z=x;i>y-5;i--,z--) {
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	 }

	//오른쪽
	public boolean winCheckR(int x,int y) {
		try {
			for (int z=x;z<x+5;z++){
				if (map[y][x]!=map[y][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	 }
	
	//왼쪽
	public boolean winCheckL(int x,int y) {
		try {
			for (int z=x;z>x-5;z--) {
				if (map[y][x]!=map[y][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	 }
	
	//오른쪽 아래 대각선
	public boolean winCheckRD(int x,int y) {
		try {
			for (int i=y, z=x;i<y+5;i++,z++){
				if (map[y][x]!=map[i][z]||i>19||z>19||i<0||z<0)
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
		return false;
		}
		return true;
	}

	//왼쪽 아래 대각선
	public boolean winCheckLD(int x,int y) {
		try {
			for (int i=y, z=x;i<y+5;i++,z--) {
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
		return false;
		}
		return true;
	}
	
	//한칸 위쪽
	public boolean winCheckOneUp(int x,int y) {
		try {
			for (int i=y-1;i<y+4;i++) {
				if (map[y][x]!=map[i][x])
					return false;
			}
		} catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		return true;
	}
	
	//한칸 아래쪽
	public boolean winCheckOneDown(int x,int y) {
		try {
			for (int i=y+1;i>y-4;i--) {
				if (map[y][x]!=map[i][x])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	//한칸 오른쪽 위 대각선
	public boolean winCheckOneRU(int x,int y) {
		try {
			for (int i=y+1, z=x-1;i>y-4;i--,z++){
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	//한칸 왼쪽 위 대각선
	public boolean winCheckOneLU(int x,int y) {
		try {
			for (int i=y+1, z=x+1;i>y-4;i--,z--){
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	//한칸 오른쪽
	public boolean winCheckOneR(int x,int y) {
		try {
		for (int z=x-1;z<x+4;z++) {
			if (map[y][x]!=map[y][z])
				return false;
		}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	//한칸 왼쪽
	public boolean winCheckOneL(int x,int y) {
		try {
			for (int z=x+1;z>x-4;z--) {
				if (map[y][x]!=map[y][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}

		return true;
	}

	//한칸 오른쪽 아래 대각선
	public boolean winCheckOneRD(int x,int y) {
		try {
			for (int i=y-1, z=x-1;i<y+4;i++,z++) {
				if (map[y][x]!=map[i][z]||i>19||z>19||i<0||z<0)
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	//한칸 왼쪽 아래 대각선
	public boolean winCheckOneLD(int x,int y) {
		try {
			for (int i=y-1, z=x+1;i<y+4;i++,z--) {
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	//센터 업다운
	public boolean winCheckCenterUD(int x,int y) {
		try {
			for (int i=y-2;i<y+3;i++) {
				if (map[y][x]!=map[i][x])
					return false;
			} 
		} catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
			return true;
	}
	
	//센터 라이트 레프트
	public boolean winCheckCenterRL(int x,int y) {
		try {
			for (int z=x-2;z<x+3;z++) {
				if (map[y][x]!=map[y][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	//센터 라이트 대각선
	public boolean winCheckCenterRU(int x,int y) {
		try {
			for (int i=y+2, z=x-2;i>y-3;i--,z++) {
				if (map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	 
	//센터 레프트 대각선
	public boolean winCheckCenterLU(int x,int y) {
		try {
			for (int i=y+2, z=x+2;i>y-4;i--,z--) {
				if(map[y][x]!=map[i][z])
					return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
}
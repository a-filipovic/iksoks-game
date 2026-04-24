package game;

import java.util.ArrayList;
import java.util.List;

enum PlayerType { Player, Opponent }

public abstract class GameLogic {
    protected int WIDTH = 4;
    protected int HEIGHT = 4;

    static class Field {
        private int x;
        private int y;

        public Field(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }

    protected static class MinMaxRes {
        public float val;
        public int x;

        public MinMaxRes(float val, int x) {
            this.val = val;
            this.x = x;
        }
    }

    protected PlayerType grid[][];
    protected List<Field> winFields;

    public GameLogic() {
        grid = new PlayerType[WIDTH][HEIGHT];
    }

    public int addPlayerMove(int x) {
	//Dodaje potez ljudskog igra?a u kolonu x
        int y = getFirstEmptyY(x);
        if (y == -1) return -1; // kolona puna
        addMove(x, y, PlayerType.Player);
        gameEndedCheck(x, y, true);
        return y;
    }

    public Field getOpponentMove() {
        MinMaxRes move = getMin(-1, 1);
        int x = move.x;

        if (x < 0 || !canPlay(x)) {
            for (int i = 0; i < WIDTH; i++) {
                if (canPlay(i)) {
                    x = i;
                    break;
                }
            }
        }
        
        if (x < 0 || !canPlay(x)) return null; // nema gde da igra

        int y = addMove(x, PlayerType.Opponent);
        gameEndedCheck(x, y, true);
        return new Field(x, y);
    }


    public boolean canPlay(int x) {
        return grid[x][0] == null;
    }

    public List<Field> getWinFields() {
        return winFields;
    }

    public boolean isGameEnded() {
        return winFields != null;
    }

    protected int getFirstEmptyY(int x) {
        if (!canPlay(x)) return -1;
        int y = HEIGHT - 1;
        while (y >= 0 && grid[x][y] != null) y--;
        return y;
    }

    protected int addMove(int x, PlayerType pt) {
        int y = getFirstEmptyY(x);
        if (y == -1) return -1; // kolona puna
        addMove(x, y, pt);
        return y;
    }

    protected void addMove(int x, int y, PlayerType pt) {
        if (y >= 0 && y < HEIGHT) {
            grid[x][y] = pt;
        }
    }

    protected abstract MinMaxRes getMax(float alpha, float beta);
    protected abstract MinMaxRes getMin(float alpha, float beta);

    protected boolean noMoreMoves() {
        for (int x = 0; x < WIDTH; x++) {
            if (canPlay(x)) return false;
        }
        return true;
    }

    protected boolean gameEndedCheck(int x, int y) {
        return gameEndedCheck(x, y, false);
    }

    protected boolean gameEndedCheck(int x, int y, boolean save) {
        return gameEndedHorizontally(x, y, save) ||
               gameEndedVertically(x, y, save) ||
               gameEndedDiagonallyLeft(x, y, save) ||
               gameEndedDiagonallyRight(x, y, save);
    }

    protected boolean gameEndedHorizontally(int x, int y, boolean save) {
        return gameEnded(x, y, 1, 0, save);
    }

    protected boolean gameEndedVertically(int x, int y, boolean save) {
        return gameEnded(x, y, 0, 1, save);
    }

    protected boolean gameEndedDiagonallyLeft(int x, int y, boolean save) {
        return gameEnded(x, y, 1, -1, save);
    }

    protected boolean gameEndedDiagonallyRight(int x, int y, boolean save) {
        return gameEnded(x, y, 1, 1, save);
    }

    protected boolean gameEnded(int x, int y, int xDir, int yDir, boolean save) {
        PlayerType pt = grid[x][y];
        ArrayList<Field> fields = new ArrayList<>(4);
        fields.add(new Field(x, y));
        for (int cnt = 0; cnt < 2; cnt++) {
            xDir *= -1;
            yDir *= -1;
            int i = x + xDir;
            int j = y + yDir;
            while (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT) {
                if (grid[i][j] == pt) {
                    fields.add(new Field(i, j));
                    if (fields.size() == 4) {
                        if (save) winFields = fields;
                        return true;
                    }
                } else {
                    break;
                }
                i += xDir;
                j += yDir;
            }
        }
        return false;
    }
}

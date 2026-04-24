package game;

public class GameLogic5x5 extends GameLogic {
    private static final int MAX_DEPTH = 6;
    protected boolean testFullSearch = false;

    public GameLogic5x5() {
        super();
        WIDTH = 5;         
        HEIGHT = 5;        
        grid = new PlayerType[WIDTH][HEIGHT]; 
    }

    @Override
    protected MinMaxRes getMax(float alpha, float beta) {
        return getMax(alpha, beta, 1);
    }

    @Override
    protected MinMaxRes getMin(float alpha, float beta) {
        return getMin(alpha, beta, 1);
    }

    protected MinMaxRes getMax(float alpha, float beta, int depth) {
        MinMaxRes res = new MinMaxRes(-1, -1);
        for (int x = 0; x < WIDTH && res.val < beta; x++) {
            if (canPlay(x)) {
                int y = addMove(x, PlayerType.Player);
                if (y == -1) continue; // kolona puna, preskoči

                float val;
                if (gameEndedCheck(x, y)) val = 1f;
                else if (noMoreMoves()) val = 0;
                else if (!testFullSearch && depth >= MAX_DEPTH) val = evaluateState();
                else val = getMin(alpha, beta, depth + 1).val;

                if (val > res.val) {
                    res.val = val;
                    res.x = x;
                    alpha = Math.max(alpha, val);
                }
                addMove(x, y, null); // undo poteza
            }
        }
        return res;
    }

    protected MinMaxRes getMin(float alpha, float beta, int depth) {
        MinMaxRes res = new MinMaxRes(1, -1);
        for (int x = 0; x < WIDTH && res.val > alpha; x++) {
            if (canPlay(x)) {
                int y = addMove(x, PlayerType.Opponent);
                if (y == -1) continue; // kolona puna, preskoči

                float val;
                if (gameEndedCheck(x, y)) val = -1f;
                else if (noMoreMoves()) val = 0;
                else if (!testFullSearch && depth >= MAX_DEPTH) val = evaluateState();
                else val = getMax(alpha, beta, depth + 1).val;

                if (val < res.val) {
                    res.val = val;
                    res.x = x;
                    beta = Math.min(beta, val);
                }
                addMove(x, y, null); // undo poteza
            }
        }
        return res;
    }

    private float evaluateState() {
        float score = 0;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (grid[x][y] == PlayerType.Player) score += countPotential(x, y, PlayerType.Player);
                if (grid[x][y] == PlayerType.Opponent) score -= countPotential(x, y, PlayerType.Opponent);
            }
        }
        return score;
    }

    private int countPotential(int x, int y, PlayerType pt) {
        int total = 0;
        int[][] dirs = {{1,0},{0,1},{1,1},{1,-1}};
        for (int[] d : dirs) {
            int cnt = 1;
            for (int s = 1; s < 4; s++) {
                int nx = x + d[0]*s;
                int ny = y + d[1]*s;
                if (nx < 0 || ny < 0 || nx >= WIDTH || ny >= HEIGHT) break;
                if (grid[nx][ny] == pt) cnt++;
                else if (grid[nx][ny] != null) break;
            }
            total += cnt;
        }
        return total;
    }
}

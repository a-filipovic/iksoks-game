package games.inverse;

/**
 * Inverse tic-tac-toe: whoever gets three in a row loses.
 */
public class InverseTicTacToeLogic {

    private InversePlayer[][] board;
    private int movesCount = 0;
    private boolean finished;
    private InversePlayer winner;

    public InverseTicTacToeLogic() {
        reset();
    }

    public void reset() {
        board = new InversePlayer[3][3];
        movesCount = 0;
        finished = false;
        winner = null;
    }

    public InversePlayer getCell(int x, int y) {
        return board[x][y];
    }

    public boolean playPersonMove(int x, int y) {
        if (finished) return false;
        boolean ok = setBoardField(x, y, InversePlayer.Person);
        if (ok) {
            terminateGameIfNeeded(x, y);
        }
        return ok;
    }

    public void playComputerMove() {
        if (finished) return;

        int bestX = -1;
        int bestY = -1;
        float alpha = -1;
        float beta = 1;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (board[x][y] == null) {
                    setBoardField(x, y, InversePlayer.Computer);
                    float val = simulatePersonMoveAlphaBeta(alpha, beta, 1);
                    if (val > alpha) {
                        alpha = val;
                        bestX = x;
                        bestY = y;
                    }
                    setBoardField(x, y, null);
                }
            }
        }

        if (bestX != -1) {
            setBoardField(bestX, bestY, InversePlayer.Computer);
            terminateGameIfNeeded(bestX, bestY);
        }
    }

    private float simulatePersonMoveAlphaBeta(float alpha, float beta, int r) {
        float res = 1;
        boolean done = false;

        for (int x = 0; x < 3 && !done; x++) {
            for (int y = 0; y < 3; y++) {
                if (board[x][y] == null) {
                    setBoardField(x, y, InversePlayer.Person);
                    float val;
                    if (threeInLine(x, y)) val = 1f / r;
                    else if (movesCount == 9) val = 0;
                    else val = simulateComputerMoveAlphaBeta(alpha, beta, r + 1);

                    if (val < res) res = val;
                    setBoardField(x, y, null);

                    if (res <= alpha) {
                        done = true;
                        break;
                    }
                    beta = Math.min(beta, res);
                }
            }
        }
        return res;
    }

    private float simulateComputerMoveAlphaBeta(float alpha, float beta, int r) {
        float res = -1;
        boolean done = false;

        for (int x = 0; x < 3 && !done; x++) {
            for (int y = 0; y < 3; y++) {
                if (board[x][y] == null) {
                    setBoardField(x, y, InversePlayer.Computer);
                    float val;
                    if (threeInLine(x, y)) val = -1f / r;
                    else if (movesCount == 9) val = 0;
                    else val = simulatePersonMoveAlphaBeta(alpha, beta, r + 1);

                    if (val > res) res = val;
                    setBoardField(x, y, null);

                    if (res >= beta) {
                        done = true;
                        break;
                    }
                    alpha = Math.max(alpha, res);
                }
            }
        }
        return res;
    }

    public boolean isFinished() {
        return finished;
    }

    public InversePlayer getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        String[] rows = new String[3];
        for (int i = 0; i < 3; i++) {
            String[] cols = new String[3];
            for (int j = 0; j < 3; j++) {
                InversePlayer p = board[i][j];
                cols[j] = " " + (p == InversePlayer.Person ? "P"
                        : (p == InversePlayer.Computer ? "C" : " ")) + " ";
            }
            rows[i] = String.join("|", cols) + '\n';
        }

        return String.join("---|---|---\n", rows);
    }

    private boolean setBoardField(int x, int y, InversePlayer p) {
        if (x < 0 || x >= 3 || y < 0 || y >= 3) return false;
        if (board[x][y] != null && p != null) return false;
        board[x][y] = p;
        movesCount += p == null ? -1 : 1;
        return true;
    }

    private void terminateGameIfNeeded(int x, int y) {
        if (threeInLine(x, y)) {
            finished = true;
            winner = board[x][y] == InversePlayer.Computer ? InversePlayer.Person : InversePlayer.Computer;
        } else if (noMoreMoves()) {
            finished = true;
        }
    }

    private boolean threeInLine(int x, int y) {
        return board[0][y] != null && board[0][y] == board[1][y] && board[0][y] == board[2][y]
                || board[x][0] != null && board[x][0] == board[x][1] && board[x][0] == board[x][2]
                || x == y && board[0][0] != null && board[0][0] == board[1][1] && board[0][0] == board[2][2]
                || x + y == 2 && board[0][2] != null && board[0][2] == board[1][1] && board[0][2] == board[2][0];
    }

    private boolean noMoreMoves() {
        return movesCount == 9;
    }
}

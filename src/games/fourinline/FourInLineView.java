package games.fourinline;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
public class FourInLineView implements common.GameShell {
    private static final int SIZE = 500;
    private static final float CIRCLE_PADDING = 0.1f;
    private static final Color PLAYER_COLOR = Color.FIREBRICK;
    private static final Color OPPONENT_COLOR = Color.DARKSLATEBLUE;
    private static final Color WINNING_BG_COLOR = Color.DARKSEAGREEN;
    private static final Color GAME_OVER_BG_COLOR = Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.5);

    private final BorderPane root = new BorderPane();
    private final GridPane gridPane = new GridPane();
    private final Button backButton = new Button("Back to menu");

    private FourInLine5x5Logic grid;
    private GraphicsContext[][] gcs;
    private float fieldSize;
    private boolean gameOver;
    private Runnable showMenuAction;

    public FourInLineView() {
        gridPane.setPadding(new Insets(8));
        root.setCenter(gridPane);

        HBox bottom = new HBox(8);
        bottom.setPadding(new Insets(8));
        backButton.setOnAction(e -> {
            if (showMenuAction != null) {
                showMenuAction.run();
            }
        });
        bottom.getChildren().add(backButton);
        root.setBottom(bottom);

        buildBoard();
    }

    /**
     * @param onGameEnd invoked with end message when a round finishes; host should show {@link EndGameDialog}.
     */
    public void setOnGameEnd(Consumer<String> onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    private Consumer<String> onGameEnd = msg -> {
    };

    /** Allows "Back to menu" without waiting for game end. */
    public void setShowMenuAction(Runnable showMenuAction) {
        this.showMenuAction = showMenuAction;
    }

    @Override
    public BorderPane getRoot() {
        return root;
    }

    private void buildBoard() {
        grid = new FourInLine5x5Logic();
        grid.testFullSearch = true;
        gameOver = false;

        gridPane.getChildren().clear();
        gcs = new GraphicsContext[grid.WIDTH][grid.HEIGHT];
        fieldSize = (float) SIZE / grid.WIDTH;

        for (int j = 0; j < grid.HEIGHT; j++) {
            for (int i = 0; i < grid.WIDTH; i++) {
                Canvas cellCanvas = new Canvas(fieldSize, fieldSize);
                final int col = i;
                cellCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> playMove(col));
                gcs[i][grid.HEIGHT - 1 - j] = cellCanvas.getGraphicsContext2D();
                gridPane.add(cellCanvas, i, j);
            }
        }
        drawGridLines();
    }

    @Override
    public void newGame() {
        buildBoard();
    }

    private void drawGridLines() {
        for (int i = 0; i < gcs.length; i++) {
            for (int j = 0; j < gcs[0].length; j++) {
                GraphicsContext gc = gcs[i][j];
                gc.setStroke(Color.BLACK);
                gc.strokeRect(0, 0, fieldSize, fieldSize);
            }
        }
    }

    private void playMove(int x) {
        if (gameOver || !grid.canPlay(x) || grid.isGameEnded()) {
            return;
        }

        int y = grid.addPlayerMove(x);
        if (y == -1) {
            return;
        }
        drawCircle(x, y, PLAYER_COLOR);

        if (grid.isGameEnded()) {
            drawWin(PLAYER_COLOR);
            finishRound("You win! (four in a row)");
            return;
        }

        if (grid.noMoreMoves()) {
            finishRound("Draw — no more moves.");
            return;
        }

        grid.testFullSearch = true;
        FourInLineLogic.Field field = grid.getOpponentMove();
        grid.testFullSearch = false;

        if (field != null) {
            drawCircle(field.getX(), field.getY(), OPPONENT_COLOR);

            if (grid.isGameEnded()) {
                drawWin(OPPONENT_COLOR);
                finishRound("AI wins! (four in a row)");
            } else if (grid.noMoreMoves()) {
                finishRound("Draw — no more moves.");
            }
        } else {
            finishRound("Draw — AI cannot move.");
        }
    }

    private void finishRound(String message) {
        gameOver = true;
        showGameOverVisual();
        onGameEnd.accept(message);
    }

    private void showGameOverVisual() {
        for (int i = 0; i < gcs.length; i++) {
            for (int j = 0; j < gcs[0].length; j++) {
                GraphicsContext gc = gcs[i][j];
                gc.setFill(GAME_OVER_BG_COLOR);
                gc.fillRect(0, 0, fieldSize, fieldSize);
            }
        }

        List<FourInLineLogic.Field> fields = grid.getWinFields();
        if (fields != null) {
            for (FourInLineLogic.Field field : fields) {
                Color c = (grid.grid[field.getX()][field.getY()] == PlayerType.Player)
                        ? PLAYER_COLOR
                        : OPPONENT_COLOR;
                drawCircle(field.getX(), field.getY(), c);
            }
        }
    }

    private void drawCircle(int x, int y, Color c) {
        int displayY = grid.HEIGHT - 1 - y;
        GraphicsContext gc = gcs[x][displayY];

        gc.clearRect(0, 0, fieldSize, fieldSize);

        float padding = fieldSize * CIRCLE_PADDING;
        float circleSize = fieldSize - 2 * padding;
        gc.setFill(c);
        gc.fillOval(padding, padding, circleSize, circleSize);
    }

    private void drawWinningBackground(int x, int y) {
        int displayY = grid.HEIGHT - 1 - y;
        GraphicsContext gc = gcs[x][displayY];
        gc.setFill(WINNING_BG_COLOR);
        gc.fillRect(0, 0, fieldSize, fieldSize);
    }

    private void drawWin(Color c) {
        List<FourInLineLogic.Field> fields = grid.getWinFields();
        if (fields == null) return;
        for (FourInLineLogic.Field field : fields) {
            drawWinningBackground(field.getX(), field.getY());
            drawCircle(field.getX(), field.getY(), c);
        }
    }
}

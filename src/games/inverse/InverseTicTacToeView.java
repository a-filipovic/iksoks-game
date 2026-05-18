package games.inverse;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class InverseTicTacToeView implements common.GameShell {

    private static final double BTN_PREF = 72;

    private final BorderPane root = new BorderPane();
    private final GridPane grid = new GridPane();
    private final Label status = new Label("Inverse tic-tac-toe: avoid three in a row. Your turn (P).");
    private final Button backButton = new Button("Back to menu");

    private InverseTicTacToeLogic logic;
    private Button[][] buttons;
    private Runnable showMenuAction;
    private Consumer<String> onGameEnd = msg -> {
    };

    public InverseTicTacToeView() {
        logic = new InverseTicTacToeLogic();
        configureLayout();
        newGame();
    }

    public void setOnGameEnd(Consumer<String> onGameEnd) {
        if (onGameEnd != null) {
            this.onGameEnd = onGameEnd;
        }
    }

    public void setShowMenuAction(Runnable showMenuAction) {
        this.showMenuAction = showMenuAction;
    }

    private void configureLayout() {
        root.setPadding(new Insets(12));
        status.setWrapText(true);
        root.setTop(status);

        grid.setHgap(6);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 0, 8, 0));
        root.setCenter(grid);

        HBox bottom = new HBox(8);
        bottom.setPadding(new Insets(8, 0, 0, 0));
        backButton.setOnAction(e -> {
            if (showMenuAction != null) {
                showMenuAction.run();
            }
        });
        bottom.getChildren().add(backButton);
        root.setBottom(bottom);
    }

    @Override
    public BorderPane getRoot() {
        return root;
    }

    @Override
    public void newGame() {
        logic.reset();
        status.setText("Inverse tic-tac-toe: avoid three in a row. Your turn (P).");
        rebuildGridButtons();
        refreshSymbols();
        setInteractable(!logic.isFinished());
    }

    private void rebuildGridButtons() {
        grid.getChildren().clear();
        buttons = new Button[3][3];

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button b = new Button(" ");
                b.setPrefSize(BTN_PREF, BTN_PREF);
                b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(b, Priority.ALWAYS);
                GridPane.setVgrow(b, Priority.ALWAYS);

                final int row = r;
                final int col = c;
                b.setOnAction(e -> handleCellClick(row, col));

                buttons[r][c] = b;
                grid.add(b, col, row);
            }
        }
    }

    private void setInteractable(boolean enabled) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c].setDisable(!enabled);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (logic.isFinished()) {
            return;
        }

        if (!logic.playPersonMove(row, col)) {
            status.setText("Invalid move — cell taken or game over.");
            return;
        }

        refreshSymbols();

        if (logic.isFinished()) {
            setInteractable(false);
            onGameEnd.accept(endMessage());
            return;
        }

        status.setText("Computer thinking…");
        setInteractable(false);

        logic.playComputerMove();
        refreshSymbols();

        if (logic.isFinished()) {
            setInteractable(false);
            onGameEnd.accept(endMessage());
        } else {
            status.setText("Your turn (P). Avoid three in a row.");
            setInteractable(true);
        }
    }

    private void refreshSymbols() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                InversePlayer p = logic.getCell(r, c);
                String text;
                if (p == InversePlayer.Person) {
                    text = "P";
                } else if (p == InversePlayer.Computer) {
                    text = "C";
                } else {
                    text = " ";
                }
                buttons[r][c].setText(text);
            }
        }
    }

    private String endMessage() {
        InversePlayer w = logic.getWinner();
        if (w == null) {
            return "Draw — board full with no three-in-a-row loser.";
        }
        if (w == InversePlayer.Person) {
            return "You win! (Opponent made three in a row.)";
        }
        return "Computer wins! (You made three in a row.)";
    }
}

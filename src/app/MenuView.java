package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MenuView {

    private final VBox root = new VBox(16);
    private Runnable onFourInLine;
    private Runnable onInverse;

    public MenuView() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Label title = new Label("X-O variants");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Choose a game:");
        subtitle.setStyle("-fx-font-size: 14px;");

        Button four = new Button("Four in Line (5×5)");
        four.setPrefWidth(280);
        four.setOnAction(e -> {
            if (onFourInLine != null) {
                onFourInLine.run();
            }
        });

        Button inv = new Button("Inverse Tic-Tac-Toe (3×3)");
        inv.setPrefWidth(280);
        inv.setOnAction(e -> {
            if (onInverse != null) {
                onInverse.run();
            }
        });

        root.getChildren().addAll(title, subtitle, four, inv);
    }

    public void setOnFourInLine(Runnable onFourInLine) {
        this.onFourInLine = onFourInLine;
    }

    public void setOnInverse(Runnable onInverse) {
        this.onInverse = onInverse;
    }

    public VBox getRoot() {
        return root;
    }
}

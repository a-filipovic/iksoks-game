package common;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 * Shows a consistent end-of-game dialog with Play again / Back to menu.
 */
public final class EndGameDialog {

    public enum Choice {
        PLAY_AGAIN,
        BACK_TO_MENU
    }

    private EndGameDialog() {
    }

    public static Choice show(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType playAgain = new ButtonType("Play again");
        ButtonType backToMenu = new ButtonType("Back to menu");
        alert.getButtonTypes().setAll(playAgain, backToMenu);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == backToMenu) {
            return Choice.BACK_TO_MENU;
        }
        return Choice.PLAY_AGAIN;
    }
}

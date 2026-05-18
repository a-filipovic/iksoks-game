package app;

import common.EndGameDialog;
import games.fourinline.FourInLineView;
import games.inverse.InverseTicTacToeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("X-O variants");
        showMenu();
        primaryStage.show();
    }

    private void showMenu() {
        MenuView menu = new MenuView();
        menu.setOnFourInLine(this::openFourInLine);
        menu.setOnInverse(this::openInverse);
        primaryStage.setScene(new Scene(menu.getRoot(), 420, 320));
    }

    private void openFourInLine() {
        FourInLineView view = new FourInLineView();
        view.setShowMenuAction(this::showMenu);
        view.setOnGameEnd(msg -> {
            EndGameDialog.Choice choice = EndGameDialog.show(primaryStage, "Game over", msg);
            if (choice == EndGameDialog.Choice.BACK_TO_MENU) {
                showMenu();
            } else {
                view.newGame();
            }
        });
        primaryStage.setScene(new Scene(view.getRoot(), 540, 640));
    }

    private void openInverse() {
        InverseTicTacToeView view = new InverseTicTacToeView();
        view.setShowMenuAction(this::showMenu);
        view.setOnGameEnd(msg -> {
            EndGameDialog.Choice choice = EndGameDialog.show(primaryStage, "Game over", msg);
            if (choice == EndGameDialog.Choice.BACK_TO_MENU) {
                showMenu();
            } else {
                view.newGame();
            }
        });
        primaryStage.setScene(new Scene(view.getRoot(), 420, 420));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

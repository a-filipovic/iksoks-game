package game;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FourInLine5x5 extends Application {
    private static final int SIZE = 500;
    private static final float CIRCLE_PADDING = 0.1f;
    private static final Color PLAYER_COLOR = Color.FIREBRICK;
    private static final Color OPPONENT_COLOR = Color.DARKSLATEBLUE;
    private static final Color WINNING_BG_COLOR = Color.DARKSEAGREEN;
    private static final Color GAME_OVER_BG_COLOR = Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.5);


    private GameLogic5x5 grid;
    private GraphicsContext[][] gcs;
    private float fieldSize;

    @Override
    public void start(Stage primaryStage) throws Exception {
        grid = new GameLogic5x5();
        grid.testFullSearch = true; // uključuje punu pretragu za testiranje

        gcs = new GraphicsContext[grid.WIDTH][grid.HEIGHT];

        fieldSize = (float) SIZE / grid.WIDTH;
        GridPane gridPane = new GridPane();

        for (int j = 0; j < grid.HEIGHT; j++) {
            for (int i = 0; i < grid.WIDTH; i++) {
            	final Canvas cellCanvas = new Canvas(fieldSize, fieldSize);
                final int iF = i;
                cellCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> playMove(iF));
                gcs[i][grid.HEIGHT - 1 - j] = cellCanvas.getGraphicsContext2D();
                gridPane.add(cellCanvas, i, j);
            }
        }

        drawGridLines();

        Scene scene = new Scene(new Pane(gridPane), SIZE, fieldSize * grid.HEIGHT);
        primaryStage.setTitle("Four in Line 5x5");
        primaryStage.setScene(scene);
        primaryStage.show();
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
        // Provera da li igrač može da odigra potez ili je igra već završena
        if (!grid.canPlay(x) || grid.isGameEnded())
            return;

        // Odigraj ljudski potez
        int y = grid.addPlayerMove(x);
        if (y == -1) { 
            System.out.println("Ovo polje je popunjeno! Odaberi drugo.");
            return;
        }
        drawCircle(x, y, PLAYER_COLOR);

        // Provera da li je ljudski potez završio igru
        if (grid.isGameEnded()) {
            drawWin(PLAYER_COLOR);
            showGameOver("Igrač je pobedio!");
            return;
        }

        // Provera da li nema više slobodnih poteza
        if (grid.noMoreMoves()) {
            showGameOver("Nerešeno! Nema više poteza.");
            return;
        }

       
        
     
     // Odigraj AI potez samo ako ima slobodnog polja 
        grid.testFullSearch = true;  // omogućava full search
        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        System.gc();  // očisti nepotrebnu memoriju pre merenja
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // izvrši AI potez i sačuvaj rezultat
        GameLogic.Field field = grid.getOpponentMove();

        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long endTime = System.nanoTime();

        System.out.println("AI potez trajao: " + (endTime - startTime)/1e6 + " ms");
        System.out.println("Memorija korišćena: " + Math.max(0, (usedMemoryAfter - usedMemoryBefore))/1024 + " KB");
        
        grid.testFullSearch = false; // vrati nazad na normalno ponašanje
        
        

     // crtaj potez na UI
        if (field != null) {
        	drawCircle(field.getX(), field.getY(), OPPONENT_COLOR);

            // Provera da li AI pobedio
            if (grid.isGameEnded()) {
                drawWin(OPPONENT_COLOR);
                showGameOver("AI je pobedio!");
            } 
            // Provera da li nema više slobodnih poteza nakon AI poteza
            else if (grid.noMoreMoves()) {
                showGameOver("Nerešeno! Nema više poteza.");
            }

        } else {
            // Ovo se sada neće desiti dok ima slobodnih polja
            showGameOver("Nerešeno! AI ne može da odigra potez.");
        }
    }

    private void showGameOver(String message) {
        System.out.println(message); // privremeno u konzoli

        // Zamuti celu tablu
        for (int i = 0; i < gcs.length; i++) {
            for (int j = 0; j < gcs[0].length; j++) {
                GraphicsContext gc = gcs[i][j];
                gc.setFill(GAME_OVER_BG_COLOR);
                gc.fillRect(0, 0, fieldSize, fieldSize);
            }
        }

        // Ponovo iscrtaj pobedničke krugove
        List<GameLogic.Field> fields = grid.getWinFields();
        if (fields != null) {
            for (GameLogic.Field field : fields) {
                Color c = (grid.grid[field.getX()][field.getY()] == PlayerType.Player) ? PLAYER_COLOR : OPPONENT_COLOR;
                drawCircle(field.getX(), field.getY(), c);
            }
        }
    }



    private void drawCircle(int x, int y, Color c) {
    	
    	int displayY = grid.HEIGHT - 1 - y;
    	GraphicsContext gc = gcs[x][displayY];
        
     // Ovo osvežava Canvas pre crtanja novog kruga
        gc.clearRect(0, 0, fieldSize, fieldSize); 
        
        float padding = fieldSize * CIRCLE_PADDING;
        float size = fieldSize - 2 * padding;
        gc.setFill(c);
        gc.fillOval(padding, padding, size, size);
    }

    private void drawWinningBackground(int x, int y) {
    	
    	int displayY = grid.HEIGHT - 1 - y;
    	GraphicsContext gc = gcs[x][displayY];
        gc.setFill(WINNING_BG_COLOR);
        gc.fillRect(0, 0, fieldSize, fieldSize);
    }

    private void drawWin(Color c) {
        List<GameLogic.Field> fields = grid.getWinFields();
        if (fields == null) return;
        for (GameLogic.Field field : fields) {
            
        	drawWinningBackground(field.getX(),field.getY());
        	drawCircle(field.getX(), field.getY(), c);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

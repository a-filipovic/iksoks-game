package common;

import javafx.scene.Parent;

/**
 * Optional contract for embedded game panels.
 */
public interface GameShell {

    Parent getRoot();

    /** Start a fresh match (same rules, cleared board/state). */
    void newGame();
}

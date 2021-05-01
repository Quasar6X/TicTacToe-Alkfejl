package hu.alkfejl.layout;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Location aware StackPane so I can place them in a gridlayout.
 * Also add a Label to render text on.
 */
public final class Tile extends StackPane {

    public final Label text = new Label();
    public final int x, y;

    public Tile(final int x, final int y) {
        text.getStyleClass().add("btnText");
        this.x = x;
        this.y = y;
        this.getStyleClass().add("btnTile");
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(text);
    }
}

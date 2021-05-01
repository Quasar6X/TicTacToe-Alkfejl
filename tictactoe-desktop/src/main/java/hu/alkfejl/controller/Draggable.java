package hu.alkfejl.controller;

import javafx.scene.Node;
import javafx.stage.Stage;

public interface Draggable {

    /**
     * Normally, I'd do this in a constructor of a super-class, but because of FXML field injection
     * the constructor is useless because the rootNode reference is null at constructor call time.
     * This is horrible but I can't think of a better way ¯\_(ツ)_/¯
     *
     * @param stage the stage the rootNode belongs to
     * @param rootNode the node by which the window will be draggable
     */
    default void setupDragEvent(final Stage stage, final Node rootNode) {
        if (rootNode == null || stage == null)
            throw new IllegalArgumentException("Neither stage, nor rootNode can be null!");
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];
        rootNode.setOnMousePressed(event -> {
            xOffset[0] = stage.getX() - event.getScreenX();
            yOffset[0] = stage.getY() - event.getScreenY();
        });
        rootNode.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset[0]);
            stage.setY(event.getScreenY() + yOffset[0]);
        });
    }
}

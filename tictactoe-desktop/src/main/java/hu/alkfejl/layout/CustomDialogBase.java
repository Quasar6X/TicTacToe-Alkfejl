package hu.alkfejl.layout;

import hu.alkfejl.App;
import hu.alkfejl.controller.Draggable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomDialogBase<T> extends Dialog<T> implements Draggable {

    public CustomDialogBase() {
        super();
        getDialogPane().getStylesheets().add(App.GLOBAL_CSS);
        var stage = ((Stage) getDialogPane().getScene().getWindow());
        stage.getIcons().add(App.ICON);
        stage.initStyle(StageStyle.UNDECORATED);
        setupDragEvent(stage, getDialogPane());
        setupDragEvent(stage, getDialogPane().getChildren().filtered(node -> node instanceof ButtonBar).stream().findFirst().orElse(null));
    }
}

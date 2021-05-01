package hu.alkfejl.controller;

import hu.alkfejl.App;
import hu.alkfejl.layout.CustomDialogBase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public final class WelcomeScreenController implements Initializable, Draggable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDragEvent(App.getStage(), rootPane);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------FXML References and Methods-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @FXML
    private StackPane rootPane;

    @FXML
    private void handleExit() {
        var alert = new CustomDialogBase<ButtonType>();
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit dialog");
        alert.setHeaderText("Do you really want to exit?");
        alert.showAndWait().filter(response -> response == ButtonType.YES).ifPresent(response -> Platform.exit());
    }

    @FXML
    private void handlePlay() {
        App.loadFXML(App.FXML.PLAYER);
    }

    @FXML
    private void handleMatchHistory() {
        App.loadFXML(App.FXML.MATCH_HISTORY);
    }
}

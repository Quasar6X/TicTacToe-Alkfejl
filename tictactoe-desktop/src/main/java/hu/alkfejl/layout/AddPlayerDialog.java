package hu.alkfejl.layout;

import hu.alkfejl.model.Player;
import hu.alkfejl.model.dao.SimplePlayerDAO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public final class AddPlayerDialog extends CustomDialogBase<String> {

    @SuppressWarnings("unchecked")
    public AddPlayerDialog() {
        super();

        setTitle("Add new player dialog");
        setHeaderText("Please choose a name...");

        var task = new Task<List<Player>>() {
            @Override
            protected List<Player> call() {
                return SimplePlayerDAO.getInstance().findAll();
            }
        };
        var thread = new Thread(task);
        thread.start();

        final var players = new ArrayList<Player>();
        task.setOnSucceeded(event ->
                players.addAll((ArrayList<Player>) event.getSource().getValue()) // PLS PROJECT VALHALLA KILL TYPE ERASURE
        );

        var regexLabelMatches = new SimpleBooleanProperty(false);

        var nameTextField = new TextField();
        var regexLabel = new Label();
        nameTextField.setPromptText("Please enter a username");
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!players.isEmpty() && newValue != null) {
                for (var player : players)
                    if (player.getName().equalsIgnoreCase(newValue)) {
                        regexLabel.setText("Name is already taken!");
                        regexLabel.setStyle("-fx-text-fill: red");
                        regexLabelMatches.set(false);
                        return;
                    }

                regexLabel.setText("OK!");
                regexLabel.setStyle("-fx-text-fill: -fx-accent");
                regexLabelMatches.set(true);
            }
        });

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(new VBox(8, nameTextField, regexLabel));

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
                return nameTextField.getText();
            return null;
        });

        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(nameTextField.textProperty().isEmpty()
                .or(regexLabelMatches.not()));
    }
}

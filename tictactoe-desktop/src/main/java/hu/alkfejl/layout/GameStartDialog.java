package hu.alkfejl.layout;

import hu.alkfejl.model.Player;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.SimplePlayerDAO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public final class GameStartDialog extends CustomDialogBase<GameStartDialog.GameOptions> {

    public GameStartDialog() {
        super();
        var strConverter = new StringConverter<Player>() {
            @Override
            public String toString(Player player) {
                return player.getName();
            }

            @Override
            public Player fromString(String string) {
                return null;
            }
        };

        setTitle("Game dialog");
        setHeaderText("Please specify...");

        var labelForSlider = new Label("Board size: ");
        var labelForSlider2 = new Label();
        var slider = new Slider(3, 15, 5);
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.valueProperty().addListener((listener, oldVal, newVal) -> slider.setValue(newVal.intValue()));
        labelForSlider2.textProperty().bind(slider.valueProperty().asString());
        labelForSlider2.setStyle("-fx-text-fill: -fx-accent");

        var players = SimplePlayerDAO.getInstance().findAll();

        var choiceBox1 = new ChoiceBox<Player>();
        choiceBox1.getItems().setAll(players.stream().filter(p -> p.getId() != 1).collect(Collectors.toList()));
        choiceBox1.getSelectionModel().selectFirst();
        choiceBox1.setConverter(strConverter);
        choiceBox1.setMaxWidth(Double.MAX_VALUE);

        var choiceBox2 = new ChoiceBox<Player>();
        choiceBox2.getItems().setAll(players);
        choiceBox2.getSelectionModel().selectFirst();
        choiceBox2.setConverter(strConverter);
        choiceBox2.setMaxWidth(Double.MAX_VALUE);

        var pattern = "^(?:[01]\\d|2[0123]):([012345]\\d):([012345]\\d)$";
        var label1Matches = new SimpleBooleanProperty(true);
        var label2Matches = new SimpleBooleanProperty(true);

        var turnTimeTextField = new TextField();
        var turnTimeLabel = new Label();
        turnTimeTextField.setText("00:00:30");
        turnTimeTextField.setPromptText("Format: HH:mm:ss");
        turnTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches(pattern)) {
                turnTimeLabel.setText("REQUIRED");
                turnTimeLabel.setStyle("-fx-text-fill: red");
                label1Matches.set(false);
            }
            else {
                turnTimeLabel.setText("OK!");
                turnTimeLabel.setStyle("-fx-text-fill: -fx-accent");
                label1Matches.set(true);
            }
        });

        var gameTimeTextField = new TextField();
        var gameTimeLabel = new Label();
        gameTimeTextField.setText("00:10:00");
        gameTimeTextField.setPromptText("Format: HH:mm:ss");
        gameTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches(pattern)) {
                gameTimeLabel.setText("REQUIRED");
                gameTimeLabel.setStyle("-fx-text-fill: red");
                label2Matches.set(false);
            }
            else {
                gameTimeLabel.setText("OK!");
                gameTimeLabel.setStyle("-fx-text-fill: -fx-accent");
                label2Matches.set(true);
            }
        });

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(new VBox(8, new HBox(labelForSlider, labelForSlider2), slider, new Label("Player 1"),
                choiceBox1, new Label("Player 2"), choiceBox2, new Label("Time it takes for a turn"), turnTimeTextField, turnTimeLabel,
                new Label("Time it takes for a game"), gameTimeTextField, gameTimeLabel));

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
                return new GameOptions(((int) slider.getValue()), choiceBox1.getValue(), choiceBox2.getValue(),
                        LocalTime.parse(turnTimeTextField.getText(), DateTimeFormatter.ISO_TIME).toSecondOfDay(),
                        LocalTime.parse(gameTimeTextField.getText(), DateTimeFormatter.ISO_TIME).toSecondOfDay());
            return null;
        });

        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(choiceBox1.valueProperty().isNull()
                .or(choiceBox2.valueProperty().isNull()
                        .or(turnTimeTextField.textProperty().isEmpty()
                                .or(gameTimeTextField.textProperty().isEmpty()
                                    .or(label1Matches.not()
                                            .or(label2Matches.not()))))));
    }

    /**
     * Class containing information for a game to start
     */
    public static final class GameOptions {
        public final int boardSize;
        public final Player p1;
        public final Player p2;
        public final int turnSeconds;
        public final int gameSeconds;

        public GameOptions(int boardSize, final Player p1, final Player p2, int turnSeconds, int gameSeconds) {
            this.boardSize = boardSize;
            this.p1 = p1;
            this.p2 = p2;
            this.turnSeconds = turnSeconds;
            this.gameSeconds = gameSeconds;
            ((PlayerHuman) p1).setSymbol(Player.Symbol.X); // Player 1 is always X and always instanceof PlayerHuman
            if (this.p2 instanceof PlayerHuman)
                ((PlayerHuman) this.p2).setSymbol(Player.Symbol.O);
        }
    }
}

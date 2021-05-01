package hu.alkfejl.controller;

import hu.alkfejl.App;
import hu.alkfejl.layout.*;
import hu.alkfejl.model.*;
import hu.alkfejl.model.dao.MoveDAO;
import hu.alkfejl.model.dao.PartyDAO;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class BoardController implements Draggable {

    /**
     * The window we are drawing on.
     */
    private Stage stage;

    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------Beans-------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private final SimpleGameLogic gameLogic = new SimpleGameLogic();
    private Player p1; // 'X' player
    private Player p2; // 'O' player
    private final List<Move> moves = new ArrayList<>();

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------Game & Turn timer------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    private int turnSeconds;
    private boolean shouldRunTurnTimer = false;
    private final AtomicInteger turnSecondsRemaining = new AtomicInteger();

    private int gameSeconds;
    private boolean shouldRunGameTimer = false;
    private final AtomicInteger gameSecondsRemaining = new AtomicInteger();

    private boolean started = false;

    private final ScheduledService<Void> serviceRunTurnTimer = new ScheduledService<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    gameSecondsRemaining.decrementAndGet();
                    turnSecondsRemaining.decrementAndGet();
                    return null;
                }
            };
        }
    };


    /**
     * Main method for setting up the Controller and handling events.
     * @param options object containing parameteres for a Game
     */
    public void setup(final GameStartDialog.GameOptions options) {

        /*-------------Setup references-------------*/
        serviceRunTurnTimer.setPeriod(Duration.seconds(1.0));
        this.p1 = options.p1;
        this.p2 = options.p2;
        this.turnSeconds = options.turnSeconds;
        this.turnSecondsRemaining.set(turnSeconds);
        this.gameSeconds = options.gameSeconds;
        this.gameSecondsRemaining.set(gameSeconds);

        /*--------------Update turn label-------------*/
        gameLogic.turnProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case X:
                    turnLabel.setText("X");
                    break;
                case O:
                    turnLabel.setText("O");
            }
        });

        /*--------------Setup turn timer--------------*/
        if (this.turnSeconds <= 0)
            turnTimerLabel.setText("\u221E");
        else
            turnTimerLabel.setText(secondsToTime(this.turnSeconds));
        /*--------------Setup game timer--------------*/
        if (this.gameSeconds <= 0)
            gameTimerLabel.setText("\u221E");
        else
            gameTimerLabel.setText(secondsToTime(this.gameSeconds));
        /*----------Turn timer service success listener----------*/
        serviceRunTurnTimer.setOnSucceeded(event -> {
            if (shouldRunTurnTimer) {
                if (turnSecondsRemaining.get() < 0) {
                    turnSecondsRemaining.set(this.turnSeconds);
                    gameLogic.flipTurn();
                    var possibleAI = determinePlayer();
                    if (possibleAI instanceof PlayerAI)
                        aiMakeMove((PlayerAI) possibleAI);
                }
                turnTimerLabel.setText(secondsToTime(turnSecondsRemaining.get()));
            }

            if (shouldRunGameTimer) {
                if (gameSecondsRemaining.get() < 0)
                    endGame(GameLogic.State.DRAW, true);
                gameTimerLabel.setText(secondsToTime(gameSecondsRemaining.get()));
            }
        });

        var party = new Party();
        party.setBoardSize(options.boardSize);
        party.setTimeOfParty(Timestamp.valueOf(LocalDateTime.now()));
        gameLogic.setParty(party);

        for (int i = 0; i < options.boardSize; i++)
            for (int j = 0; j < options.boardSize; j++) {
                var tile = new Tile(i, j);
                tile.text.textProperty().bind(gameLogic.getBoard()[i][j].valueProperty());
                tile.setOnMouseReleased(event -> {
                    if (!started) {
                        if (this.turnSeconds > 0 && !shouldRunTurnTimer)
                            shouldRunTurnTimer = true;

                        if (this.gameSeconds > 0 && !shouldRunGameTimer)
                            shouldRunGameTimer = true;
                        serviceRunTurnTimer.start();
                        started = true;
                    }

                    var player = determinePlayer();

                    if (player instanceof PlayerHuman) {
                        int x = ((Tile) event.getSource()).x;
                        int y = ((Tile) event.getSource()).y;
                        var move = gameLogic.move(player, x, y);
                        if (move != null) {
                            moves.add(move);
                            turnSecondsRemaining.set(BoardController.this.turnSeconds);
                        }

                        var state = gameLogic.checkState();
                        if (state != GameLogic.State.NONE) {
                            endGame(state, true);
                            return;
                        }

                        var possibleAI = determinePlayer();
                        if (possibleAI instanceof PlayerAI)
                            aiMakeMove((PlayerAI) possibleAI);
                    }
                    playButtonPressAnim(tile);
                });
                grid.add(tile, j, i);
            }
    }

    public void setupForReplay(final int boardSize, final Player p1, final Player p2, final List<Move> moves, final Player winner, final Stage stage) {
        var party = new Party();
        party.setBoardSize(boardSize);
        gameLogic.setParty(party);
        ((PlayerHuman) p1).setSymbol(Player.Symbol.X);
        if (p2 instanceof PlayerHuman)
            ((PlayerHuman) p2).setSymbol(Player.Symbol.O);

        turnTimerLabel.setText("-");
        gameTimerLabel.setText("-");

        gameLogic.turnProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case X:
                    turnLabel.setText("X");
                    break;
                case O:
                    turnLabel.setText("O");
            }
        });

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                var tile = new Tile(i, j);
                tile.text.textProperty().bind(gameLogic.getBoard()[i][j].valueProperty());
                grid.add(tile, j, i);
            }

        setupScaling(stage);

        var scheduledService = new ScheduledService<Boolean>() {
            private final Iterator<Move> movesIterator = moves.listIterator();
            private int lastPlayerID = -1;

            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {

                    @Override
                    protected Boolean call() {
                        if (movesIterator.hasNext()) {
                            var move = movesIterator.next();

                            var moveCoords = move.getTile().split("_");
                            var x = Integer.parseInt(moveCoords[0]);
                            var y = Integer.parseInt(moveCoords[1]);

                            if (lastPlayerID == move.getPlayerID())
                                Platform.runLater(gameLogic::flipTurn);

                            if (move.getPlayerID() == p1.getId()){
                                Platform.runLater(() -> gameLogic.move(p1, x, y));
                                lastPlayerID = p1.getId();
                            }
                            else {
                                Platform.runLater(() -> gameLogic.move(p2, x, y));
                                lastPlayerID = p2.getId();
                            }

                            grid.getChildren().forEach(node -> {
                                if (GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y)
                                    Platform.runLater(() -> playButtonPressAnim((Tile) node));
                            });
                            return false;
                        }
                        return true;
                    }
                };
            }
        };
        scheduledService.setDelay(Duration.seconds(1.0));
        scheduledService.setPeriod(Duration.seconds(1.0));
        scheduledService.start();
        scheduledService.setOnSucceeded(event -> {
            if (((Boolean) event.getSource().getValue())) {
                scheduledService.cancel();
                var dialog = new CustomDialogBase<Void>();
                if (winner != null) {
                    dialog.setTitle("Winner");
                    dialog.setHeaderText(winner.getName() + " Won!");
                    dialog.setGraphic(new ImageView(App.TROPHY));
                } else {
                    dialog.setTitle("Draw");
                    dialog.setHeaderText("It's a draw :(");
                }
                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                dialog.showAndWait();
                this.stage.close();
                App.loadFXML(App.FXML.MATCH_HISTORY);
            }
        });
    }

    private void playButtonPressAnim(final Tile tile) {
        final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;
        var scaleBtn = new ScaleTransition(Duration.millis(GOLDEN_RATIO * 100), tile);
        scaleBtn.setFromX(1.0);
        scaleBtn.setFromY(1.0);
        scaleBtn.setToX(0.8);
        scaleBtn.setToY(0.8);
        scaleBtn.setAutoReverse(true);
        scaleBtn.setCycleCount(2);

        var colorBtn = new Transition() {
            {
                setCycleCount(1);
                setCycleDuration(Duration.millis(GOLDEN_RATIO * 100));
                setInterpolator(Interpolator.EASE_BOTH);
            }

            @Override
            protected void interpolate(double frac) {
                tile.setBackground(new Background(new BackgroundFill(Color.web("#254B62", 1.0 - frac), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };

        var parallelTransition = new ParallelTransition(scaleBtn, colorBtn);
        parallelTransition.play();
    }

    /**
     * End this game and upload it's stats to the database.
     * Only called after normal gameplay.
     * @param endState the result of the game
     */
    private void endGame(final GameLogic.State endState, boolean save) {
        shouldRunTurnTimer = false;
        shouldRunGameTimer = false;
        serviceRunTurnTimer.cancel();

        if (save && !moves.isEmpty()) {
            var uploadTask = new Task<Void>() {
                private final PartyDAO partyDAO = SimplePartyDAO.getInstance();
                private final MoveDAO moveDAO = SimpleMoveDAO.getInstance();

                @Override
                protected Void call() {
                    switch (endState) {
                        case XWin:
                            gameLogic.getParty().setWinnerID(p1.getId());
                            break;
                        case OWin:
                            gameLogic.getParty().setWinnerID(p2.getId());
                    }
                    var party = partyDAO.save(gameLogic.getParty());
                    moves.forEach(move -> {
                        move.setPartyID(party.getId());
                        moveDAO.save(move);
                    });
                    return null;
                }
            };
            var uploadThread = new Thread(uploadTask);
            uploadThread.setDaemon(true);
            uploadThread.start();
        }

        var winner = endState == GameLogic.State.XWin ? p1.getName() : (endState == GameLogic.State.OWin ? p2.getName() : "");
        var alert = new CustomDialogBase<Void>();
        if (!winner.isEmpty()) {
            alert.setTitle("Winner");
            alert.setHeaderText(winner + " Won!");
            alert.setGraphic(new ImageView(App.TROPHY));
        } else {
            alert.setTitle("Draw");
            alert.setHeaderText("It's a draw :(");
        }
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
        stage.close();
        App.loadFXML(App.FXML.WELCOME);
    }

    /**
     * Determines whose turn it is
     * @return the player whose turn it is
     */
    private Player determinePlayer() {
        return gameLogic.getTurn() == Player.Symbol.X ? p1 : p2;
    }

    /**
     * Make the ai move
     * @param ai the AI player
     */
    private void aiMakeMove(final PlayerAI ai) {
        var xy = ai.randomMove(gameLogic.getParty().getBoardSize());
        while (!(gameLogic.getBoard()[xy.getKey()][xy.getValue()].getValue().equals(GameLogic.Tile.NONE)))
            xy = ai.randomMove(gameLogic.getParty().getBoardSize());

        final int x = xy.getKey();
        final int y = xy.getValue();
        var move = gameLogic.move(ai, x, y);
        grid.getChildren().forEach(node -> {
            if (GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y)
                playButtonPressAnim((Tile) node);
        });
        if (move != null) {
            moves.add(move);
            turnSecondsRemaining.set(BoardController.this.turnSeconds);
        }
        var state = gameLogic.checkState();
        if (state != GameLogic.State.NONE) {
            endGame(state, true);
        }
    }

    /**
     * Set up the GUI's properties and show it.
     */
    public void setupScaling(final Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.show();
        stage.centerOnScreen();

        setupDragEvent(stage, menuBar);
        setupDragEvent(stage, ((BorderPane) grid.getParent()).getTop());
        var parent = (BorderPane) grid.getParent();

        parent.prefWidthProperty().bind(grid.widthProperty());
        parent.prefHeightProperty().bind(grid.heightProperty());

        grid.setPrefWidth(grid.getChildren().get(0).getLayoutBounds().getWidth());
        grid.setPrefHeight(grid.getChildren().get(0).getLayoutBounds().getHeight());

        grid.getChildren().forEach(child -> {
            ((StackPane) child).prefWidthProperty().bind(grid.widthProperty().divide(gameLogic.getParty().getBoardSize()));
            ((StackPane) child).prefHeightProperty().bind(grid.heightProperty().divide(gameLogic.getParty().getBoardSize()));
        });

        stage.setMinWidth(Math.max(54 * grid.getRowCount(), vbox.getPrefWidth()));
        stage.setMinHeight(54 * grid.getColumnCount() + vbox.getHeight() * 2);

        stage.setOnCloseRequest(event -> {
            App.loadFXML(App.FXML.WELCOME);
            event.consume();
        });
    }

    /**
     * @param seconds seconds to convert
     * @return time string in hh:mm:ss
     */
    public static String secondsToTime(long seconds) {
        long hours = TimeUnit.SECONDS.toHours(seconds);
        seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------FXML References and Methods-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @FXML private GridPane grid;
    @FXML private VBox vbox;
    @FXML private Label turnTimerLabel;
    @FXML private Label turnLabel;
    @FXML private Label gameTimerLabel;
    @FXML private MenuBar menuBar;

    @FXML
    private void handleClose() {
        endGame(GameLogic.State.NONE, false);
    }

    @FXML
    private void handleCloseAndSave() {
        endGame(GameLogic.State.NONE, true);
    }

    @FXML
    private void handleAbout() {
        var dialog = new CustomDialogBase<Void>();
        dialog.setTitle("About");
        dialog.setHeaderText("This program was made by Daniel Szucs (Quasar6X)");
        var link = new Hyperlink();
        link.setText("https://github.com/Quasar6X");
        link.setOnAction(event -> {
            App.app.getHostServices().showDocument(link.getText());
            link.setVisited(false);
        });
        var vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(new Label("My github page"), link);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    @FXML
    private void handleHelp() {
        var dialog = new CustomDialogBase<Void>();
        dialog.setTitle("Help");
        dialog.setHeaderText("Turn timer: how much time you have for making a move.\nGame timer: how much time you have for a game.\n" +
                            "To place your mark simply click one of the available tiles.\nYou can see whose turn it is in the top right corner.\n" +
                            "To end the game go to the file menu and pick\nthe appropriate close operation for you.");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
}

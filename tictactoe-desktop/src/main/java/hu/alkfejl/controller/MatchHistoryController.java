package hu.alkfejl.controller;

import hu.alkfejl.App;
import hu.alkfejl.layout.CustomDialogBase;
import hu.alkfejl.model.Party;
import hu.alkfejl.model.dao.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class MatchHistoryController implements Initializable, Draggable {

    private static final PartyDAO partyDAO = SimplePartyDAO.getInstance();
    private static final PlayerDAO playerDAO = SimplePlayerDAO.getInstance();
    private static final MoveDAO moveDAO = SimpleMoveDAO.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDragEvent(App.getStage(), buttonBar);
        setupDragEvent(App.getStage(), table);

        refreshTable();

        boardSizeCol.setCellValueFactory(new PropertyValueFactory<>("boardSize"));
        dateCol.setCellFactory(callback -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    if (getTableRow().getItem() == null) // https://bugs.openjdk.java.net/browse/JDK-8251483
                        return;
                    var label = new Label(getTableRow().getItem().getTimeOfParty().toString().split("\\.")[0].trim().replace(" ", " \n"));
                    label.setAlignment(Pos.CENTER);
                    setGraphic(label);
                }
            }
        });
        p1Col.setCellFactory(callback -> new TableCell<>() {
            private final List<Integer> playerIdCache = new ArrayList<>();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    var party = getTableRow().getItem();
                    var moves = moveDAO.findAll().stream().filter(move -> move.getPartyID() == party.getId());
                    moves.forEach(move -> {
                        if (!playerIdCache.contains(move.getPlayerID()))
                            playerIdCache.add(move.getPlayerID());
                    });
                    playerDAO.findAll().stream().filter(player -> player.getId() == playerIdCache.get(0)).findAny().ifPresent(player -> {
                        var label = new Label(player.getName());
                        label.setAlignment(Pos.CENTER);
                        setGraphic(label);
                    });
                }
            }
        });
        p2Col.setCellFactory(callback -> new TableCell<>() {
            private final List<Integer> playerIdCache = new ArrayList<>();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else  {
                    var party = getTableRow().getItem();
                    var moves = moveDAO.findAll().stream().filter(move -> move.getPartyID() == party.getId());
                    moves.forEach(move -> {
                        if (!playerIdCache.contains(move.getPlayerID()))
                            playerIdCache.add(move.getPlayerID());
                    });
                    playerDAO.findAll().stream().filter(player -> player.getId() == playerIdCache.get(playerIdCache.size() - 1)).findAny().ifPresent(player -> {
                        var label = new Label(player.getName());
                        label.setAlignment(Pos.CENTER);
                        setGraphic(label);
                    });
                }
            }
        });
        winnerCol.setCellFactory(callback -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    var label = new Label();
                    label.setAlignment(Pos.CENTER);
                    playerDAO.findAll().stream().filter(p -> p.getId() == getTableRow().getItem().getWinnerID()).findAny().ifPresentOrElse(
                            player -> label.setText(player.getName()),
                            () -> label.setText("Draw"));
                    setGraphic(label);
                }
            }
        });
        actionCol.setCellFactory(callback -> new TableCell<>() {
            private final Button replayBtn = new Button("Replay");
            private final Button deleteBtn = new Button("Delete");
            private final List<Integer> playerIdCache = new ArrayList<>();

            {
                replayBtn.setOnAction(event -> {
                    var stage = new Stage();
                    BoardController controller = App.loadFXML(App.FXML.BOARD, stage, o -> {}).getController();
                    var party = getTableRow().getItem();
                    var players = playerDAO.findAll();

                    var moves = moveDAO.findAll().stream().filter(move -> move.getPartyID() == party.getId());
                    moves.forEach(move -> {
                        if (!playerIdCache.contains(move.getPlayerID()))
                            playerIdCache.add(move.getPlayerID());
                    });

                    try {
                        controller.setupForReplay(party.getBoardSize(),
                                players.stream().filter(p1 -> p1.getId() == playerIdCache.get(0)).findAny().orElseThrow(),
                                players.stream().filter(p1 -> p1.getId() == playerIdCache.get(playerIdCache.size() - 1)).findAny().orElseThrow(),
                                moveDAO.findAll().stream().filter(move -> move.getPartyID() == party.getId()).collect(Collectors.toList()),
                                players.stream().filter(p3 -> p3.getId() == party.getWinnerID()).findAny().orElse(null),
                                stage);
                    } catch (final NoSuchElementException e) {
                        var dialog = new CustomDialogBase<Void>();
                        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        dialog.setTitle("Something went wrong...");
                        dialog.setHeaderText("Oh-Oh, It seems like something went wrong...");
                        dialog.showAndWait();
                    }
                });

                deleteBtn.setOnAction(event -> deleteParty(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    var vBox = new VBox(replayBtn, deleteBtn);
                    vBox.setSpacing(10);
                    vBox.setPadding(new Insets(10, 0, 10, 0));
                    vBox.setAlignment(Pos.CENTER);
                    setGraphic(vBox);
                }
            }
        });
    }

    private void deleteParty(final Party party) {
        var dialog = new CustomDialogBase<ButtonType>();
        dialog.setTitle("Delete match");
        dialog.setHeaderText("Do you really want to delete this match?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        dialog.showAndWait().filter(response -> response == ButtonType.YES).ifPresent(buttonType -> {
            var task = new Task<Void>() {
                @Override
                protected Void call() {
                    moveDAO.findAll().stream().filter(move -> move.getPartyID() == party.getId()).forEach(moveDAO::delete);
                    partyDAO.delete(party);
                    return null;
                }
            };
            task.setOnSucceeded(workerStateEvent -> refreshTable());
            var thread = new Thread(task);
            thread.start();
        });
    }

    private void refreshTable() {
        var task = new Task<Void>() {
            @Override
            protected Void call() {
                table.getItems().setAll(partyDAO.findAll());
                return null;
            }
        };
        var refreshThread = new Thread(task);
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------FXML References and Methods-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @FXML private ButtonBar buttonBar;
    @FXML private TableView<Party> table;
    @FXML private TableColumn<Party, Integer> boardSizeCol;
    @FXML private TableColumn<Party, String> dateCol;
    @FXML private TableColumn<Party, String> p1Col;
    @FXML private TableColumn<Party, String> p2Col;
    @FXML private TableColumn<Party, String> winnerCol;
    @FXML private TableColumn<Party, Void> actionCol;

    @FXML
    private void handleCancel() {
        App.loadFXML(App.FXML.WELCOME);
    }
}

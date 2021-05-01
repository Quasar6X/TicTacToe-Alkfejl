package hu.alkfejl.controller;

import hu.alkfejl.App;
import hu.alkfejl.layout.AddPlayerDialog;
import hu.alkfejl.layout.CustomDialogBase;
import hu.alkfejl.layout.GameStartDialog;
import hu.alkfejl.model.Player;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class PlayerListController implements Initializable, Draggable {

    private static final PlayerDAO playerDAO = SimplePlayerDAO.getInstance();
    private static final MoveDAO moveDAO = SimpleMoveDAO.getInstance();
    private static final PartyDAO partyDAO = SimplePartyDAO.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDragEvent(App.getStage(), buttonBar);
        setupDragEvent(App.getStage(), table);

        refreshTable();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        actionColumn.setCellFactory(callback -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setOnAction(event -> deletePlayer(getTableRow().getItem()));

                editBtn.setOnAction(event -> editPlayer(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else {
                    var hbox = new HBox(editBtn, deleteBtn);
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void editPlayer(final hu.alkfejl.model.Player player) {
        var dialog = new AddPlayerDialog();
        dialog.setTitle("Edit dialog");
        dialog.setHeaderText("You are editing Player: \"" + player.getName() + "\" with id: \"" + player.getId() + "\"");
        dialog.showAndWait().ifPresent(newName -> {
            ((PlayerHuman) player).setName(newName);
            updateOrSavePlayer(player);
        });
    }

    private void deletePlayer(final hu.alkfejl.model.Player player) {
        var alert = new CustomDialogBase<ButtonType>();
        alert.setTitle("Delete dialog");
        alert.setHeaderText("Do you really want to delete player \"" + player.getName() +"\"?");
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        alert.showAndWait().filter(response -> response == ButtonType.YES).ifPresent(response -> {
            var task = new Task<Void>() {
                @Override
                protected Void call() {
                    var moves = moveDAO.findAll();
                    var movesOfPlayer = moves.stream().filter(move -> move.getPlayerID() == player.getId()).collect(Collectors.toList());
                    var idCache = new ArrayList<Integer>();
                    movesOfPlayer.forEach(move -> {
                        if (!idCache.contains(move.getPartyID())) {
                            idCache.add(move.getPartyID());
                        }
                    });
                    idCache.forEach(partyID -> {
                        moves.stream().filter(move -> move.getPartyID() == partyID).collect(Collectors.toList()).forEach(moveDAO::delete);
                        partyDAO.findAll().stream().filter(party -> party.getId() == partyID).findAny().ifPresent(partyDAO::delete);
                    });

                    playerDAO.findAll().stream().filter(p -> p.getId() == player.getId()).findAny().ifPresent(playerDAO::delete);
                    return null;
                }
            };
            task.setOnSucceeded(event -> refreshTable());
            var thread = new Thread(task);
            thread.start();
        });
    }

    private void refreshTable() {
        var task = new Task<Void>() {
            @Override
            protected Void call() {
                table.getItems().setAll(playerDAO.findAll().stream().filter(player -> player.getId() != 1).collect(Collectors.toList()));
                return null;
            }
        };
        var refreshThread = new Thread(task);
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void updateOrSavePlayer(final Player player) {
        var task = new Task<Void>() {
            @Override
            protected Void call() {
                playerDAO.save(player);
                return null;
            }
        };
        task.setOnSucceeded(event -> refreshTable());
        var thread = new Thread(task);
        thread.start();
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------FXML References and Methods-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @FXML private ButtonBar buttonBar;
    @FXML private TableView<hu.alkfejl.model.Player> table;
    @FXML private TableColumn<hu.alkfejl.model.Player, String> nameColumn;
    @FXML private TableColumn<hu.alkfejl.model.Player, Void> actionColumn;

    @FXML
    private void handleCancel() {
        App.loadFXML(App.FXML.WELCOME);
    }

    @FXML
    private void handlePlay() {
        var gameStartDialog = new GameStartDialog();
        gameStartDialog.showAndWait().ifPresent(result -> {
            if (result.p1.getName().equals(result.p2.getName())) {
                var alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(App.ICON);
                alert.getDialogPane().getStylesheets().add(App.GLOBAL_CSS);
                alert.setTitle("Same player error");
                alert.setHeaderText("P1 and P2 cannot be the same!");
                alert.showAndWait();
            } else {
                var stage = new Stage();
                BoardController controller = App.loadFXML(App.FXML.BOARD, stage, o -> {}).getController();
                controller.setup(result);
                controller.setupScaling(stage);
            }
        });
    }

    @FXML
    private void handleAddNewPlayer() {
        var addNewPlayerDialog = new AddPlayerDialog();
        addNewPlayerDialog.showAndWait().ifPresent(s -> {
            var player = new PlayerHuman();
            player.setName(s);
            updateOrSavePlayer(player);
        });
    }
}

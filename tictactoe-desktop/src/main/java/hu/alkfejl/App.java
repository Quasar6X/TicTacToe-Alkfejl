package hu.alkfejl;

import hu.alkfejl.connection.ConnectionManagerClient;

import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;
import hu.alkfejl.model.dao.SimplePlayerDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

public final class App extends Application {

    private static final Logger LOG = Logger.getLogger(App.class);
    private static final ConnectionManagerClient connectionManager = new ConnectionManagerClient();
    private static Stage stage;

    public static final String GLOBAL_CSS;
    public static final Image ICON;
    public static final Image TROPHY;
    public static App app;

    static {
        String tempCss;
        var url = App.class.getResource("/hu/alkfejl/css/global.css");
        if (url != null)
            tempCss = url.toExternalForm();
        else {
            var exc = new RuntimeException("Could not load global.css");
            LOG.warn(exc.getMessage(), exc);
            throw exc;
        }
        GLOBAL_CSS = tempCss;

        ICON = loadImageFromPath("/hu/alkfejl/img/logo.png");
        TROPHY = loadImageFromPath("/hu/alkfejl/img/trophy.png");
    }

    private static Image loadImageFromPath(final String path) {
        Image temp;
        var inStream = App.class.getResourceAsStream(path);
        if (inStream != null)
            temp = new Image(inStream);
        else {
            var exc = new RuntimeException("Could not load " + path);
            LOG.warn(exc.getMessage(), exc);
            throw exc;
        }
        return temp;
    }

    public enum FXML {
        BOARD("/hu/alkfejl/fxml/board.fxml"),
        WELCOME("/hu/alkfejl/fxml/welcome.fxml"),
        PLAYER("/hu/alkfejl/fxml/player.fxml"),
        MATCH_HISTORY("/hu/alkfejl/fxml/match_history.fxml");

        public final String fxml;

        FXML(String fxml) { this.fxml = fxml; }
    }

    @Override
    public void init() {
        SimplePartyDAO.getInstance().setConnectionManager(connectionManager);
        SimplePlayerDAO.getInstance().setConnectionManager(connectionManager);
        SimpleMoveDAO.getInstance().setConnectionManager(connectionManager);
    }

    @Override
    public void start(final Stage stage) {
        App.app = this;
        App.stage = stage;
        loadFXML(FXML.WELCOME);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.getIcons().add(ICON);
        stage.show();
    }

    @Override
    public void stop() {
        connectionManager.closeConnections();
    }

    public static void loadFXML(final FXML fxml) {
        loadFXML(fxml, stage, o -> {});
    }

    public static <T> FXMLLoader loadFXML(final FXML fxml, final Stage stage, final Consumer<T> controllerOps) {
        final var loader = new FXMLLoader(App.class.getResource(fxml.fxml));
        try {
            Parent root = loader.load();
            controllerOps.accept(loader.getController());
            var scene = new Scene(root);
            var css = App.class.getResource("/hu/alkfejl/css/global.css");
            if (css != null)
                scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (final Exception e) {
            LOG.error("Could not load FXML file", e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
        }
        return loader;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

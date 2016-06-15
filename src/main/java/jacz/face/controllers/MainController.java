package jacz.face.controllers;

import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.actions.ints.*;
import jacz.face.messages.Messages;
import jacz.face.state.ConnectionToServerStatus;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineclient.SessionManager;
import jacz.util.lists.tuple.Duple;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController extends GenericController {

    //@FXML
    //private Label label;

    @FXML
    private ToggleSwitch connectSwitch;

    @FXML
    private Label connectedLabel;

    @FXML
    private Button settingsButton;

    @FXML
    private AnchorPane viewContainer;

    @FXML
    private Label connectedFavoritePeersLabel;

    @FXML
    private Label totalFavoritePeersLabel;

    @FXML
    private Label connectedRegularPeersLabel;

    //@FXML
    //private Label serverAddressLabel;

    /**
     * Jacuzzi peer client
     */
    private PeerEngineClient client;

    ConnectionToServerStatus connectionToServerStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize controller
        // todo

        System.out.println("Controller initialized");


        connectionToServerStatus = new ConnectionToServerStatus();


        //connectedLabel.textProperty().bind(connectionToServerStatus.connectedLabelTextProperty());
        //serverAddressLabel.textProperty().bind(connectionToServerStatus.serverAddressProperty());
        //connectionButton.textProperty().bind(connectionToServerStatus.connectionButtonTextProperty());


        connectedLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(PropertiesAccessor.getInstance().getConnectionStateProperties().aggregatedConnectionStatusProperty());
            }

            @Override
            protected String computeValue() {
                switch (PropertiesAccessor.getInstance().getConnectionStateProperties().aggregatedConnectionStatusProperty().get()) {
                    case DISCONNECTED:
                        return Messages.ServerMessages.DISCONNECTED();
                    case CONNECTING:
                        return Messages.ServerMessages.CONNECTING();
                    case DISCONNECTING:
                        return Messages.ServerMessages.DISCONNECTING();
                    case CONNECTED:
                        return Messages.ServerMessages.CONNECTED();
                    default:
                        return Messages.ServerMessages.DISCONNECTED();
                }
            }
        });

        BooleanProperty receiveWishForConnectionStatus = new SimpleBooleanProperty(false);
        receiveWishForConnectionStatus.bind(new BooleanBinding() {
            {
                super.bind(PropertiesAccessor.getInstance().getConnectionStateProperties().aggregatedConnectionStatusProperty());
            }

            @Override
            protected boolean computeValue() {
                switch (PropertiesAccessor.getInstance().getConnectionStateProperties().aggregatedConnectionStatusProperty().get()) {
                    case CONNECTED:
                        Util.setLater(connectSwitch.selectedProperty(), false);
                        return true;
                    default:
                        return false;
                }
            }
        });
        PropertiesAccessor.getInstance().getConnectionStateProperties().isWishForConnectionProperty().addListener((observable, oldValue, newValue) -> {
            Util.setLater(connectSwitch.selectedProperty(), newValue);
        });

        connectSwitch.setOnMouseClicked(event -> {
            if (connectSwitch.isSelected()) {
                client.connect();
            } else {
                client.disconnect();
            }
        });

        connectedFavoritePeersLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(PropertiesAccessor.getInstance().getPeersStateProperties().connectedFavoritePeersProperty());
            }

            @Override
            protected String computeValue() {
                return Integer.toString(PropertiesAccessor.getInstance().getPeersStateProperties().connectedFavoritePeersProperty().get());
            }
        });
        totalFavoritePeersLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(PropertiesAccessor.getInstance().getPeersStateProperties().totalFavoritePeersProperty());
            }

            @Override
            protected String computeValue() {
                return Integer.toString(PropertiesAccessor.getInstance().getPeersStateProperties().totalFavoritePeersProperty().get());
            }
        });
        connectedRegularPeersLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(PropertiesAccessor.getInstance().getPeersStateProperties().connectedRegularPeersProperty());
            }

            @Override
            protected String computeValue() {
                return Integer.toString(PropertiesAccessor.getInstance().getPeersStateProperties().connectedRegularPeersProperty().get());
            }
        });
    }

    public List<String> listAvailableConfigs(String baseDir) throws IOException {
        return SessionManager.listAvailableConfigs(baseDir);
    }

    public void loadConfig(String baseDir) throws IOException {

        Duple<PeerEngineClient, List<String>> duple = SessionManager.load(
                listAvailableConfigs(baseDir).get(0),
                new GeneralEventsImpl(PropertiesAccessor.getInstance().getGeneralStateProperties()),
                new ConnectionEventsImpl(connectionToServerStatus, PropertiesAccessor.getInstance().getConnectionStateProperties()),
                new PeersEventsImpl(PropertiesAccessor.getInstance().getPeersStateProperties()),
                new ResourceTransferEventsImpl(PropertiesAccessor.getInstance().getTransferStatsProperties()),
                new TempFileManagerEventsImpl(),
                new DatabaseSynchEventsImpl(),
                new DownloadEventsImpl(PropertiesAccessor.getInstance().getTransferStatsProperties()),
                new IntegrationEventsImpl(),
                new ErrorEventsImpl());
        client = duple.element1;
        ClientAccessor.setup(client);
        PropertiesAccessor.getInstance().setup(client);

        //setupLocal(client.getDatabases().getLocalDB(), client);
        //removeLocal(client.getDatabases().getLocalDB(), client);

//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP20"));
//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP21"));
//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP22"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP27"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP28"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP29"));
    }

    private static void setupLocal(String db, PeerEngineClient peerEngineClient) {
        // stored as id=2
        Movie movie = new Movie(db, "Alien");
        VideoFile videoFile1 = new VideoFile(db, "hash1");
        VideoFile videoFile2 = new VideoFile(db, "hash2");
        VideoFile videoFile3 = new VideoFile(db, "hash3");
        VideoFile videoFile4 = new VideoFile(db, "hash4");
        VideoFile videoFile5 = new VideoFile(db, "hash5");
        movie.addVideoFile(videoFile1);
        movie.addVideoFile(videoFile2);
        movie.addVideoFile(videoFile3);
        movie.addVideoFile(videoFile4);
        movie.addVideoFile(videoFile5);

        peerEngineClient.localItemModified(videoFile1);
        peerEngineClient.localItemModified(videoFile2);
        peerEngineClient.localItemModified(videoFile3);
        peerEngineClient.localItemModified(videoFile4);
        peerEngineClient.localItemModified(videoFile5);
        peerEngineClient.localItemModified(movie);
    }

    private static void removeLocal(String db, PeerEngineClient peerEngineClient) {
        Movie movie = Movie.getMovieById(db, 1);
        peerEngineClient.removeLocalItem(movie);
    }


    public void connectAction() {
        //client.connect();
        //ThreadUtil.safeSleep(3000);
        Util.setLater(connectSwitch.selectedProperty(), false);
    }

    public void setText(final String str) {
        //Platform.runLater(() -> label.setText(str));
    }

    public void switchToMoviesView() throws IOException {
        replaceViewContainerContent("/view/movies_view.fxml");
    }

    public void switchToSeriesView() throws IOException {
        replaceViewContainerContent("/view/series_view.fxml");
    }

    public void switchToFavoritesView() throws IOException {
        replaceViewContainerContent("/view/favorites_view.fxml");
    }

    public void switchToTransfersView() throws IOException {
        replaceViewContainerContent("/view/transfers_view.fxml");
    }

    public void switchToPeersView() throws IOException {
        replaceViewContainerContent("/view/peers_view.fxml");
    }

    public void switchToSettingsView() throws IOException {
        replaceViewContainerContent("/view/settings.fxml");
    }

    private void replaceViewContainerContent(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        AnchorPane page = fxmlLoader.load(getClass().getResource(fxml).openStream());
        ((GenericController) fxmlLoader.getController()).setMain(main);


        //FXMLLoader loader = new FXMLLoader();
        //InputStream in = FXMLLoginDemoApp.class.getResourceAsStream(fxml);
        //loader.setBuilderFactory(new JavaFXBuilderFactory());
        //loader.setLocation(FXMLLoginDemoApp.class.getResource(fxml));
//        AnchorPane page;
//        try {
//            page = (AnchorPane) fxmlLoader.load(in);
//        } finally {
//            in.close();
//        }
        viewContainer.getChildren().clear();
        viewContainer.getChildren().addAll(page);
        //return (Initializable) loader.getController();
    }

    public void openSettings() {
        System.out.println("open settings");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("/view/settings.fxml").openStream());
            //AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("view/settings.fxml").openStream());
            SettingsController settingsController = fxmlLoader.getController();
            settingsController.setMain(main);

            Dialog<SettingsController.SettingsValues> settingsDialog = new Dialog<>();
            settingsDialog.setTitle("settings");
            settingsDialog.getDialogPane().setContent(settingsPane);

            // Set the button types.
            settingsDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            // Convert the result to a settings value when the ok button is clicked.
            settingsDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new SettingsController.SettingsValues();
                }
                return null;
            });

            Optional<SettingsController.SettingsValues> result = settingsDialog.showAndWait();

            result.ifPresent(newSettings -> {
                System.out.println(newSettings.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("stopping client...");
        if (client != null) {
            client.stop();
        }
        System.out.println("client stopped!");
    }
}

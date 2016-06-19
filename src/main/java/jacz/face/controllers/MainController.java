package jacz.face.controllers;

import jacz.database.Movie;
import jacz.database.TVSeries;
import jacz.database.VideoFile;
import jacz.database.util.ImageHash;
import jacz.face.actions.ints.*;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
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
    private Button backwardsButton;

    @FXML
    private Button forwardButton;

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
                        //Util.setLater(connectSwitch.selectedProperty(), false);
                        return true;
                    default:
                        return false;
                }
            }
        });
        PropertiesAccessor.getInstance().getConnectionStateProperties().isWishForConnectionProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("change in iswish...: " + newValue);
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



        // move to initial window
//        NavigationHistory.Element element = main.getNavigationHistory().getCurrentElement();
//        try {
//            moveToNavigationElement(element);
//        } catch (IOException e) {
//            // todo
//            e.printStackTrace();
//        }
    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        backwardsButton.disableProperty().bind(main.getNavigationHistory().canMoveBackwardsProperty().not());
        forwardButton.disableProperty().bind(main.getNavigationHistory().canMoveForwardProperty().not());
    }

    public void moveToNavigationElement(NavigationHistory.Element element) throws IOException {
        switch (element.window) {

            case MEDIA_LIST:
                //main.setCurrentMediaView(Main.MediaView.MOVIES);
                replaceViewContainerContent("/view/media_list_view.fxml");
                break;
            case ITEM_DETAIL:
                replaceViewContainerContent("/view/movie_view.fxml");
                break;
            case TRANSFERS:
                replaceViewContainerContent("/view/transfers_view.fxml");
                break;
            case PEERS:
                replaceViewContainerContent("/view/peers_view.fxml");
                break;
        }
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
                new IntegrationEventsImpl(PropertiesAccessor.getInstance().getMediaDatabaseProperties()),
                new ErrorEventsImpl());
        client = duple.element1;
        ClientAccessor.setup(client);
        PropertiesAccessor.getInstance().setup(client);

        //setupSeries(client.getDatabases().getLocalDB(), client);
        //setupLocal(client.getDatabases().getLocalDB(), client);
        //removeLocal(client.getDatabases().getLocalDB(), client);
        //loadLocal(client.getDatabases().getLocalDB(), client);

//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP20"));
//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP21"));
//        client.addFavoritePeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP22"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP27"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP28"));
//        client.addBlockedPeer(new PeerId("Ga7Of_mN5U6W-xWK_e7No92a5pSsjpLikeauCKACP29"));
    }

    private static void setupLocal(String db, PeerEngineClient peerEngineClient) {
        // stored as id=1
        Movie movie = new Movie(db, "Alien");
        movie.setYear(1998);
        movie.setMinutes(126);
        movie.setImageHash(new ImageHash("f37725b46a263b3de9ff25b2061943e1".toUpperCase(), "jpg"));
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
        try {
            peerEngineClient.addLocalImageFile("./alien.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        movie = new Movie(db, "Cisne negro");
        movie.setYear(2009);
        movie.setImageHash(new ImageHash("eedd8704225fb2307f8afec037e6aab3".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./cisne_negro.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        movie = new Movie(db, "Gravity");
        movie.setYear(2011);
        movie.setImageHash(new ImageHash("5aee69a414abe5886444bdbd7e1dc592".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./gravity-poster.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        movie = new Movie(db, "Interstellar");
        movie.setYear(20014);
        movie.setImageHash(new ImageHash("ee14fd01e0ca1330886d9043b3f44248".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./interstellar.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        movie = new Movie(db, "Mad max");
        movie.setYear(20015);
        movie.setImageHash(new ImageHash("951752219b972cdc9179e920bb6f24e1".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./mad-max.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        movie = new Movie(db, "Postal");
        movie.setYear(2002);
        movie.setImageHash(new ImageHash("5138a57cde050f229fcc38608d795641".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./postal.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupSeries(String db, PeerEngineClient peerEngineClient) {
        // stored as id=1
        TVSeries tvSeries = new TVSeries(db, "Breaking bad");
        tvSeries.setYear(2011);
        tvSeries.setImageHash(new ImageHash("3bbd87be296c63cfd3e6d3490b9d3d2c".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(tvSeries);
        try {
            peerEngineClient.addLocalImageFile("./breaking bad.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvSeries = new TVSeries(db, "Juego de tronos");
        tvSeries.setYear(2013);
        tvSeries.setImageHash(new ImageHash("a5fb7f1fe8102f00c634b292e8b5c8ee".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(tvSeries);
        try {
            peerEngineClient.addLocalImageFile("./got.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadLocal(String db, PeerEngineClient peerEngineClient) {
        Movie movie = Movie.getMovies(db).get(0);
        movie.setYear(1998);
        movie.setMinutes(126);
        movie.setImageHash(new ImageHash("f37725b46a263b3de9ff25b2061943e1".toUpperCase(), "jpg"));
        peerEngineClient.localItemModified(movie);
        try {
            peerEngineClient.addLocalImageFile("./alien.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeLocal(String db, PeerEngineClient peerEngineClient) {
        Movie movie = Movie.getMovieById(db, 1);
        peerEngineClient.removeLocalItem(movie);
    }



    public void setText(final String str) {
        //Platform.runLater(() -> label.setText(str));
    }

    public void switchToMoviesView() throws IOException {
        main.getNavigationHistory().navigate(NavigationHistory.Element.mediaList(NavigationHistory.MediaType.MOVIES));
        main.displayCurrentNavigationWindow();
    }

    public void switchToSeriesView() throws IOException {
        main.getNavigationHistory().navigate(NavigationHistory.Element.mediaList(NavigationHistory.MediaType.SERIES));
        main.displayCurrentNavigationWindow();
    }

    public void switchToFavoritesView() throws IOException {
        main.getNavigationHistory().navigate(NavigationHistory.Element.mediaList(NavigationHistory.MediaType.FAVORITES));
        main.displayCurrentNavigationWindow();
    }

    public void switchToTransfersView() throws IOException {
        main.getNavigationHistory().navigate(NavigationHistory.Element.transfers());
        main.displayCurrentNavigationWindow();
    }

    public void switchToPeersView() throws IOException {
        main.getNavigationHistory().navigate(NavigationHistory.Element.peers());
        main.displayCurrentNavigationWindow();
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

        AnchorPane.setBottomAnchor(page, 0d);
        AnchorPane.setRightAnchor(page, 0d);
        AnchorPane.setTopAnchor(page, 0d);
        AnchorPane.setLeftAnchor(page, 0d);


        //return (Initializable) loader.getController();
    }

    public void openSettings() {
        System.out.println("open settings");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("/view/settings.fxml").openStream());
            //AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("view/settings.fxml").openStream());
            final SettingsController settingsController = fxmlLoader.getController();
            settingsController.setMain(main);

            Dialog<SettingsController.SettingsValues> settingsDialog = new Dialog<>();
            settingsDialog.setTitle("settings");
            settingsDialog.getDialogPane().setContent(settingsPane);

            // Set the button types.
            settingsDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            // Convert the result to a settings value when the ok button is clicked.
            settingsDialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return settingsController.buildSettingsValues();
                } else {
                    return null;
                }
            });

            Optional<SettingsController.SettingsValues> result = settingsDialog.showAndWait();

            result.ifPresent(newSettings -> {
                System.out.println(newSettings.toString());
                client.setLocalPort(newSettings.localPort);
                client.setExternalPort(newSettings.externalPort);
                client.setMaxDownloadSpeed(newSettings.maxDownloadSpeed);
                client.setMaxUploadSpeed(newSettings.maxUploadSpeed);
                client.setWishForRegularsConnections(newSettings.useRegularConnections);
                client.setMaxRegularConnections(newSettings.maxRegularConnections);
                client.setMaxRegularConnectionsForAdditionalCountries(newSettings.maxRegularConnectionsForAdditionalCountries);
                client.setMainCountry(newSettings.mainCountry);
                client.setAdditionalCountries(newSettings.additionalCountries);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navigateBackwards() throws IOException {
        main.getNavigationHistory().backwards();
        main.displayCurrentNavigationWindow();
    }

    public void navigateForward() throws IOException {
        main.getNavigationHistory().forward();
        main.displayCurrentNavigationWindow();
    }

    public void stop() {
        System.out.println("stopping client...");
        if (client != null) {
            client.stop();
        }
        System.out.println("client stopped!");
    }
}

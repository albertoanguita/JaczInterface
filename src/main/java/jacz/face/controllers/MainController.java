package jacz.face.controllers;

import jacz.face.actions.ints.*;
import jacz.face.messages.Messages;
import jacz.face.state.ConnectionStateProperties;
import jacz.face.state.ConnectionToServerStatus;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineclient.SessionManager;
import jacz.util.lists.tuple.Duple;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends GenericController {

    //@FXML
    //private Label label;

    @FXML
    private ToggleSwitch connectSwitch;

    @FXML
    private Label connectedLabel;

    //@FXML
    //private Label serverAddressLabel;

    /**
     * Jacuzzi peer client
     */
    private PeerEngineClient client;

    ConnectionToServerStatus connectionToServerStatus;

    private ConnectionStateProperties connectionStateProperties;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize controller
        // todo

        System.out.println("Controller initialized");


        connectionToServerStatus = new ConnectionToServerStatus();



        //connectedLabel.textProperty().bind(connectionToServerStatus.connectedLabelTextProperty());
        //serverAddressLabel.textProperty().bind(connectionToServerStatus.serverAddressProperty());
        //connectionButton.textProperty().bind(connectionToServerStatus.connectionButtonTextProperty());



        connectionStateProperties = new ConnectionStateProperties();

        connectedLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(connectionStateProperties.aggregatedConnectionStatusProperty());
            }
            @Override
            protected String computeValue() {
                switch (connectionStateProperties.aggregatedConnectionStatusProperty().get()) {
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
                super.bind(connectionStateProperties.aggregatedConnectionStatusProperty());
            }
            @Override
            protected boolean computeValue() {
                System.out.println("change");
                switch (connectionStateProperties.aggregatedConnectionStatusProperty().get()) {
                    case CONNECTED:
                        System.out.println("heeeeeeeeeee");
                        Util.setLater(connectSwitch.selectedProperty(), false);
                        return true;
                    default:
                        return false;
                }
            }
        });
        connectionStateProperties.isWishForConnectionProperty().addListener((observable, oldValue, newValue) -> {
            Util.setLater(connectSwitch.selectedProperty(), newValue);
        });

        connectSwitch.setOnMouseClicked(event -> {
            if (connectSwitch.isSelected()) {
                client.connect();
            } else {
                client.disconnect();
            }
        });
    }

    public List<String> listAvailableConfigs(String baseDir) throws IOException {
        return SessionManager.listAvailableConfigs(baseDir);
    }

    public void loadConfig(String baseDir) throws IOException {

        Duple<PeerEngineClient, List<String>> duple = SessionManager.load(
                listAvailableConfigs(baseDir).get(0),
                new GeneralEventsImpl(),
                new ConnectionEventsImpl(connectionToServerStatus, connectionStateProperties),
                new PeersEventsImpl(),
                new ResourceTransferEventsImpl(),
                new TempFileManagerEventsImpl(),
                new DatabaseSynchEventsImpl(),
                new DownloadEventsImpl(),
                new IntegrationEventsImpl(),
                new ErrorEventsImpl());
        client = duple.element1;
    }

    public void connectAction() {
        //client.connect();
        //ThreadUtil.safeSleep(3000);
        Util.setLater(connectSwitch.selectedProperty(), false);
    }

    public void setText(final String str) {
        //Platform.runLater(() -> label.setText(str));
    }

    public void stop() {
        System.out.println("stopping client...");
        if (client != null) {
            client.stop();
        }
        System.out.println("client stopped!");
    }
}

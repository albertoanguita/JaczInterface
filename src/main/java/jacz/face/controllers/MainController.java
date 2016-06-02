package jacz.face.controllers;

import jacz.face.messages.Messages;
import jacz.face.state.ConnectionState;
import jacz.face.state.ConnectionToServerStatus;
import jacz.face.actions.ints.*;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineclient.SessionManager;
import jacz.util.lists.tuple.Duple;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {



    public class MyTask extends Task<String> {

        @Override
        protected String call() throws Exception {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @FXML
    private Label label;

    @FXML
    private Button connectionButton;

    @FXML
    private Label connectedLabel;

    @FXML
    private Label serverAddressLabel;

    /**
     * Jacuzzi peer client
     */
    private PeerEngineClient client;

    ConnectionToServerStatus connectionToServerStatus;

    private ConnectionState connectionState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize controller
        // todo

        System.out.println("Controller initialized");


        connectionToServerStatus = new ConnectionToServerStatus();



        //connectedLabel.textProperty().bind(connectionToServerStatus.connectedLabelTextProperty());
        serverAddressLabel.textProperty().bind(connectionToServerStatus.serverAddressProperty());
        connectionButton.textProperty().bind(connectionToServerStatus.connectionButtonTextProperty());



        connectionState = new ConnectionState();

        connectedLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(connectionState.connectionToServerStateProperty());
            }
            @Override
            protected String computeValue() {
                switch (connectionState.connectionToServerStateProperty().get()) {

                    case UNREGISTERED:
                    case REGISTERING:
                    case DISCONNECTED:
                        return Messages.ServerMessages.DISCONNECTED();
                    case CONNECTING:
                    case WAITING_FOR_NEXT_CONNECTION_TRY:
                        return Messages.ServerMessages.CONNECTING();
                    case CONNECTED:
                        return Messages.ServerMessages.CONNECTED();

                    default:
                        return Messages.ServerMessages.DISCONNECTED();
                }
            }
        });
        //UserOfTask userOfTask = new UserOfTask("hola", label.textProperty(), this);
    }

    public List<String> listAvailableConfigs(String baseDir) throws IOException {
        return SessionManager.listAvailableConfigs(baseDir);
    }

    public void loadConfig(String baseDir) throws IOException {

        Duple<PeerEngineClient, List<String>> duple = SessionManager.load(
                listAvailableConfigs(baseDir).get(0),
                new GeneralEventsImpl(),
                new ConnectionEventsImpl(connectionToServerStatus, connectionState),
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
        client.connect();
    }

    public void setText(final String str) {
        Platform.runLater(() -> label.setText(str));
    }

    public void stop() {
        System.out.println("stopping client...");
        if (client != null) {
            client.stop();
        }
        System.out.println("client stopped!");
    }
}

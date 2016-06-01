package jacz.face.controllers;

import jacz.face.actions.ConnectionToServerStatus;
import jacz.face.actions.ints.*;
import jacz.peerengineclient.PeerEngineClient;
import jacz.peerengineclient.SessionManager;
import jacz.util.lists.tuple.Duple;
import javafx.application.Platform;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize controller
        // todo

        System.out.println("Controller initialized");

        ConnectionToServerStatus connectionToServerStatus = new ConnectionToServerStatus();

        connectedLabel.textProperty().bind(connectionToServerStatus.connectedLabelTextProperty());
        serverAddressLabel.textProperty().bind(connectionToServerStatus.serverAddressProperty());
        connectionButton.textProperty().bind(connectionToServerStatus.connectionButtonTextProperty());


        //UserOfTask userOfTask = new UserOfTask("hola", label.textProperty(), this);

        try {
            Duple<PeerEngineClient, List<String>> duple = SessionManager.load(
                    "./etc/user_0",
                    new GeneralEventsImpl(),
                    new ConnectionEventsImpl(connectionToServerStatus),
                    new PeersEventsImpl(),
                    new ResourceTransferEventsImpl(),
                    new TempFileManagerEventsImpl(),
                    new DatabaseSynchEventsImpl(),
                    new DownloadEventsImpl(),
                    new IntegrationEventsImpl(),
                    new ErrorEventsImpl());
            client = duple.element1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectAction() {
        client.connect();
    }

    public void setText(final String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(str);
            }
        });
    }
}

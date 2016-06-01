package jacz.face.actions;

import jacz.face.messages.Messages;
import jacz.face.util.Util;
import jacz.peerengineservice.client.connection.State;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class maintains the JavaFX properties for describing the status of the connection to the server
 */
public class ConnectionToServerStatus {

    private StringProperty connectionButtonText;

    private BooleanProperty connectButtonDisabled;

    private StringProperty connectedLabelText;

    // todo missing a "connecting..." status

    // todo separate message in two labels so they have different color in CSS
    //private StringProperty disconnectedLabelText;

    private StringProperty serverAddress;

    public ConnectionToServerStatus() {
        connectionButtonText = new SimpleStringProperty();
        connectedLabelText = new SimpleStringProperty();
        serverAddress = new SimpleStringProperty();

        setDisconnected();
    }

    public String getConnectedLabelText() {
        return connectedLabelText.get();
    }

    public void setConnectedLabelText(String str) {
        this.connectedLabelText.set(str);
    }

    public StringProperty connectedLabelTextProperty() {
        return connectedLabelText;
    }

    public String getServerAddress() {
        return serverAddress.get();
    }

    public void setServerAddress(String str) {
        this.serverAddress.set(str);
    }

    public StringProperty serverAddressProperty() {
        return serverAddress;
    }

    public String getConnectionButtonText() {
        return connectionButtonText.get();
    }

    public void setConnectionButtonText(String str) {
        this.connectionButtonText.set(str);
    }

    public StringProperty connectionButtonTextProperty() {
        return connectionButtonText;
    }

    public Boolean getConnectButtonDisabled() {
        return connectButtonDisabled.get();
    }

    public void setConnectButtonDisabled(Boolean b) {
        this.connectButtonDisabled.set(b);
    }

    public BooleanProperty connectButtonDisabledProperty() {
        return connectButtonDisabled;
    }


    public void connectionEstablished(State state) {
        setConnected(state.getExternalPort());
    }

    public void unableToConnect() {
        setDisconnected();
    }

    public void disconnected() {
        setDisconnected();
    }

//    private void connected(IP4Port ip4Port) {
    private void setConnected(int port) {
        Util.setLater(connectionButtonTextProperty(), Messages.ServerMessages.CONNECTED_BUTTON());
        Util.setLater(connectedLabelText, Messages.ServerMessages.CONNECTED());
//        Util.setLater(serverAddress, Messages.ServerMessages.ADDRESS(ip4Port));
        Util.setLater(serverAddress, Messages.ServerMessages.PORT(port));
    }

    private void setDisconnected() {
        Util.setLater(connectionButtonTextProperty(), Messages.ServerMessages.DISCONNECTED_BUTTON());
        Util.setLater(connectedLabelText, Messages.ServerMessages.DISCONNECTED());
        Util.setLater(serverAddress, Messages.ServerMessages.NULL_ADDRESS());
    }
}

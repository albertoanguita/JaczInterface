package jacz.face.state;

import jacz.peerengineservice.client.connection.State;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Collection of properties related to the connection state
 */
public class ConnectionState {

    private final SimpleObjectProperty<State.NetworkTopologyState> networkTopologyStateProperty;

    private final SimpleObjectProperty<State.ConnectionToServerState> connectionToServerStateProperty;

    private final SimpleObjectProperty<State.LocalServerConnectionsState> localServerConnectionStateProperty;

    private final SimpleIntegerProperty localPortProperty;

    private final SimpleIntegerProperty externalPortProperty;

    private final SimpleStringProperty localAddressProperty;

    private final SimpleStringProperty externalAddressProperty;

    public ConnectionState() {
        networkTopologyStateProperty = new SimpleObjectProperty<>(State.NetworkTopologyState.NO_DATA);
        connectionToServerStateProperty = new SimpleObjectProperty<>(State.ConnectionToServerState.DISCONNECTED);
        localServerConnectionStateProperty = new SimpleObjectProperty<>(State.LocalServerConnectionsState.CLOSED);
        localPortProperty = new SimpleIntegerProperty(-1);
        externalPortProperty = new SimpleIntegerProperty(-1);
        localAddressProperty = new SimpleStringProperty("");
        externalAddressProperty = new SimpleStringProperty("");
    }

    public final void updateState(State state) {
        networkTopologyStateProperty.set(state.getNetworkTopologyState());
        connectionToServerStateProperty.set(state.getConnectionToServerState());
        localServerConnectionStateProperty.set(state.getLocalServerConnectionsState());
        localPortProperty.set(state.getLocalPort());
        externalPortProperty.set(state.getExternalPort());
    }

    public final State.NetworkTopologyState getNetworkTopologyState() {
        return networkTopologyStateProperty.get();
    }

    public final SimpleObjectProperty<State.NetworkTopologyState> networkTopologyStateProperty() {
        return networkTopologyStateProperty;
    }

    public final State.ConnectionToServerState getConnectionToServerState() {
        return connectionToServerStateProperty.get();
    }

    public final SimpleObjectProperty<State.ConnectionToServerState> connectionToServerStateProperty() {
        return connectionToServerStateProperty;
    }

    public final State.LocalServerConnectionsState getLocalServerConnectionsState() {
        return localServerConnectionStateProperty.get();
    }

    public final SimpleObjectProperty<State.LocalServerConnectionsState> localServerConnectionsStateProperty() {
        return localServerConnectionStateProperty;
    }

    public final int getLocalPort() {
        return localPortProperty.get();
    }

    public final SimpleIntegerProperty localPortProperty() {
        return localPortProperty;
    }

    public final int getExternalPort() {
        return externalPortProperty.get();
    }

    public final SimpleIntegerProperty localExternalProperty() {
        return externalPortProperty;
    }

    public final String getLocalAddress() {
        return localAddressProperty.get();
    }

    public final SimpleStringProperty localAddressProperty() {
        return localAddressProperty;
    }

    public final String getExternalAddress() {
        return externalAddressProperty.get();
    }

    public final SimpleStringProperty externalAddressProperty() {
        return externalAddressProperty;
    }
}

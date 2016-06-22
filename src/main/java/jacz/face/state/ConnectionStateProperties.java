package jacz.face.state;

import jacz.face.util.Util;
import jacz.peerengineservice.client.connection.ConnectionState;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;

/**
 * Collection of properties related to the connection state
 */
public class ConnectionStateProperties extends GenericStateProperties {

    public enum AggregatedConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        DISCONNECTING,
        CONNECTED
    }

    public enum NetworkTopologyStateIssue {
        COULD_NOT_FETCH_LOCAL_ADDRESS,
        COULD_NOT_FETCH_EXTERNAL_ADDRESS
    }

    private final SimpleBooleanProperty isWishForConnectionProperty;

    private final SimpleObjectProperty<ConnectionState.NetworkTopologyState> networkTopologyStateProperty;

    private final ObjectProperty<NetworkTopologyStateIssue> networkTopologyStateIssue;

    private final SimpleObjectProperty<ConnectionState.LocalServerConnectionsState> localServerConnectionStateProperty;

    private final SimpleObjectProperty<ConnectionState.ConnectionToServerState> connectionToServerStateProperty;

    private final SimpleIntegerProperty localPortProperty;

    private final SimpleIntegerProperty externalPortProperty;

    private final SimpleStringProperty localAddressProperty;

    private final SimpleStringProperty externalAddressProperty;

    private final SimpleBooleanProperty hasGatewayProperty;

    private final SimpleObjectProperty<AggregatedConnectionStatus> aggregatedConnectionStatusProperty;

    public ConnectionStateProperties() {
        isWishForConnectionProperty = new SimpleBooleanProperty(false);
        networkTopologyStateProperty = new SimpleObjectProperty<>(ConnectionState.NetworkTopologyState.init());
        networkTopologyStateIssue = new SimpleObjectProperty<>(null);
        localServerConnectionStateProperty = new SimpleObjectProperty<>(ConnectionState.LocalServerConnectionsState.init());
        connectionToServerStateProperty = new SimpleObjectProperty<>(ConnectionState.ConnectionToServerState.init());
        localPortProperty = new SimpleIntegerProperty(-1);
        externalPortProperty = new SimpleIntegerProperty(-1);
        localAddressProperty = new SimpleStringProperty("");
        externalAddressProperty = new SimpleStringProperty("");
        hasGatewayProperty = new SimpleBooleanProperty(false);
        aggregatedConnectionStatusProperty = new SimpleObjectProperty<>(AggregatedConnectionStatus.DISCONNECTED);
        aggregatedConnectionStatusProperty.bind(new ObjectBinding<AggregatedConnectionStatus>() {
            {
                super.bind(isWishForConnectionProperty, networkTopologyStateProperty, localServerConnectionStateProperty, connectionToServerStateProperty);
            }

            @Override
            protected AggregatedConnectionStatus computeValue() {
                if (isWishForConnectionProperty.get()) {
                    if (connectionToServerStateProperty.get() == ConnectionState.ConnectionToServerState.CONNECTED) {
                        return AggregatedConnectionStatus.CONNECTED;
                    } else {
                        return AggregatedConnectionStatus.CONNECTING;
                    }
                } else {
                    if (localServerConnectionStateProperty.get() == ConnectionState.LocalServerConnectionsState.CLOSED) {
                        return AggregatedConnectionStatus.DISCONNECTED;
                    } else {
                        return AggregatedConnectionStatus.DISCONNECTING;
                    }
                }
            }
        });
    }

    public final void updateState(ConnectionState connectionState) {
        System.out.println(connectionState);
        Util.setLater(isWishForConnectionProperty, connectionState.isWishForConnection());
        Util.setLater(networkTopologyStateProperty, connectionState.getNetworkTopologyState());
        Util.setLater(connectionToServerStateProperty, connectionState.getConnectionToServerState());
        Util.setLater(localServerConnectionStateProperty, connectionState.getLocalServerConnectionsState());
        Util.setLater(localPortProperty, connectionState.getLocalPort());
        Util.setLater(externalPortProperty, connectionState.getExternalPort());
        Util.setLater(localAddressProperty, connectionState.getLocalAddress());
        Util.setLater(externalAddressProperty, connectionState.getExternalAddress());
        Util.setLater(hasGatewayProperty, connectionState.isHasGateway());
    }

    public void setNetworkTopologyStateIssue(NetworkTopologyStateIssue issue) {
        Util.setLater(networkTopologyStateIssue, issue);
    }

    public final boolean isWishForConnection() {
        return isWishForConnectionProperty.get();
    }

    public final SimpleBooleanProperty isWishForConnectionProperty() {
        return isWishForConnectionProperty;
    }

    public final ConnectionState.NetworkTopologyState getNetworkTopologyState() {
        return networkTopologyStateProperty.get();
    }

    public final SimpleObjectProperty<ConnectionState.NetworkTopologyState> networkTopologyStateProperty() {
        return networkTopologyStateProperty;
    }

    public final ConnectionState.LocalServerConnectionsState getLocalServerConnectionsState() {
        return localServerConnectionStateProperty.get();
    }

    public final SimpleObjectProperty<ConnectionState.LocalServerConnectionsState> localServerConnectionStateProperty() {
        return localServerConnectionStateProperty;
    }

    public final ConnectionState.ConnectionToServerState getConnectionToServerState() {
        return connectionToServerStateProperty.get();
    }

    public final SimpleObjectProperty<ConnectionState.ConnectionToServerState> connectionToServerStateProperty() {
        return connectionToServerStateProperty;
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

    public final boolean isHasGateway() {
        return hasGatewayProperty.get();
    }

    public final SimpleBooleanProperty hasGatewayProperty() {
        return hasGatewayProperty;
    }

    public final AggregatedConnectionStatus getAggregatedConnectionStatus() {
        return aggregatedConnectionStatusProperty.get();
    }

    public final SimpleObjectProperty<AggregatedConnectionStatus> aggregatedConnectionStatusProperty() {
        return aggregatedConnectionStatusProperty;
    }

}

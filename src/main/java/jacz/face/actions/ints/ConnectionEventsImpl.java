package jacz.face.actions.ints;

import jacz.face.state.ConnectionStateProperties;
import jacz.face.state.ConnectionToServerStatus;
import jacz.peerengineservice.client.connection.ConnectionEvents;
import jacz.peerengineservice.client.connection.ConnectionState;
import jacz.util.network.IP4Port;

/**
 * Connection events
 */
public class ConnectionEventsImpl implements ConnectionEvents {

    private final ConnectionToServerStatus connectionToServerStatus;
    private final ConnectionStateProperties connectionStateProperties;

    public ConnectionEventsImpl(ConnectionToServerStatus connectionToServerStatus, ConnectionStateProperties connectionStateProperties) {
        this.connectionToServerStatus = connectionToServerStatus;
        this.connectionStateProperties = connectionStateProperties;
    }

    @Override
    public void localPortModified(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void externalPortModified(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void initializingConnection(ConnectionState state) {
        System.out.println("initializing connection");
        connectionStateProperties.updateState(state);
    }

    @Override
    public void localAddressFetched(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void couldNotFetchLocalAddress(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToFetchExternalAddress(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void externalAddressFetched(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void couldNotFetchExternalAddress(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void unrecognizedMessageFromServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToConnectToServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void connectionToServerEstablished(ConnectionState state) {
        connectionToServerStatus.connectionEstablished(state);
        connectionStateProperties.updateState(state);
    }

    @Override
    public void registrationRequired(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void localServerUnreachable(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void unableToConnectToServer(ConnectionState state) {
        connectionToServerStatus.unableToConnect();
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToOpenLocalServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void localServerOpen(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void couldNotOpenLocalServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToCloseLocalServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void localServerClosed(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToCreateNATRule(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void NATRuleCreated(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void couldNotFetchUPNPGateway(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void errorCreatingNATRule(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToDestroyNATRule(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void NATRuleDestroyed(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void couldNotDestroyNATRule(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void listeningConnectionsWithoutNATRule(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void disconnectedFromServer(ConnectionState state) {
        connectionToServerStatus.disconnected();
        connectionStateProperties.updateState(state);
    }

    @Override
    public void failedToRefreshServerConnection(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void tryingToRegisterWithServer(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void registrationSuccessful(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void alreadyRegistered(ConnectionState state) {
        connectionStateProperties.updateState(state);
    }

    @Override
    public void peerCouldNotConnectToUs(Exception e, IP4Port ip4Port) {
        // todo
        System.out.println("Peer failed to connect to us from " + ip4Port.toString() + ". " + e.getMessage());
        //connectionStateProperties.updateState(state);
    }

    @Override
    public void localServerError(ConnectionState state, Exception e) {
        // todo
        System.out.println("Error in the peer connections listener. All connections closed. Error: " + e.getMessage());
        connectionStateProperties.updateState(state);
    }
}

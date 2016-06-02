package jacz.face.actions.ints;

import jacz.face.state.ConnectionState;
import jacz.face.state.ConnectionToServerStatus;
import jacz.peerengineservice.client.connection.ConnectionEvents;
import jacz.peerengineservice.client.connection.State;
import jacz.util.network.IP4Port;

/**
 * Connection events
 */
public class ConnectionEventsImpl implements ConnectionEvents {

    private final ConnectionToServerStatus connectionToServerStatus;
    private final ConnectionState connectionState;

    public ConnectionEventsImpl(ConnectionToServerStatus connectionToServerStatus, ConnectionState connectionState) {
        this.connectionToServerStatus = connectionToServerStatus;
        this.connectionState = connectionState;
    }

    @Override
    public void localPortModified(int port) {
        System.out.println("Local port modified: " + port);
        //connectionState.updateState(port);
    }

    @Override
    public void externalPortModified(int port) {
        System.out.println("External port modified: " + port);
        //connectionState.updateState(port);
    }

    @Override
    public void initializingConnection() {
        System.out.println("Initializing connection");
    }

    @Override
    public void localAddressFetched(String localAddress, State state) {
        System.out.println("Local address fetched. Local address: " + localAddress + ". State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void couldNotFetchLocalAddress(State state) {
        System.out.println("Could not fetch local address. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToFetchExternalAddress(State state) {
        System.out.println("Trying to fetch external address. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void externalAddressFetched(String externalAddress, boolean hasGateway, State state) {
        System.out.println("External address fetched. External address: " + externalAddress + ". Has gateway: " + hasGateway + ". State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void couldNotFetchExternalAddress(State state) {
        System.out.println("Could not fetch external address. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void unrecognizedMessageFromServer(State state) {
        System.out.println("Unrecognized message from server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToConnectToServer(State state) {
        System.out.println("Trying to connect to server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void connectionToServerEstablished(State state) {
        connectionToServerStatus.connectionEstablished(state);
        System.out.println("Connected to server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void registrationRequired(State state) {
        System.out.println("Registration with server required. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void localServerUnreachable(State state) {
        System.out.println("Local server unreachable. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void unableToConnectToServer(State state) {
        connectionToServerStatus.unableToConnect();
        System.out.println("Unable to connect to server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToOpenLocalServer(State state) {
        System.out.println("Trying to open Local server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void localServerOpen(State state) {
        System.out.println("Local server open. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void couldNotOpenLocalServer(State state) {
        System.out.println("Could not open local server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToCloseLocalServer(State state) {
        System.out.println("Trying to close local server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void localServerClosed(State state) {
        System.out.println("Local server closed. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToCreateNATRule(State state) {
        System.out.println("Trying to create NAT rule. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void NATRuleCreated(State state) {
        System.out.println("NAT rule created. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void couldNotFetchUPNPGateway(State state) {
        System.out.println("Could not fetch UPNP gateway. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void errorCreatingNATRule(State state) {
        System.out.println("Error creating NAT rule. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToDestroyNATRule(State state) {
        System.out.println("Trying to destroy NAT rule. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void NATRuleDestroyed(State state) {
        System.out.println("NAT rule destroyed. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void couldNotDestroyNATRule(State state) {
        System.out.println("Could not destroy NAT rule. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void listeningConnectionsWithoutNATRule(State state) {
        System.out.println("Listening connections without NAT rule. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void disconnectedFromServer(State state) {
        connectionToServerStatus.disconnected();
        System.out.println("Disconnected from server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void failedToRefreshServerConnection(State state) {
        System.out.println("Failed to refresh server connection. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void tryingToRegisterWithServer(State state) {
        System.out.println("Trying to register with server. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void registrationSuccessful(State state) {
        System.out.println("Registration with server successful. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void alreadyRegistered(State state) {
        System.out.println("Already registered. State: " + state);
        connectionState.updateState(state);
    }

    @Override
    public void peerCouldNotConnectToUs(Exception e, IP4Port ip4Port) {
        System.out.println("Peer failed to connect to us from " + ip4Port.toString() + ". " + e.getMessage());
        //connectionState.updateState(state);
    }

    @Override
    public void localServerError(Exception e) {
        System.out.println("Error in the peer connections listener. All connections closed. Error: " + e.getMessage());
        //connectionState.updateState(state);
    }
}

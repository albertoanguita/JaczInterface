package jacz.face.messages;


import jacz.face.state.ConnectionStateProperties;
import jacz.peerengineservice.client.connection.ConnectionState;
import org.aanguita.jacuzzi.network.IP4Port;

/**
 * Text messages to display in the GUI
 */
public final class Messages {

    public final static class ServerMessages {

        public static String CONNECTED_BUTTON() { return "Disconnect";}

        public static String DISCONNECTED_BUTTON() { return "Connect";}

        public static String CONNECTED() { return "Connected";}

        public static String CONNECTING() { return "Connecting";}

        public static String DISCONNECTING() { return "Disconnecting";}

        public static String DISCONNECTED() { return "Disconnected";}

        public static String NULL_ADDRESS() { return "-";}

        public static String ADDRESS(IP4Port ip4Port) { return ip4Port.toString();}
        public static String PORT(int port) { return "" + port;}


    }

    public static String networkTopologyStateMessages(ConnectionState.NetworkTopologyState networkTopologyState) {
        switch (networkTopologyState) {

            case NO_DATA:
                return "Unknown network topology";
            case FETCHING_DATA:
                return "Analyzing network topology";
            case WAITING_FOR_NEXT_LOCAL_ADDRESS_FETCH:
                return "Waiting for a new network topology analysis";
            case LOCAL_ADDRESS_FETCHED:
                return "Local ip address fetched";
            case WAITING_FOR_NEXT_EXTERNAL_ADDRESS_FETCH:
                return "Waiting for a new network topology analysis";
            case ALL_FETCHED:
                return "Network topology fetched";
            default:
                return null;
        }
    }

    public static String connectionToServerStateMessages(ConnectionState.ConnectionToServerState connectionToServerState) {
        switch (connectionToServerState) {
            case UNREGISTERED:
                return "Unregistered in server";
            case REGISTERING:
                return "Registering";
            case DISCONNECTED:
                return "Disconnected from server";
            case CONNECTING:
                return "Connecting to server";
            case CONNECTED:
                return "Connected to server";
            case WAITING_FOR_NEXT_CONNECTION_TRY:
                return "Waiting for a new connection try";
            default:
                return null;
        }
    }

    public static String localServerConnectionStateMessages(ConnectionState.LocalServerConnectionsState localServerConnectionsState) {
        switch (localServerConnectionsState) {
            case CLOSED:
                return "Local server closed";
            case OPENING:
                return "Opening local server";
            case WAITING_FOR_OPENING_TRY:
                return "Waiting for a new try to open local serve";
            case OPEN:
                return "Local server open";
            case CREATING_NAT_RULE:
                return "Creating NAT rule in gateway device";
            case WAITING_FOR_NAT_RULE_TRY:
                return "Waiting for a new try to create a NAT rule";
            case LISTENING:
                return "Local server open and accepting connections";
            case DESTROYING_NAT_RULE:
                return "Destroying NAT rule in gateway device";
            case CLOSING:
                return "Closing local server";
            default:
                return null;
        }
    }

    public static String networkTopologyIssueMessages(ConnectionStateProperties.ConnectionIssue issue) {
        if (issue == null) {
            return null;
        } else {
            switch (issue) {

                case COULD_NOT_FETCH_LOCAL_ADDRESS:
                    return "Unable to fetch local ip address";
                case COULD_NOT_FETCH_EXTERNAL_ADDRESS:
                    return "Unable to fetch public ip address";
                case UNRECOGNIZED_MESSAGE_FROM_SERVER:
                    return "Unrecognized message from server";
                case LOCAL_SERVER_UNREACHABLE:
                    return "Local server unreachable";
                case UNABLE_TO_CONNECT_TO_SERVER:
                    return "Unable to connect to server";
                case UNABLE_TO_OPEN_LOCAL_SERVER:
                    return "Unable to open local server";
                case FAILED_TO_REFRESH_SERVER_CONNECTION:
                    return "Failed to refresh server connection";
                case UNABLE_TO_FETCH_UPNP_SERVER:
                    return "Unable to fetch UPNP device";
                case ERROR_CREATING_NAT_RULE:
                    return "Failed to create NAT rule in the UPNP device";
                case ERROR_DESTROYING_NAT_RULE:
                    return "Failed to destroy NAT rule in the UPNP device";
                default:
                    return null;
            }
        }
    }
}

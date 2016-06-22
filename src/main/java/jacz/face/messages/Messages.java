package jacz.face.messages;


import jacz.face.state.ConnectionStateProperties;
import jacz.peerengineservice.client.connection.ConnectionState;
import jacz.util.network.IP4Port;

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

    public static String networkTopologyIssueMessages(ConnectionStateProperties.NetworkTopologyStateIssue issue) {
        if (issue == null) {
            return null;
        } else {
            switch (issue) {

                case COULD_NOT_FETCH_LOCAL_ADDRESS:
                    return "Unable to fetch local ip address";
                case COULD_NOT_FETCH_EXTERNAL_ADDRESS:
                    return "Unable to fetch public ip address";
                default:
                    return null;
            }
        }
    }
}

package jacz.face.controllers;

import jacz.face.state.PropertiesAccessor;
import jacz.peerengineclient.PeerEngineClient;

/**
 * Class that provides a singleton for accessing the peer engine client
 */
public class ClientAccessor {

    private final PeerEngineClient client;

    private static ClientAccessor instance;

    public static void setup(PeerEngineClient client) {
        instance = new ClientAccessor(client);
    }

    public static ClientAccessor getInstance() {
        return instance;
    }

    private ClientAccessor(PeerEngineClient client) {
        this.client = client;
    }

    public PeerEngineClient getClient() {
        return client;
    }
}

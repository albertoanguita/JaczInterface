package jacz.face.controllers;

import jacz.face.state.PropertiesAccessor;
import jacz.peerengineclient.PeerEngineClient;

/**
 * Created by alberto on 6/7/16.
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

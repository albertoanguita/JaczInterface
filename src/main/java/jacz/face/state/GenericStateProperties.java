package jacz.face.state;

import jacz.peerengineclient.PeerEngineClient;

/**
 * Created by Alberto on 13/06/2016.
 */
public class GenericStateProperties {

    protected PeerEngineClient client;

    public GenericStateProperties() {
        client = null;
    }

    public void setClient(PeerEngineClient client) {
        this.client = client;
    }
}

package jacz.face.actions.ints;

import jacz.face.state.GeneralStateProperties;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.client.GeneralEvents;

/**
 * Created by Alberto on 28/04/2016.
 */
public class GeneralEventsImpl implements GeneralEvents {

    private final GeneralStateProperties generalStateProperties;

    public GeneralEventsImpl(GeneralStateProperties generalStateProperties) {
        this.generalStateProperties = generalStateProperties;
    }

    @Override
    public void newOwnNick(String newNick) {
        generalStateProperties.updateOwnNick(newNick);
    }

    @Override
    public void newObjectMessage(PeerId peerID, Object message) {
        System.out.println("New object message from " + peerID + ": " + message);
    }

    @Override
    public void stop() {
        System.out.println("Stop");
    }
}

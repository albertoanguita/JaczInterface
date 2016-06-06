package jacz.face.actions.ints;

import jacz.commengine.communication.CommError;
import jacz.face.state.PeersStateProperties;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.client.connection.peers.PeerInfo;
import jacz.peerengineservice.client.connection.peers.PeersEvents;

/**
 * Created by Alberto on 28/04/2016.
 */
public class PeersEventsImpl implements PeersEvents {

    private final PeersStateProperties peersStateProperties;

    public PeersEventsImpl(PeersStateProperties peersStateProperties) {
        this.peersStateProperties = peersStateProperties;
    }

    @Override
    public void newPeerConnected(PeerId peerId, PeerInfo peerInfo) {
        System.out.println("New peer connected: " + formatPeer(peerId) + ", " + peerInfo);
        peersStateProperties.updatePeerInfo(peerId, peerInfo);
    }

    @Override
    public void modifiedPeerRelationship(PeerId peerId, PeerInfo peerInfo) {
        System.out.println("Modified peer relationship: " + formatPeer(peerId) + ", " + peerInfo);
        peersStateProperties.updatePeerInfo(peerId, peerInfo);
    }

    @Override
    public void newPeerNick(PeerId peerId, String nick, PeerInfo peerInfo) {
        System.out.println("Peer " + formatPeer(peerId) + " changed his nick to " + nick + ", " + peerInfo);
        peersStateProperties.updatePeerInfo(peerId, peerInfo);
    }

    @Override
    public void peerDisconnected(PeerId peerId, PeerInfo peerInfo, CommError error) {
        System.out.println("Peer disconnected (" + formatPeer(peerId) + "). Error = " + error);
        peersStateProperties.updatePeerInfo(peerId, peerInfo);
    }

    private String formatPeer(PeerId peerId) {
        return "{" + peerId.toString().substring(40) + "}";
    }
}

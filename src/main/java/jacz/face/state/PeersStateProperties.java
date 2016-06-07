package jacz.face.state;

import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.client.connection.peers.PeerInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Stores internal state referring to connected peers. Includes list of connected peers, and for each
 * peer their connection status (nick, relationship, ...)
 *
 * todo add sortedlist wrapper to sort the peers
 * todo move this data to the client api
 */
public class PeersStateProperties {

    public static class PeerPropertyInfo {

        public final PeerId peerId;

        public final PeerInfo peerInfo;

        public PeerPropertyInfo(PeerId peerId, PeerInfo peerInfo) {
            this.peerId = peerId;
            this.peerInfo = peerInfo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PeerPropertyInfo that = (PeerPropertyInfo) o;

            return peerId.equals(that.peerId);

        }

        @Override
        public int hashCode() {
            return peerId.hashCode();
        }
    }

    /**
     * Peers observed in this class
     */
    SortedList<PeerPropertyInfo> observedPeers;


    public PeersStateProperties() {
        observedPeers = new SortedList<>(FXCollections.observableArrayList());
    }

    public ObservableList<PeerPropertyInfo> observedPeers() {
        return observedPeers;
    }

    public void newPeerConnected(PeerId peerId, PeerInfo peerInfo) {
        observedPeers.add(new PeerPropertyInfo(peerId, peerInfo));
    }

    public void peerDisconnected(PeerId peerId, PeerInfo peerInfo) {
        // if not favorite nor blocked, remove from list
    }

    public void updatePeerInfo(PeerId peerId, PeerInfo peerInfo) {
        PeerPropertyInfo peerPropertyInfo = new PeerPropertyInfo(peerId, peerInfo);
        int index = observedPeers.indexOf(peerPropertyInfo);
        if (index >= 0) {
            // replace existing datum
            observedPeers.set(index, peerPropertyInfo);
        } else {
            // new item
            observedPeers.add(peerPropertyInfo);
        }
    }


}

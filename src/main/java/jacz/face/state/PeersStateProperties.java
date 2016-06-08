package jacz.face.state;

import jacz.face.controllers.ClientAccessor;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.client.connection.peers.PeerInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores internal state referring to connected peers. Includes list of connected peers, and for each
 * peer their connection status (nick, relationship, ...)
 *
 * todo add sortedlist wrapper to sort the peers
 */
public class PeersStateProperties {

    public static class PeerPropertyInfo2 {

        private final PeerId peerId;

        private final ObjectProperty<PeerId> peerIdProperty;

        private final StringProperty shortIdProperty;

        private final StringProperty relationProperty;

        private final StringProperty nickProperty;

        public PeerPropertyInfo2(PeerId peerId) {
            this.peerId = peerId;
            peerIdProperty = null;
            shortIdProperty = null;
            relationProperty = null;
            nickProperty = null;
        }

        public PeerPropertyInfo2(PeerInfo peerInfo) {
            peerId = peerInfo.peerId;
            peerIdProperty = new SimpleObjectProperty<>(peerInfo.peerId);
            shortIdProperty = new SimpleStringProperty(peerInfo.peerId.toString().substring(35));
            relationProperty = new SimpleStringProperty(peerInfo.relationship.toString());
            nickProperty = new SimpleStringProperty(peerInfo.nick);
        }

        public void update(PeerInfo peerInfo) {
            relationProperty.set(peerInfo.relationship.toString());
            nickProperty.set(peerInfo.nick);
        }

        public PeerId getPeerIdProperty() {
            return peerIdProperty.get();
        }

        public ObjectProperty<PeerId> peerIdPropertyProperty() {
            return peerIdProperty;
        }

        public String getShortIdProperty() {
            return shortIdProperty.get();
        }

        public StringProperty shortIdPropertyProperty() {
            return shortIdProperty;
        }

        public String getRelationProperty() {
            return relationProperty.get();
        }

        public StringProperty relationPropertyProperty() {
            return relationProperty;
        }

        public String getNickProperty() {
            return nickProperty.get();
        }

        public StringProperty nickPropertyProperty() {
            return nickProperty;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PeerPropertyInfo2 that = (PeerPropertyInfo2) o;

            return peerId.equals(that.peerId);
        }

        @Override
        public int hashCode() {
            return peerId.hashCode();
        }
    }

//    public static class PeerPropertyInfo {
//
//        public final PeerId peerId;
//
//        public final PeerInfo peerInfo;
//
//        public PeerPropertyInfo(PeerId peerId, PeerInfo peerInfo) {
//            this.peerId = peerId;
//            this.peerInfo = peerInfo;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            PeerPropertyInfo that = (PeerPropertyInfo) o;
//
//            return peerId.equals(that.peerId);
//
//        }
//
//        @Override
//        public int hashCode() {
//            return peerId.hashCode();
//        }
//    }



    /**
     * Peers observed in this class
     */
    ObservableList<PeerPropertyInfo2> observedPeers = null;



//    public PeersStateProperties() {
//        if (observedPeers == null) {
//            Stream<PeerId> firstPeers =
//                    Stream.concat(
//                            ClientAccessor.getInstance().getClient().getFavoritePeers().stream(),
//                            ClientAccessor.getInstance().getClient().getBlockedPeers().stream());
//            observedPeers = FXCollections.observableArrayList(
//                    firstPeers
//                            .map(p -> ClientAccessor.getInstance().getClient().getPeerInfo(p))
//                            .map(PeerPropertyInfo2::new)
//                            .collect(Collectors.toList()));
//        }
//    }

    public ObservableList<PeerPropertyInfo2> observedPeers() {
        if (observedPeers == null) {
            Stream<PeerId> firstPeers =
                    Stream.concat(
                            ClientAccessor.getInstance().getClient().getFavoritePeers().stream(),
                            ClientAccessor.getInstance().getClient().getBlockedPeers().stream());
            observedPeers = FXCollections.observableArrayList(
                    firstPeers
                            .map(p -> ClientAccessor.getInstance().getClient().getPeerInfo(p))
                            .map(PeerPropertyInfo2::new)
                            .collect(Collectors.toList()));
        }
        return observedPeers;
    }

    public void updatePeerInfo(PeerInfo peerInfo) {
        int index = indexOfPeerInfo(peerInfo);
        if (index >= 0) {
            if (includeInList(peerInfo)) {
                // update existing datum
                observedPeers.get(index).update(peerInfo);
            } else {
                // this peer is no longer observed
                observedPeers.remove(index);
            }
        } else {
            // new item
            if (includeInList(peerInfo)) {
                observedPeers.add(new PeerPropertyInfo2(peerInfo));
            }
        }
    }

    private int indexOfPeerInfo(PeerInfo peerInfo) {
        return observedPeers.indexOf(new PeerPropertyInfo2(peerInfo.peerId));
    }

    private static boolean includeInList(PeerInfo peerInfo) {
        return peerInfo.connected || peerInfo.relationship.isFavorite() || peerInfo.relationship.isBlocked();
    }
}

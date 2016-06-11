package jacz.face.state;

import com.neovisionaries.i18n.CountryCode;
import jacz.face.controllers.ClientAccessor;
import jacz.face.util.Util;
import jacz.peerengineservice.PeerId;
import jacz.peerengineservice.client.connection.peers.PeerInfo;
import jacz.peerengineservice.util.PeerRelationship;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores internal state referring to connected peers. Includes list of connected peers, and for each
 * peer their connection status (nick, relationship, ...)
 */
public class PeersStateProperties {

    public static class PeerPropertyInfo {

        private final PeerId peerIdValue;

        private final ObjectProperty<PeerId> peerId;

        private final BooleanProperty connected;

        private final ObjectProperty<PeerRelationship> relation;

        private final StringProperty nick;

        private final ObjectProperty<CountryCode> country;

        private final IntegerProperty affinity;

        private Date connectionDate;

        private final LongProperty secondsConnected;

        public PeerPropertyInfo(PeerId peerIdValue) {
            this.peerIdValue = peerIdValue;
            peerId = null;
            connected = null;
            relation = null;
            nick = null;
            country = null;
            affinity = null;
            secondsConnected = null;
        }

        public PeerPropertyInfo(PeerInfo peerInfo) {
            peerIdValue = peerInfo.peerId;
            peerId = new SimpleObjectProperty<>(peerInfo.peerId);
            connected = new SimpleBooleanProperty(peerInfo.connected);
            relation = new SimpleObjectProperty<>(peerInfo.relationship);
            nick = new SimpleStringProperty(peerInfo.nick);
            country = new SimpleObjectProperty<>(peerInfo.mainCountry);
            affinity = new SimpleIntegerProperty(peerInfo.affinity);
            connectionDate = peerInfo.connected ? peerInfo.lastConnectionDate : null;
            secondsConnected = peerInfo.connected ? new SimpleLongProperty(-1) : new SimpleLongProperty(0);
        }

        public void update(PeerInfo peerInfo) {
            connected.setValue(peerInfo.connected);
            relation.set(peerInfo.relationship);
            nick.set(peerInfo.nick);
            country.set(peerInfo.mainCountry);
            affinity.set(peerInfo.affinity);
            connectionDate = peerInfo.connected ? peerInfo.lastConnectionDate : null;
            updateSecondsConnected();
        }

        public void updateSecondsConnected() {
            secondsConnected.set((System.currentTimeMillis() - connectionDate.getTime()) / 1000);
        }

        public PeerId getPeerId() {
            return peerId.get();
        }

        public ObjectProperty<PeerId> peerIdProperty() {
            return peerId;
        }

        public boolean getConnected() {
            return connected.get();
        }

        public BooleanProperty connectedProperty() {
            return connected;
        }

        public PeerRelationship getRelation() {
            return relation.get();
        }

        public ObjectProperty<PeerRelationship> relationProperty() {
            return relation;
        }

        public String getNick() {
            return nick.get();
        }

        public StringProperty nickProperty() {
            return nick;
        }

        public CountryCode getCountry() {
            return country.get();
        }

        public ObjectProperty<CountryCode> countryProperty() {
            return country;
        }

        public int getAffinity() {
            return affinity.get();
        }

        public IntegerProperty affinityProperty() {
            return affinity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PeerPropertyInfo that = (PeerPropertyInfo) o;

            return peerIdValue.equals(that.peerIdValue);
        }

        @Override
        public int hashCode() {
            return peerIdValue.hashCode();
        }
    }




    /**
     * Peers observed in this class
     */
    private ObservableList<PeerPropertyInfo> observedPeers = null;

    private IntegerProperty totalFavoritePeers;

    private IntegerProperty connectedFavoritePeers;

    private IntegerProperty connectedRegularPeers;

    public PeersStateProperties() {
        observedPeers = null;
        totalFavoritePeers = new SimpleIntegerProperty(0);
        connectedFavoritePeers = new SimpleIntegerProperty(0);
        connectedRegularPeers = new SimpleIntegerProperty(0);
    }

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

    public ObservableList<PeerPropertyInfo> observedPeers() {
        if (observedPeers == null) {
            Stream<PeerId> firstPeers =
                    Stream.concat(
                            ClientAccessor.getInstance().getClient().getFavoritePeers().stream(),
                            ClientAccessor.getInstance().getClient().getBlockedPeers().stream());

            observedPeers = FXCollections.observableArrayList(new Callback<PeerPropertyInfo, Observable[]>() {
                @Override
                public Observable[] call(PeerPropertyInfo p) {
                    return new Observable[]{p.peerIdProperty(), p.connectedProperty(), p.relationProperty(), p.nickProperty(), p.countryProperty(), p.affinityProperty()};
                }
            });
            observedPeers.addAll(
                    firstPeers
                    .map(p -> ClientAccessor.getInstance().getClient().getPeerInfo(p))
                    .map(PeerPropertyInfo::new)
                    .collect(Collectors.toList()));
        }
        return observedPeers;
    }

    public IntegerProperty totalFavoritePeersProperty() {
        return totalFavoritePeers;
    }

    public IntegerProperty connectedFavoritePeersProperty() {
        return connectedFavoritePeers;
    }

    public IntegerProperty connectedRegularPeersProperty() {
        return connectedRegularPeers;
    }

    public void updatePeerInfo(PeerInfo peerInfo) {
        updateConnectionNumbers();
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
                observedPeers.add(new PeerPropertyInfo(peerInfo));
            }
        }
    }

    private void updateConnectionNumbers() {
        Util.setLater(totalFavoritePeers, ClientAccessor.getInstance().getClient().getFavoritePeers().size());
        int connectedFavoriteCount = 0;
        int connectedRegularCount = 0;
        for (PeerId peerId : ClientAccessor.getInstance().getClient().getConnectedPeers()) {
            if (ClientAccessor.getInstance().getClient().getPeerInfo(peerId).relationship.isFavorite()) {
                connectedFavoriteCount++;
            } else {
                connectedRegularCount++;
            }
        }
        Util.setLater(connectedFavoritePeers, connectedFavoriteCount);
        Util.setLater(connectedRegularPeers, connectedRegularCount);
    }

    private int indexOfPeerInfo(PeerInfo peerInfo) {
        return observedPeers.indexOf(new PeerPropertyInfo(peerInfo.peerId));
    }

    private static boolean includeInList(PeerInfo peerInfo) {
        return peerInfo.connected || peerInfo.relationship.isFavorite() || peerInfo.relationship.isBlocked();
    }
}

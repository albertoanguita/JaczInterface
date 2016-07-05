package jacz.face.state;

import jacz.peerengineclient.PeerEngineClient;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by alberto on 6/7/16.
 */
public class PropertiesAccessor {

    private static PropertiesAccessor instance = new PropertiesAccessor();

    private final AtomicBoolean isSetup;

    private GeneralStateProperties generalStateProperties;

    private ConnectionStateProperties connectionStateProperties;

    private PeersStateProperties peersStateProperties;

    private TransferStatsProperties transferStatsProperties;

    private MediaDatabaseProperties mediaDatabaseProperties;

    private FilesStateProperties filesStateProperties;

    private MediaPlayerProperties mediaPlayerProperties;

    public static PropertiesAccessor getInstance() {
        return instance;
    }

    private PropertiesAccessor() {
        isSetup = new AtomicBoolean(false);
        generalStateProperties = new GeneralStateProperties();
        connectionStateProperties = new ConnectionStateProperties();
        peersStateProperties = new PeersStateProperties();
        transferStatsProperties = new TransferStatsProperties();
        mediaDatabaseProperties = new MediaDatabaseProperties();
        filesStateProperties = new FilesStateProperties();
        mediaPlayerProperties = new MediaPlayerProperties();
    }

    public void setup(PeerEngineClient client) {
        if (!isSetup.getAndSet(true)) {
            generalStateProperties.setClient(client);
            connectionStateProperties.setClient(client);
            peersStateProperties.setClient(client);
            transferStatsProperties.setClient(client);
            mediaDatabaseProperties.setClient(client);
            filesStateProperties.setClient(client);
            mediaPlayerProperties.setClient(client);
        }
    }

    public void stop() {
        transferStatsProperties.stop();
    }

    public GeneralStateProperties getGeneralStateProperties() {
        return generalStateProperties;
    }

    public ConnectionStateProperties getConnectionStateProperties() {
        return connectionStateProperties;
    }

    public PeersStateProperties getPeersStateProperties() {
        return peersStateProperties;
    }

    public TransferStatsProperties getTransferStatsProperties() {
        return transferStatsProperties;
    }

    public MediaDatabaseProperties getMediaDatabaseProperties() {
        return mediaDatabaseProperties;
    }

    public FilesStateProperties getFilesStateProperties() {
        return filesStateProperties;
    }

    public MediaPlayerProperties getMediaPlayerProperties() {
        return mediaPlayerProperties;
    }
}

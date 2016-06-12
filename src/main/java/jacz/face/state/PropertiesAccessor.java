package jacz.face.state;

/**
 * Created by alberto on 6/7/16.
 */
public class PropertiesAccessor {

    private static PropertiesAccessor instance = new PropertiesAccessor();

    private GeneralStateProperties generalStateProperties;

    private ConnectionStateProperties connectionStateProperties;

    private PeersStateProperties peersStateProperties;

    private TransferStatsProperties transferStatsProperties;

    public static PropertiesAccessor getInstance() {
        return instance;
    }

    private PropertiesAccessor() {
        generalStateProperties = new GeneralStateProperties();
        connectionStateProperties = new ConnectionStateProperties();
        peersStateProperties = new PeersStateProperties();
        // todo
        transferStatsProperties = new TransferStatsProperties(null);
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
}

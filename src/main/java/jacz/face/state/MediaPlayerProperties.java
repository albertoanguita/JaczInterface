package jacz.face.state;

import jacz.peerengineclient.PeerEngineClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Properties related to available system media players, for playing media files
 */
public class MediaPlayerProperties extends GenericStateProperties {

    private static final String USE_VLC_KEY = "use-vlc";

    private static final String VLC_PATH_KEY = "vlc-path";

    private final BooleanProperty useVLC;

    private final StringProperty VLCPath;

    public MediaPlayerProperties() {
        useVLC = new SimpleBooleanProperty(false);
        VLCPath = new SimpleStringProperty(null);
    }

    public boolean getUseVLC() {
        return useVLC.get();
    }

    public BooleanProperty useVLCProperty() {
        return useVLC;
    }

    public String getVLCPath() {
        return VLCPath.get();
    }

    public StringProperty VLCPathProperty() {
        return VLCPath;
    }

    @Override
    public void setClient(PeerEngineClient client) {
        super.setClient(client);
        this.useVLC.setValue(getStoredUseVLC());
        this.VLCPath.setValue(getStoredVLCPath());
    }

    public void updateMediaPathProperty(boolean useVLC, String VLCPath) {
        client.getCustomStorage().setBoolean(USE_VLC_KEY, useVLC);
        client.getCustomStorage().setString(VLC_PATH_KEY, VLCPath);
        this.useVLC.setValue(useVLC);
        this.VLCPath.setValue(VLCPath);
    }

    public boolean getStoredUseVLC() {
        return client.getCustomStorage().containsItem(USE_VLC_KEY) ? client.getCustomStorage().getBoolean(USE_VLC_KEY) : false;
    }

    public String getStoredVLCPath() {
        return client.getCustomStorage().containsItem(VLC_PATH_KEY) ? client.getCustomStorage().getString(VLC_PATH_KEY) : null;
    }
}

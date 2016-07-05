package jacz.face.state;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by alberto on 7/5/16.
 */
public class MediaPlayerProperties extends GenericStateProperties {

    private final StringProperty VLCPath;

    public MediaPlayerProperties() {
        // todo read settings
        VLCPath = new SimpleStringProperty(null);
    }

    public String getVLCPath() {
        return VLCPath.get();
    }

    public StringProperty VLCPathProperty() {
        return VLCPath;
    }
}

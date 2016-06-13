package jacz.face.state;

import jacz.face.util.Util;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by alberto on 6/7/16.
 */
public class GeneralStateProperties extends GenericStateProperties {

    private final SimpleStringProperty ownNickProperty;

    public GeneralStateProperties() {
        ownNickProperty = new SimpleStringProperty();
    }

    public void updateOwnNick(String ownNick) {
        Util.setLater(ownNickProperty, ownNick);
    }

    public SimpleStringProperty ownNickPropertyProperty() {
        return ownNickProperty;
    }
}

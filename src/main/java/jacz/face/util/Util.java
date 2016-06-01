package jacz.face.util;

import javafx.application.Platform;
import javafx.beans.value.WritableObjectValue;

/**
 * utility Methods
 */
public class Util {

    public static <T> void setLater(final WritableObjectValue<T> property, final T value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                property.set(value);
            }
        });

    }
}

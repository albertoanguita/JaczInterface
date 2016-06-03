package jacz.face.util;

import javafx.application.Platform;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableIntegerValue;
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

    public static <T> void setLater(final WritableBooleanValue property, final boolean value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                property.set(value);
            }
        });

    }

    public static <T> void setLater(final WritableIntegerValue property, final int value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                property.set(value);
            }
        });

    }
}

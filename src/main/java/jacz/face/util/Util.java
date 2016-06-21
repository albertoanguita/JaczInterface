package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.util.GenreCode;
import javafx.application.Platform;
import javafx.beans.value.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility Methods
 */
public class Util {

    public static <T> void setLater(final WritableObjectValue<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    public static <T> void setLaterIf(final WritableObjectValue<T> property, final T value, boolean setLater) {
        if (setLater) {
            setLater(property, value);
        } else {
            property.set(value);
        }
    }

    public static <T> void setLater(final WritableBooleanValue property, final boolean value) {
        Platform.runLater(() -> property.set(value));
    }

//    public static <T> void setLater(final WritableIntegerValue property, final int value) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                property.set(value);
//            }
//        });
//    }
//
//    public static <T> void setLater(final WritableLongValue property, final long value) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                property.set(value);
//            }
//        });
//    }

    public static <T> void setLater(final WritableNumberValue property, final Number value) {
        Platform.runLater(() -> property.setValue(value));
    }

    public static void runLaterIf(Runnable action, boolean runLater) {
        if (runLater) {
            Platform.runLater(action);
        } else {
            action.run();
        }
    }

    public static List<String> getCountriesNames() {
        return Stream.of(CountryCode.values()).map(CountryCode::getName).sorted().collect(Collectors.toList());
    }

    public static CountryCode getCountryFromName(String countryName) {
        return CountryCode.findByName("^" + countryName + "$").get(0);
    }

    public static List<String> getGenresNames() {
        return Stream.of(GenreCode.values()).map(GenreCode::name).sorted().collect(Collectors.toList());
    }
}

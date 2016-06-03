package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import javafx.application.Platform;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableObjectValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<String> getCountriesNames() {
        return Stream.of(CountryCode.values()).map(CountryCode::getName).sorted().collect(Collectors.toList());
    }

    public static CountryCode getCountryFromName(String countryName) {
        return CountryCode.findByName("^" + countryName + "$").get(0);
    }
}

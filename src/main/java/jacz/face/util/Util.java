package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.ProducedCreationItem;
import jacz.database.util.GenreCode;
import jacz.database.util.QualityCode;
import jacz.face.controllers.ClientAccessor;
import javafx.application.Platform;
import javafx.beans.value.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
        return getCountriesNames(new ArrayList<>());
    }

    public static List<String> getCountriesNames(Collection<CountryCode> except) {
        return Stream.of(CountryCode.values()).filter(country -> !except.contains(country)).map(CountryCode::getName).sorted().collect(Collectors.toList());
    }

    public static CountryCode getCountryFromName(String countryName) {
        return CountryCode.findByName("^" + countryName + "$").get(0);
    }

    public static List<String> getGenresNames() {
        return Stream.of(GenreCode.values()).map(GenreCode::name).sorted().collect(Collectors.toList());
    }

    public static List<String> getGenresNames(Collection<GenreCode> except) {
        return Stream.of(GenreCode.values()).filter(genre -> !except.contains(genre)).map(GenreCode::name).sorted().collect(Collectors.toList());
    }

    public static void displayImage(Pane pane, String imagePath) {
        if (imagePath != null) {
            ImageView imageView = new ImageView();
            imageView.fitWidthProperty().bind(pane.widthProperty());
            imageView.fitHeightProperty().bind(pane.heightProperty());
            Image image = new Image(new File(imagePath).toURI().toString());
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            // todo remove style, set elsewhere
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
            pane.getChildren().addAll(imageView);
        } else {
            pane.getChildren().clear();
        }
    }

    public static List<String> getQualityNames() {
        return Stream.of(QualityCode.values()).map(QualityCode::name).sorted().collect(Collectors.toList());
    }

    public static String buildImagePath(ProducedCreationItem producedCreationItem) {
        String hash = producedCreationItem.getImageHash() != null ? producedCreationItem.getImageHash().getHash() : null;
        if (hash != null) {
            return ClientAccessor.getInstance().getClient().getFile(hash);
        } else {
            return null;
        }
    }


}

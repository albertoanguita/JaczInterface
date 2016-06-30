package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import jacz.database.ProducedCreationItem;
import jacz.database.util.GenreCode;
import jacz.database.util.LocalizedLanguage;
import jacz.database.util.QualityCode;
import jacz.face.controllers.ClientAccessor;
import javafx.application.Platform;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableNumberValue;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility Methods
 */
public class Util {

    public enum LocalizedLanguageFormat {
        NORMAL,
        SHORT,
        VERY_SHORT;

        public boolean languageIsShort() {
            return this == VERY_SHORT;
        }

        public boolean countryIsShort() {
            return this != NORMAL;
        }
    }

    public static <T> void setLater(final WritableObjectValue<T> property, final T value) {
        setLaterIf(property, value, false);
    }

    public static <T> void setLaterIf(final WritableObjectValue<T> property, final T value, boolean setLater) {
        runLaterIf(() -> property.set(value), setLater);
    }

    public static <T> void setLater(final WritableBooleanValue property, final boolean value) {
        setLaterIf(property, value, false);
    }

    public static <T> void setLaterIf(final WritableBooleanValue property, final boolean value, boolean setLater) {
        runLaterIf(() -> property.set(value), setLater);
    }

    public static <T> void setLater(final WritableNumberValue property, final Number value) {
        setLaterIf(property, value, false);
    }

    public static <T> void setLaterIf(final WritableNumberValue property, final Number value, boolean setLater) {
        runLaterIf(() -> property.setValue(value), setLater);
    }

    public static void runLaterIf(Runnable action, boolean runLater) {
        if (runLater) {
            Platform.runLater(action);
        } else {
            action.run();
        }
    }

    public static List<String> getCountriesNames() {
        return getCountriesNamesStream().collect(Collectors.toList());
    }

    public static List<String> getCountriesNames(String nullValue) {
        return Stream.concat(Stream.of(nullValue), getCountriesNamesStream()).collect(Collectors.toList());
    }

    public static List<String> getCountriesNames(Collection<CountryCode> except) {
        return getCountriesNamesStream(except).collect(Collectors.toList());
    }

    private static Stream<String> getCountriesNamesStream() {
        return Stream.of(CountryCode.values()).map(CountryCode::getName).sorted();
    }

    private static Stream<String> getCountriesNamesStream(Collection<CountryCode> except) {
        return Stream.of(CountryCode.values()).filter(country -> !except.contains(country)).map(CountryCode::getName).sorted();
    }

    public static CountryCode getCountryFromName(String countryName) {
        if (countryName == null) {
            return null;
        } else {
            List<CountryCode> foundCountries = CountryCode.findByName("^" + countryName + "$");
            return foundCountries.isEmpty() ? null : foundCountries.get(0);
        }
    }

    public static List<String> getLanguagesNames() {
        return getLanguagesNamesStream().collect(Collectors.toList());
    }

    public static List<String> getLanguagesNames(String nullValue) {
        return Stream.concat(Stream.of(nullValue), getLanguagesNamesStream()).collect(Collectors.toList());
    }

    public static List<String> getLanguagesNames(Collection<LanguageCode> except) {
        return getLanguagesNamesStream(except).collect(Collectors.toList());
    }

    private static Stream<String> getLanguagesNamesStream() {
        return Stream.of(LanguageCode.values()).map(LanguageCode::getName).sorted();
    }

    private static Stream<String> getLanguagesNamesStream(Collection<LanguageCode> except) {
        return Stream.of(LanguageCode.values()).filter(language -> !except.contains(language)).map(LanguageCode::getName).sorted();
    }

    public static LanguageCode getLanguageFromName(String languageName) {
        if (languageName == null) {
            return null;
        } else {
            List<LanguageCode> foundLanguages = LanguageCode.findByName("^" + languageName + "$");
            return foundLanguages.isEmpty() ? null : foundLanguages.get(0);
        }
    }

    public static List<String> getGenresNames() {
        return getGenresNamesStream().collect(Collectors.toList());
    }

    public static List<String> getGenresNames(String nullValue) {
        return Stream.concat(Stream.of(nullValue), getGenresNamesStream()).collect(Collectors.toList());
    }

    public static List<String> getGenresNames(Collection<GenreCode> except) {
        return getGenresNamesStream(except).collect(Collectors.toList());
    }

    private static Stream<String> getGenresNamesStream() {
        return Stream.of(GenreCode.values()).map(GenreCode::name).sorted();
    }

    private static Stream<String> getGenresNamesStream(Collection<GenreCode> except) {
        return Stream.of(GenreCode.values()).filter(genre -> !except.contains(genre)).map(GenreCode::name).sorted();
    }

    public static GenreCode getGenreFromName(String genreName) {
        try {
            return GenreCode.valueOf(genreName);
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }

    public static List<String> getQualitiesNames() {
        return getQualitiesNamesStream().collect(Collectors.toList());
    }

    public static List<String> getQualitiesNames(String nullValue) {
        return Stream.concat(Stream.of(nullValue), getQualitiesNamesStream()).collect(Collectors.toList());
    }

    public static List<String> getQualitiesNames(Collection<QualityCode> except) {
        return getQualitiesNamesStream(except).collect(Collectors.toList());
    }

    private static Stream<String> getQualitiesNamesStream() {
        return Stream.of(QualityCode.values()).map(QualityCode::name);
    }

    private static Stream<String> getQualitiesNamesStream(Collection<QualityCode> except) {
        return Stream.of(QualityCode.values()).filter(quality -> !except.contains(quality)).map(QualityCode::name);
    }

    public static QualityCode getQualityFromName(String qualityName) {
        try {
            return QualityCode.valueOf(qualityName);
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }

    public static String formatLocalizedLanguageList(List<LocalizedLanguage> localizedLanguageList, LocalizedLanguageFormat format) {
        StringBuilder result = new StringBuilder();
        for (LocalizedLanguage localizedLanguage : localizedLanguageList) {
            result.append(formatLocalizedLanguage(localizedLanguage, format)).append(", ");
        }
        return result.length() == 0 ? "" : result.substring(0, result.length() - 2);
    }

    public static String formatLocalizedLanguage(LocalizedLanguage localizedLanguage, LocalizedLanguageFormat format) {
        return localizedLanguage.country != null ?
                formatLanguage(localizedLanguage.language, format.languageIsShort()) + " (" + formatCountry(localizedLanguage.country, format.countryIsShort()) + ")" :
                formatLanguage(localizedLanguage.language, format.languageIsShort());
    }

    public static String formatLanguage(LanguageCode languageCode, boolean _short) {
        return _short ? languageCode.name() : languageCode.getName();
    }

    public static String formatCountry(CountryCode countryCode, boolean _short) {
        return _short ? countryCode.name() : countryCode.getName();
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

    public static Integer parseInteger(String text) {
        return text != null && !text.isEmpty() ? Integer.parseInt(text) : null;
    }
}

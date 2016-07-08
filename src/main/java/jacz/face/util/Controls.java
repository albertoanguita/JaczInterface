package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;
import jacz.database.util.GenreCode;
import jacz.database.util.LocalizedLanguage;
import org.aanguita.jacuzzi.lists.tuple.Duple;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * todo replace with list views that can be sorted with drag and drop (there are examples on the net)
 */
public class Controls {


//    private static class VideoFileCell extends TreeCell<VideoFile> {
//
//        public VideoFileCell() {
//        }
//
//        @Override
//        protected void updateItem(VideoFile file, boolean empty) {
//            // calling super here is very important - don't skip this!
//            super.updateItem(file, empty);
//            if (empty) {
//                this.setGraphic(null);
//            } else {
//                // build an anchor pane with an hbox inside it that stores all video file info fields
//                VBox nameAndHash = new VBox();
//                nameAndHash.getChildren().add(new Label(file.getName()));
//                nameAndHash.getChildren().add(new Label("Hash: " + file.getHash()));
//
//                Label size = new Label(file.getLength() + "B");
//
//                ChoiceBox<String> quality = new ChoiceBox<>(FXCollections.observableList(Util.getQualityNames()));
//                quality.setValue(file.getQuality().name());
//
//                HBox hBox = new HBox(nameAndHash, size, quality, );
//                AnchorPane rootPane = new AnchorPane(hBox);
//                this.setGraphic(rootPane);
//            }
//
//
//                // format the number as if it were a monetary value using the
//            // formatting relevant to the current locale. This would format
//            // 43.68 as "$43.68", and -23.67 as "-$23.67"
//            setText(file == null ? "" : file.getName());
//
//            // change the text fill based on whether it is positive (green)
//            // or negative (red). If the cell is selected, the text will
//            // always be white (so that it can be read against the blue
//            // background), and if the value is zero, we'll make it black.
////            if (item != null) {
////                double value = item.doubleValue();
////                setTextFill(isSelected() ? Color.WHITE :
////                        value == 0 ? Color.BLACK :
////                                value < 0 ? Color.RED : Color.GREEN);
////            }
//        }
//    }

    public static final String NO_LANGUAGE = " ";

    public static final String NO_COUNTRY = " ";

    public static void countryListPane(Pane pane, List<CountryCode> initialCountries) {
        pane.getChildren().clear();
        buildCloseableLabelList(pane, initialCountries, CountryCode::getName, ContentDisplay.RIGHT, "", "", () -> countryListPane(pane, initialCountries));

        ComboBox<String> addCountry = buildComboBoxWithAction(Util.getCountriesNames(initialCountries), 50d, countryName -> {
            CountryCode country = Util.getCountryFromName(countryName);
            initialCountries.add(country);
            countryListPane(pane, initialCountries);
        });
//        // todo does not currently work because the combo box is not editable
//        addCountry.setPromptText("new country");

        pane.getChildren().add(addCountry);
    }

    public static List<CountryCode> getSelectedCountries(Pane pane) {
        return getSelectedItems(pane, Util::getCountryFromName);
    }

    public static void stringListPane(Pane pane, List<String> stringList) {
        pane.getChildren().clear();
        buildCloseableLabelList(pane, stringList, str -> str, ContentDisplay.RIGHT, "", "", () -> stringListPane(pane, stringList));

        TextField newValueTextField = buildTextFieldWithAction(50d, newValue -> {
            stringList.add(newValue);
            stringListPane(pane, stringList);
            pane.getChildren().get(stringList.size()).requestFocus();
        });
        pane.getChildren().add(newValueTextField);
    }


    public static List<String> getSelectedStringValues(Pane pane) {
        return getSelectedItems(pane, s -> s);
    }

    public static void genreListPane(Pane pane, List<GenreCode> genres) {
        pane.getChildren().clear();
        buildCloseableLabelList(pane, genres, GenreCode::toString, ContentDisplay.RIGHT, "", "", () -> genreListPane(pane, genres));

        ComboBox<String> addGenre = buildComboBoxWithAction(Util.getGenresNames(genres), 50d, genreName -> {
            GenreCode genreCode = GenreCode.valueOf(genreName);
            genres.add(genreCode);
            genreListPane(pane, genres);
        });
        pane.getChildren().add(addGenre);
    }

    public static List<GenreCode> getSelectedGenres(Pane pane) {
        return getSelectedItems(pane, GenreCode::valueOf);
    }

    public static void localizedLanguageListPane(Pane pane, List<LocalizedLanguage> initialLanguages) {
        pane.getChildren().clear();
        buildCloseableLabelList(pane, initialLanguages, Controls::formatLocalizedLanguage, ContentDisplay.RIGHT, "", "", () -> localizedLanguageListPane(pane, initialLanguages));
        HBox newHBox = new HBox();
        Duple<ComboBox<String>, ComboBox<String>>  languageCountryComboBoxes = localizedLanguageEditor(newHBox, null, null);
        Button addButton = new Button("Add");
        addButton.disableProperty().bind(languageCountryComboBoxes.element1.valueProperty().isEqualTo(NO_LANGUAGE));
        addButton.setOnAction(event -> {
            LanguageCode language = Util.getLanguageFromName(languageCountryComboBoxes.element1.getValue());
            CountryCode country = Util.getCountryFromName(languageCountryComboBoxes.element2.getValue());
            initialLanguages.add(new LocalizedLanguage(language, country));
            localizedLanguageListPane(pane, initialLanguages);
        });
        newHBox.getChildren().add(addButton);
        pane.getChildren().add(newHBox);
    }

    public static List<LocalizedLanguage> parseLocalizedLanguageList(Pane pane) {
        return getSelectedItems(pane, Controls::parseLocalizedLanguage);
    }

    private static LocalizedLanguage parseLocalizedLanguage(String s) {
        String languageName;
        String countryName;
        if (s.contains(" (")) {
            String[] split = s.split(Pattern.quote(" ("));
            languageName = split[0];
            countryName = split[1].substring(0, split[1].length() - 1);
        } else {
            languageName = s;
            countryName = null;
        }
        return new LocalizedLanguage(Util.getLanguageFromName(languageName), Util.getCountryFromName(countryName));
    }

    public static Duple<ComboBox<String>, ComboBox<String>> emptyLocalizedLanguageEditor(Pane pane) {
        return localizedLanguageEditor(pane, null, null);
    }

    public static Duple<ComboBox<String>, ComboBox<String>> localizedLanguageEditor(Pane pane, LocalizedLanguage localizedLanguage) {
        return localizedLanguage != null ?
                localizedLanguageEditor(pane, localizedLanguage.language, localizedLanguage.country) :
                emptyLocalizedLanguageEditor(pane);
    }

    public static Duple<ComboBox<String>, ComboBox<String>> localizedLanguageEditor(Pane pane, LanguageCode languageCode, CountryCode countryCode) {
        pane.getChildren().clear();
        HBox languageHBox = new HBox();
        languageHBox.setSpacing(5d);
        ComboBox<String> languageComboBox = languageEditor(languageHBox, languageCode);
//        HBox languageHBox = languageEditor(localizedLanguage.language);
//        ComboBox<String> languageComboBox = (ComboBox<String>) languageHBox.getChildren().get(1);
        HBox countryHBox = new HBox();
        countryHBox.setSpacing(5d);
        ComboBox<String> countryComboBox = countryEditor(countryHBox, countryCode);
        countryHBox.disableProperty().bind(languageComboBox.valueProperty().isEqualTo(NO_LANGUAGE));
        pane.getChildren().addAll(languageHBox, countryHBox);
        return new Duple<>(languageComboBox, countryComboBox);
    }

    public static ComboBox<String> languageEditor(Pane pane, LanguageCode languageCode) {
        pane.getChildren().clear();
        Label title = new Label("Language");
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.setPrefWidth(50d);
        languageComboBox.getItems().addAll(FXCollections.observableList(Util.getLanguagesNames(NO_LANGUAGE)));
        languageComboBox.setValue(languageCode == null ? NO_LANGUAGE : languageCode.getName());
        pane.getChildren().addAll(title, languageComboBox);
        return languageComboBox;
//        hBox.setSpacing(5d);
//        return hBox;
    }

    public static ComboBox<String> countryEditor(Pane pane, CountryCode countryCode) {
        pane.getChildren().clear();
        Label title = new Label("Country (optional)");
        ComboBox<String> countryComboBox = new ComboBox<>();
        countryComboBox.setPrefWidth(50d);
        countryComboBox.getItems().addAll(FXCollections.observableList(Util.getCountriesNames(NO_COUNTRY)));
        countryComboBox.setValue(countryCode == null ? NO_COUNTRY : countryCode.getName());
        pane.getChildren().addAll(title, countryComboBox);
        return countryComboBox;
//        HBox hBox = new HBox(title, countryComboBox);
//        hBox.setSpacing(5d);
//        return hBox;
    }

    public static LocalizedLanguage parseLocalizedLanguage(Pane pane) {
        LanguageCode languageCode = parseLanguageEditor((HBox) pane.getChildren().get(0));
        CountryCode countryCode = parseCountryEditor((HBox) pane.getChildren().get(1));
        return languageCode != null ? new LocalizedLanguage(languageCode, countryCode) : null;
    }

    public static LanguageCode parseLanguageEditor(HBox hBox) {
        ComboBox<String> languageComboBox = (ComboBox<String>) hBox.getChildren().get(1);
        return Util.getLanguageFromName(languageComboBox.getValue());
    }

    public static CountryCode parseCountryEditor(HBox hBox) {
        ComboBox<String> countryComboBox = (ComboBox<String>) hBox.getChildren().get(1);
        return Util.getCountryFromName(countryComboBox.getValue());
    }

    private static String formatLocalizedLanguage(LocalizedLanguage localizedLanguage) {
        return localizedLanguage.country != null ?
                localizedLanguage.language.getName() + " (" + localizedLanguage.country.getName() + ")" :
                localizedLanguage.language.getName();
    }

    /*public static List<LocalizedLanguage> getSelectedLocalizedLanguages(Pane pane) {
        return getSelectedItems(pane, Util::getLanguageFromName);
    }*/


    private static <E> void buildCloseableLabelList(Pane pane, List<E> elements, Function<E, String> buildName, ContentDisplay contentDisplay, String labelClass, String buttonClass, Runnable rePaint) {
        for (int i = 0; i < elements.size(); i++) {
            E element = elements.get(i);
            int finalIndex = i;
            pane.getChildren().add(buildCloseableLabel(buildName.apply(element), contentDisplay, labelClass, buttonClass, () -> {
                elements.remove(finalIndex);
                rePaint.run();
            }));
        }
    }

    private static Label buildCloseableLabel(String value, ContentDisplay contentDisplay, String labelClass, String buttonClass, Runnable closeAction) {
        Button closeButton = new Button("X");
        closeButton.getStyleClass().add(buttonClass);
        closeButton.setOnAction(event -> closeAction.run());
        Label label = new Label(value);
        label.getStyleClass().add(labelClass);
        label.setGraphic(closeButton);
        label.setContentDisplay(contentDisplay);
        label.setMinWidth(label.getWidth());
        label.setMinHeight(label.getHeight());
        return label;
    }

    public static <T> ComboBox<T> buildComboBoxWithAction(List<T> items, double prefWidth, Consumer<T> onActionConsumer) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(prefWidth);
        comboBox.getItems().addAll(FXCollections.observableList(items));
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> onActionConsumer.accept(newValue));
        return comboBox;
    }

    public static TextField buildTextFieldWithAction(double prefWidth, Consumer<String> onEnterConsumer) {
        TextField textField = new TextField();
        textField.setPrefWidth(prefWidth);
        textField.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER) && !textField.getText().isEmpty()) {
                onEnterConsumer.accept(textField.getText());
                key.consume();
            }
        });
        return textField;
    }

    public static <E> List<E> getSelectedItems(Pane pane, Function<String, E> mapper) {
        return pane.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText())
                .map(mapper::apply)
                .collect(Collectors.toList());
    }

}

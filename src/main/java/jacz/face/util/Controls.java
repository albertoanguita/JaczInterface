package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.util.GenreCode;
import jacz.util.lists.tuple.Duple;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * todo replace with list views that can be sorted with drag and drop (there are examples on the net)
 */
public class Controls {

    public static void countryListPane(Pane pane, List<CountryCode> initialCountries) {
        for (int i = 0; i < initialCountries.size(); i++) {
            CountryCode country = initialCountries.get(i);
            //pane.getChildren().add(getCountryLabel(pane, initialCountries, i, country));
            pane.getChildren().add(getListLabel(pane, initialCountries, i, country.getName(), paneListDuple -> countryListPane(paneListDuple.element1, paneListDuple.element2)));
        }
        //ChoiceBox<String> addCountry = new ChoiceBox<>(FXCollections.observableList(Util.getCountriesNames()));


        ComboBox<String> addCountryC = new ComboBox<>();
        addCountryC.setPrefWidth(50d);
        addCountryC.getItems().addAll(FXCollections.observableList(Util.getCountriesNames(initialCountries)));
        addCountryC.valueProperty().addListener((observable, oldValue, newValue) -> {
            CountryCode country = Util.getCountryFromName(newValue);
            pane.getChildren().clear();
            initialCountries.add(country);
            countryListPane(pane, initialCountries);
        });
        // todo does not currently work because the combo box is not editable
        addCountryC.setPromptText("new country");

        pane.getChildren().add(addCountryC);
    }

    public static List<CountryCode> getSelectedCountries(Pane pane) {
        return getSelectedItems(pane, Util::getCountryFromName);
    }

    public static void stringListPane(Pane pane, List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            String value = stringList.get(i);
            pane.getChildren().add(getListLabel(pane, stringList, i, value, paneListDuple -> stringListPane(paneListDuple.element1, paneListDuple.element2)));
        }
        TextField newValueTextField = new TextField();
        newValueTextField.setPrefWidth(50d);
        newValueTextField.setOnKeyPressed(key -> {
            if (key.getCode().equals(KeyCode.ENTER) && !newValueTextField.getText().isEmpty()) {
                stringList.add(newValueTextField.getText());
                pane.getChildren().clear();
                stringListPane(pane, stringList);
                pane.getChildren().get(stringList.size()).requestFocus();
                key.consume();
                System.out.println(pane.getHeight());
            }
        });
        pane.getChildren().add(newValueTextField);
    }


    public static List<String> getSelectedStringValues(Pane pane) {
        return getSelectedItems(pane, s -> s);
    }

    public static void genreListPane(Pane pane, List<GenreCode> genres) {
        for (int i = 0; i < genres.size(); i++) {
            GenreCode genre = genres.get(i);
            //pane.getChildren().add(getCountryLabel(pane, initialCountries, i, country));
            pane.getChildren().add(getListLabel(pane, genres, i, genre.toString(), paneListDuple -> genreListPane(paneListDuple.element1, paneListDuple.element2)));
        }
        ChoiceBox<String> addGenre = new ChoiceBox<>(FXCollections.observableList(Util.getGenresNames()));
        addGenre.setPrefWidth(50d);
        addGenre.valueProperty().addListener((observable, oldValue, newValue) -> {
            GenreCode genreCode = GenreCode.valueOf(newValue);
            pane.getChildren().clear();
            genres.add(genreCode);
            genreListPane(pane, genres);
        });
        pane.getChildren().add(addGenre);
    }

    public static List<GenreCode> getSelectedGenres(Pane pane) {
        return getSelectedItems(pane, GenreCode::valueOf);
    }

    private static <E> Label getListLabel(Pane pane, List<E> list, int index, String value, Consumer<Duple<Pane, List<E>>> updatePane) {
        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> {
            pane.getChildren().clear();
            list.remove(index);
            updatePane.accept(new Duple<>(pane, list));
        });

        // In real life, use an external style sheet rather than inline styles:
        // I did it this way for brevity
        //closeButton.setStyle("-fx-font-size: 6pt; -fx-text-fill:red;");

        Label label = new Label(value);
        label.setGraphic(closeButton);
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setMinWidth(label.getWidth());
        label.setMinHeight(label.getHeight());
        return label;
    }

    public static <E> List<E> getSelectedItems(Pane pane, Function<String, E> mapper) {
        return pane.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText())
                .map(mapper::apply)
                .collect(Collectors.toList());
    }

}

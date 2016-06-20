package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import jacz.util.lists.tuple.Duple;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        ChoiceBox<String> addCountry = new ChoiceBox<>(FXCollections.observableList(Util.getCountriesNames()));
        addCountry.setPrefWidth(50d);
        addCountry.valueProperty().addListener((observable, oldValue, newValue) -> {
            CountryCode country = Util.getCountryFromName(newValue);
            pane.getChildren().clear();
            initialCountries.add(country);
            countryListPane(pane, initialCountries);
        });
        pane.getChildren().add(addCountry);
    }

    private static Label getCountryLabel(Pane pane, List<CountryCode> countries, int index, CountryCode country) {
        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> {
            pane.getChildren().clear();
            countries.remove(index);
            countryListPane(pane, countries);
        });

        // In real life, use an external style sheet rather than inline styles:
        // I did it this way for brevity
        //closeButton.setStyle("-fx-font-size: 6pt; -fx-text-fill:red;");

        Label label = new Label(country.getName());
        label.setGraphic(closeButton);
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setMinWidth(label.getWidth());
        label.setMinHeight(label.getHeight());
        return label;
    }

    public static List<CountryCode> getSelectedCountries(Pane pane) {
        List<CountryCode> countries = new ArrayList<>();
        pane.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> countries.add(Util.getCountryFromName(label.getText()))
                );
        return countries;
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

    private static <E> Label getListLabel(Pane pane, List<E> list, int index, String value, Consumer<Duple<Pane, List<E>>> updatePane) {
        Button closeButton = new Button("X");
        closeButton.setOnAction(event -> {
            pane.getChildren().clear();
            list.remove(index);
            updatePane.accept(new Duple<>(pane, list));
            //countryListPane(pane, countries);
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

    public static List<String> getSelectedStringValues(Pane pane) {
        List<String> list = new ArrayList<>();
        pane.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> list.add(label.getText())
                );
        return list;
    }

}

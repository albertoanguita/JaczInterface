package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by alberto on 6/15/16.
 */
public class SettingsController extends GenericController {

    public static class SettingsValues {

        public final int localPort;

        public final int externalPort;

        public final Integer maxDownloadSpeed;

        public final Integer maxUploadSpeed;

        public final boolean useRegularConnections;

        public final int maxRegularConnections;

        public final int maxRegularConnectionsForAdditionalCountries;

        public final CountryCode mainCountry;

        public final List<CountryCode> additionalCountries;

        public SettingsValues(
                int localPort,
                int externalPort,
                Integer maxDownloadSpeed,
                Integer maxUploadSpeed,
                boolean useRegularConnections,
                int maxRegularConnections,
                int maxRegularConnectionsForAdditionalCountries,
                CountryCode mainCountry, List<CountryCode> additionalCountries) {
            this.localPort = localPort;
            this.externalPort = externalPort;
            this.maxDownloadSpeed = maxDownloadSpeed;
            this.maxUploadSpeed = maxUploadSpeed;
            this.useRegularConnections = useRegularConnections;
            this.maxRegularConnections = maxRegularConnections;
            this.maxRegularConnectionsForAdditionalCountries = maxRegularConnectionsForAdditionalCountries;
            this.mainCountry = mainCountry;
            this.additionalCountries = additionalCountries;
        }

        @Override
        public String toString() {
            return "SettingsValues{" +
                    "localPort=" + localPort +
                    ", externalPort=" + externalPort +
                    ", maxDownloadSpeed=" + maxDownloadSpeed +
                    ", maxUploadSpeed=" + maxUploadSpeed +
                    ", useRegularConnections=" + useRegularConnections +
                    ", maxRegularConnections=" + maxRegularConnections +
                    ", maxRegularConnectionsForAdditionalCountries=" + maxRegularConnectionsForAdditionalCountries +
                    ", mainCountry=" + mainCountry.getName() +
                    ", additionalCountries=" + additionalCountries +
                    '}';
        }
    }

    @FXML
    private TextField localPortTextField;

    @FXML
    private TextField externalPortTextField;

    @FXML
    private TextField maxDownloadSpeedTextField;

    @FXML
    private TextField maxUploadSpeedTextField;

    @FXML
    private CheckBox useRegularConnectionsCheckBox;

    @FXML
    private TextField maxRegularConnectionsTextField;

    @FXML
    private TextField maxRegularConnectionsAddCountriesTextField;

    @FXML
    private ChoiceBox<String> mainCountryChoiceBox;

    @FXML
    private CheckComboBox<String> additionalCountriesCheckComboBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PeerEngineClient client = ClientAccessor.getInstance().getClient();
        localPortTextField.setText(Integer.toString(client.getLocalPort()));
        externalPortTextField.setText(Integer.toString(client.getExternalPort()));
        maxDownloadSpeedTextField.setText(formatMaxSpeed(client.getMaxDownloadSpeed()));
        maxUploadSpeedTextField.setText(formatMaxSpeed(client.getMaxUploadSpeed()));
        useRegularConnectionsCheckBox.setSelected(client.isWishForRegularConnections());
        maxRegularConnectionsTextField.setText(Integer.toString(client.getMaxRegularConnections()));
        maxRegularConnectionsAddCountriesTextField.setText(Integer.toString(client.getMaxRegularConnectionsForAdditionalCountries()));

        maxRegularConnectionsTextField.disableProperty().bind(useRegularConnectionsCheckBox.selectedProperty().not());
        maxRegularConnectionsAddCountriesTextField.disableProperty().bind(useRegularConnectionsCheckBox.selectedProperty().not());

        mainCountryChoiceBox.setItems(FXCollections.observableList(Util.getCountriesNames()));
        int indexOfMainCountry = Util.getCountriesNames().indexOf(client.getMainCountry().getName());
        SingleSelectionModel<String> selectionModel = mainCountryChoiceBox.getSelectionModel();
        selectionModel.select(indexOfMainCountry);

        IndexedCheckModel<String> cm = additionalCountriesCheckComboBox.getCheckModel();
        List<String> availableCountries = Util.getCountriesNames();
        additionalCountriesCheckComboBox.getItems().addAll(FXCollections.observableList(Util.getCountriesNames()));
        for (CountryCode additionalCountry : client.getAdditionalCountries()) {
            String countryName = additionalCountry.getName();
            int index = availableCountries.indexOf(countryName);
            cm.check(index);
        }
    }

    private static String formatMaxSpeed(Integer maxSpeed) {
        return maxSpeed != null ? maxSpeed.toString() : "";
    }

    public SettingsValues buildSettingsValues() {
        return new SettingsValues(
                parsePort(localPortTextField.getText()),
                parsePort(externalPortTextField.getText()),
                parseMaxSpeed(maxDownloadSpeedTextField.getText()),
                parseMaxSpeed(maxUploadSpeedTextField.getText()),
                useRegularConnectionsCheckBox.isSelected(),
                parseMaxConnections(maxRegularConnectionsTextField.getText()),
                parseMaxConnections(maxRegularConnectionsAddCountriesTextField.getText()),
                Util.getCountryFromName(mainCountryChoiceBox.getValue()),
                parseAdditionalCountries(additionalCountriesCheckComboBox));

    }

    private static int parsePort(String value) {
        return Integer.parseInt(value);
    }

    private static Integer parseMaxSpeed(String value) {
        return !value.isEmpty() ? Integer.parseInt(value) : null;
    }

    private static int parseMaxConnections(String value) {
        return Integer.parseInt(value);
    }

    private static List<CountryCode> parseAdditionalCountries(CheckComboBox<String> additionalCountriesCheckComboBox) {
        IndexedCheckModel<String> cm = additionalCountriesCheckComboBox.getCheckModel();
        List<String> availableCountries = Util.getCountriesNames();
        return cm.getCheckedIndices().stream().map(checkedIndex -> Util.getCountryFromName(availableCountries.get(checkedIndex))).collect(Collectors.toList());
    }
}

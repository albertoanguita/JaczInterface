package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.face.util.Controls;
import jacz.face.util.Util;
import jacz.peerengineclient.PeerEngineClient;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

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
    private ComboBox<String> mainCountryComboBox;

    @FXML
    private HBox additionalCountriesHBox;

    private ValidationSupport validationSupport;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PeerEngineClient client = ClientAccessor.getInstance().getClient();
        localPortTextField.setText(formatPort(client.getLocalPort()));
        externalPortTextField.setText(formatPort(client.getExternalPort()));
        maxDownloadSpeedTextField.setText(formatMaxSpeed(client.getMaxDownloadSpeed()));
        maxUploadSpeedTextField.setText(formatMaxSpeed(client.getMaxUploadSpeed()));
        useRegularConnectionsCheckBox.setSelected(client.isWishForRegularConnections());
        maxRegularConnectionsTextField.setText(Integer.toString(client.getMaxRegularConnections()));
        maxRegularConnectionsAddCountriesTextField.setText(Integer.toString(client.getMaxRegularConnectionsForAdditionalCountries()));

        maxRegularConnectionsTextField.disableProperty().bind(useRegularConnectionsCheckBox.selectedProperty().not());
        maxRegularConnectionsAddCountriesTextField.disableProperty().bind(useRegularConnectionsCheckBox.selectedProperty().not());

        mainCountryComboBox.setItems(FXCollections.observableList(Util.getCountriesNames()));
        mainCountryComboBox.setValue(client.getMainCountry().getName());

        Controls.countryListPane(additionalCountriesHBox, client.getAdditionalCountries());
//        IndexedCheckModel<String> cm = additionalCountriesCheckComboBox.getCheckModel();
//        List<String> availableCountries = Util.getCountriesNames();
//        additionalCountriesCheckComboBox.getItems().addAll(FXCollections.observableList(Util.getCountriesNames()));
//        for (CountryCode additionalCountry : client.getAdditionalCountries()) {
//            String countryName = additionalCountry.getName();
//            int index = availableCountries.indexOf(countryName);
//            cm.check(index);
//        }

        validationSupport = new ValidationSupport();
        validationSupport.registerValidator(localPortTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Empty for automatic, 1024 to 65535 otherwise", !localPortIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(externalPortTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "1024 to 65535", !externalPortIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(maxDownloadSpeedTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Empty for unlimited, numeric above 0 otherwise", !speedIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(maxUploadSpeedTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Empty for unlimited, numeric above 0 otherwise", !speedIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(maxUploadSpeedTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Empty for unlimited, numeric above 0 otherwise", !speedIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(maxRegularConnectionsTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Number from 0", !maxConnectionsIsCorrect(newValue));
            }
        });
        validationSupport.registerValidator(maxRegularConnectionsAddCountriesTextField, false, new Validator<String>() {
            @Override
            public ValidationResult apply(Control control, String newValue) {
                return ValidationResult.fromErrorIf(control, "Number from 0", !maxConnectionsIsCorrect(newValue));
            }
        });



    }

    public ReadOnlyBooleanProperty invalidProperty() {
        return validationSupport.invalidProperty();
    }

    private boolean localPortIsCorrect(String text) {
        if (text.isEmpty()) {
            return true;
        } else {
            try {
                int port = Integer.parseInt(text);
                return port >= 1024 && port <= 65535;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private boolean externalPortIsCorrect(String text) {
        try {
            int port = Integer.parseInt(text);
            return port >= 1024 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean speedIsCorrect(String text) {
        if (text.isEmpty()) {
            return true;
        } else {
            try {
                int port = Integer.parseInt(text);
                return port >= 1;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private boolean maxConnectionsIsCorrect(String text) {
        try {
            int port = Integer.parseInt(text);
            return port >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public SettingsValues buildSettingsValues() {
        return new SettingsValues(
                parsePort(localPortTextField.getText()),
                parsePort(externalPortTextField.getText()),
                parseSpeed(maxDownloadSpeedTextField.getText()),
                parseSpeed(maxUploadSpeedTextField.getText()),
                useRegularConnectionsCheckBox.isSelected(),
                parseMaxConnections(maxRegularConnectionsTextField.getText()),
                parseMaxConnections(maxRegularConnectionsAddCountriesTextField.getText()),
                Util.getCountryFromName(mainCountryComboBox.getValue()),
                Controls.getSelectedCountries(additionalCountriesHBox));

    }

    private static String formatPort(int value) {
        if (value == 0) {
            return "";
        } else {
            return Integer.toString(value);
        }
    }

    private static String formatMaxSpeed(Integer maxSpeed) {
        return maxSpeed != null ? maxSpeed.toString() : "";
    }

    private static int parsePort(String value) {
        if (value.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }

    private static Integer parseSpeed(String value) {
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

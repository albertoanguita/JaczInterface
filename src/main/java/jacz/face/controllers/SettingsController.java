package jacz.face.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by alberto on 6/15/16.
 */
public class SettingsController extends MainController {

    public static class SettingsValues {

        @Override
        public String toString() {
            return "SettingsValues{}";
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
}

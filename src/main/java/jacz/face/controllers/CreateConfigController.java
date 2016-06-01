package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.peerengineclient.SessionManager;
import jacz.peerengineservice.PeerId;
import jacz.util.lists.tuple.Duple;
import jacz.util.stochastic.MouseToRandom;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Alberto on 12/05/2016.
 */
public class CreateConfigController implements Initializable {


    @FXML
    public TextField nickBox;

    @FXML
    public ChoiceBox<CountryCode> countrySelector;


    @FXML
    ProgressBar progressBar;

//    @FXML
//    private Rectangle rectangle;

    @FXML
    private Label label;

    private MouseToRandom mouseToRandom;

    private boolean hasPrinted;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
//        assert rectangle != null : "fx:id=\"rectangle\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'IssueTrackingLite.fxml'.";
        mouseToRandom = new MouseToRandom(100);
        List<CountryCode> countries = Arrays.asList(CountryCode.values());
        countrySelector.setItems(FXCollections.observableList(countries));
        System.out.println(countrySelector.getValue());
        hasPrinted = false;
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        double progress = mouseToRandom.mouseCoords((int) mouseEvent.getX(), (int) mouseEvent.getY());
        progressBar.setProgress(progress);
        checkInput();
    }

    public void checkInput() {
        if (mouseToRandom.hasFinished() && !hasPrinted) {
            System.out.println("DONE!");
            label.setText("DONE!!!");
            progressBar.setStyle("-fx-accent: #00ff00");
//            PeerId peerID = PeerId.generateRandomPeerId(mouseToRandom.getRandomBytes());
//            System.out.println(peerID);
            hasPrinted = true;

            try {
                System.out.println(nickBox.getText());
                System.out.println(countrySelector.getValue());
                Duple<String, PeerId> dirAndPeerId = SessionManager.createUserConfig("./etc", mouseToRandom.getRandomBytes(), nickBox.getText(), countrySelector.getValue());
                System.out.println("User config created at " + dirAndPeerId.element1);
                System.out.println("With peer id: " + dirAndPeerId.element2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

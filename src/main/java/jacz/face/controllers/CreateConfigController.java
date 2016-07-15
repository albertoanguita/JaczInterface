package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.face.util.Util;
import jacz.peerengineclient.SessionManager;
import jacz.peerengineservice.PeerId;
import org.aanguita.jacuzzi.lists.tuple.Duple;
import org.aanguita.jacuzzi.stochastic.MouseToRandom;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Alberto on 12/05/2016.
 */
public class CreateConfigController extends GenericController {

    private static final String CUSTOM_STORAGE_VERSION_0_1 = "0.1";

    private static final String CUSTOM_STORAGE_CURRENT_VERSION = CUSTOM_STORAGE_VERSION_0_1;

    private static class ByteArrayWrapper {

        public final byte[] array;

        public ByteArrayWrapper(byte[] array) {
            this.array = array;
        }
    }

    private static class CreateProfileService extends Service<Duple<String, PeerId>> {

        private final StringProperty nick;

        private final ObjectProperty<CountryCode> country;

        private final ObjectProperty<ByteArrayWrapper> randomSeed;

        public CreateProfileService(String nick, CountryCode country, byte[] randomSeed) {
            this.nick = new ReadOnlyStringWrapper(nick);
            this.country = new ReadOnlyObjectWrapper<>(country);
            this.randomSeed = new ReadOnlyObjectWrapper<>(new ByteArrayWrapper(randomSeed));
        }

        @Override
        protected Task<Duple<String, PeerId>> createTask() {
            return new Task<Duple<String, PeerId>>() {
                @Override
                protected Duple<String, PeerId> call()
                        throws IOException {
                    return SessionManager.createUserConfig("./etc", randomSeed.get().array, nick.get(), country.get(), CUSTOM_STORAGE_CURRENT_VERSION);
                }
            };
        }
    }


    @FXML
    public TextField nickBox;

    @FXML
    public ComboBox<String> countrySelector;

    @FXML
    ProgressBar progressBar;

    @FXML
    private Button next;

    @FXML
    private MaskerPane masker;

    @FXML
    private Label nickQuestion;

    @FXML
    private Label seedQuestion;

    @FXML
    private Label countryQuestion;

    private MouseToRandom mouseToRandom;

    private BooleanProperty mouseFinishedProperty;

    private BooleanProperty profileCreatedProperty;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countrySelector.setItems(FXCollections.observableList(Util.getCountriesNames()));
        mouseFinishedProperty = new SimpleBooleanProperty(false);
        profileCreatedProperty = new SimpleBooleanProperty(false);

        next.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(mouseFinishedProperty, profileCreatedProperty, countrySelector.valueProperty());
            }

            @Override
            protected boolean computeValue() {
                return !mouseFinishedProperty.get() || profileCreatedProperty.get() || countrySelector.valueProperty().get() == null;
            }
        });

        mouseToRandom = new MouseToRandom(1024);


        nickQuestion.setGraphic(Glyph.create("FontAwesome|" + FontAwesome.Glyph.QUESTION_CIRCLE));
        seedQuestion.setGraphic(Glyph.create("FontAwesome|" + FontAwesome.Glyph.QUESTION_CIRCLE));
        countryQuestion.setGraphic(Glyph.create("FontAwesome|" + FontAwesome.Glyph.QUESTION_CIRCLE));
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        double progress = mouseToRandom.mouseCoords((int) mouseEvent.getX(), (int) mouseEvent.getY());
        progressBar.setProgress(progress);
        if (!mouseFinishedProperty.get() && mouseToRandom.hasFinished()) {
            progressBar.setStyle("-fx-accent: #00ff00");
            mouseFinishedProperty.setValue(true);
        }
    }

    public void createProfile() {
        profileCreatedProperty.set(true);
        masker.setVisible(true);

        CreateProfileService createProfileService = new CreateProfileService(nickBox.getText(), Util.getCountryFromName(countrySelector.getValue()), mouseToRandom.getRandomBytes());
        createProfileService.setOnSucceeded(t -> {
            Duple<String, PeerId> dirAndPeerId = (Duple<String, PeerId>) t.getSource().getValue();
            System.out.println("User config created at " + dirAndPeerId.element1);
            System.out.println("With peer id: " + dirAndPeerId.element2);
            masker.setVisible(false);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle(null);
            info.setHeaderText("Profile successfully created!");
            info.setResizable(true);
            info.setContentText("Your id is: " + dirAndPeerId.element2);
            info.getDialogPane().setPrefWidth(500);
            info.showAndWait();

            try {
                main.gotoMain();
            } catch (IOException e) {
                // todo
                e.printStackTrace();
            }

            System.out.println("DONE!");
        });
        createProfileService.start();
    }

}

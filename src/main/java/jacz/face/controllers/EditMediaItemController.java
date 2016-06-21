package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.Controls;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/20/16.
 */
public abstract class EditMediaItemController extends GenericController {

    public static class MediaItemData {

        public final String title;

        protected final String originalTitle;

        protected final Integer year;

        protected final String synopsis;

        protected final List<CountryCode> countries;

        protected final List<String> creators;

        protected final List<String> actors;

        public MediaItemData(String title, String originalTitle, Integer year, String synopsis, List<CountryCode> countries, List<String> creators, List<String> actors) {
            this.title = title;
            this.originalTitle = originalTitle;
            this.year = year;
            this.synopsis = synopsis;
            this.countries = countries;
            this.creators = creators;
            this.actors = actors;
        }
    }

    @FXML
    TextField titleTextField;

    @FXML
    TextField originalTitleTextField;

    @FXML
    TextField yearTextField;

    @FXML
    TextArea synopsisTextArea;

    @FXML
    HBox countriesHBox;

    @FXML
    FlowPane creatorsFlowPane;

    @FXML
    FlowPane actorsFlowPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);

        // todo get user intention from main
        if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
            // load the controls data from the edited item
            MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);

            titleTextField.setText(mediaItem.getTitle());
            titleTextField.setEditable(false);
            originalTitleTextField.setText(mediaItem.getOriginalTitle());
            yearTextField.setText(mediaItem.getYear() != null ? mediaItem.getYear().toString() : null);
            synopsisTextArea.setText(mediaItem.getSynopsis() != null ? mediaItem.getSynopsis() : null);
            Controls.countryListPane(countriesHBox, mediaItem.getCountries());
            Controls.stringListPane(creatorsFlowPane, mediaItem.getCreators());
            Controls.stringListPane(actorsFlowPane, mediaItem.getActors());
        } else {
            Controls.countryListPane(countriesHBox, new ArrayList<>());
            Controls.stringListPane(creatorsFlowPane, new ArrayList<>());
            Controls.stringListPane(actorsFlowPane, new ArrayList<>());
        }
    }

    public MediaItemData buildMediaItemData() {
        System.out.println("new synopsis: " + parseText(synopsisTextArea.getText()));
        return new MediaItemData(parseText(titleTextField.getText()), parseText(originalTitleTextField.getText()), parseInt(yearTextField.getText()), parseText(synopsisTextArea.getText()), Controls.getSelectedCountries(countriesHBox), Controls.getSelectedStringValues(creatorsFlowPane), Controls.getSelectedStringValues(actorsFlowPane));
    }

    public static Integer parseInt(String text) {
        return text != null ? Integer.parseInt(text) : null;
    }

    public static String parseText(String text) {
        return text != null && !text.isEmpty() ? text : null;
    }

    public static void changeMovie(Movie movie, MediaItemData mediaItemData) {
        movie.setOriginalTitle(mediaItemData.originalTitle);
        movie.setYear(mediaItemData.year);
        movie.setSynopsis(mediaItemData.synopsis);
        movie.setCountries(mediaItemData.countries);
        movie.setCreators(mediaItemData.creators);
        movie.setActors(mediaItemData.actors);
    }
}

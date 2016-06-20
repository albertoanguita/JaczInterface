package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.Movie;
import jacz.database.util.GenreCode;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends EditProducedMediaItemController {

    public static class MovieData extends ProducedMediaItemData {

        public final Integer minutes;

        public MovieData(String title, String originalTitle, Integer year, String synopsis, List<CountryCode> countries, List<String> creators, List<String> actors, List<String> companies, List<GenreCode> genres, Integer minutes) {
            super(title, originalTitle, year, synopsis, countries, creators, actors, companies, genres);
            this.minutes = minutes;
        }
    }

    @FXML
    Pane imagePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //super.initialize(url, resourceBundle);
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
        }
    }

    public MovieData buildMovieData() {
        return null;
        //return new MovieData(titleTextField.getText(), originalTitleTextField.getText(), Integer.parseInt(yearTextField.getText()));
    }

    public void chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set movie poster");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (selectedFile != null) {
            System.out.println(selectedFile);
            imagePane.setPrefWidth(140);
            imagePane.setPrefHeight(180);
            ImageView imageView = new ImageView();
            Image image = new Image(selectedFile.toURI().toString(), 140, 180, true, true);
            imageView.setImage(image);
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
            imagePane.getChildren().addAll(imageView);
        }

    }

    public static void changeMovie(Movie movie, MovieData movieData) {
        movie.setOriginalTitle(movieData.originalTitle);
        movie.setYear(movieData.year);
        ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

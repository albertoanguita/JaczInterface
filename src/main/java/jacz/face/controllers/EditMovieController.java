package jacz.face.controllers;

import jacz.database.Movie;
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
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends EditProducedMediaItemController {

    public static class MovieData extends ProducedMediaItemData {

        private final Integer minutes;

        public MovieData(ProducedMediaItemData producedMediaItemData, Integer minutes) {
            super(producedMediaItemData, producedMediaItemData.companies, producedMediaItemData.genres, producedMediaItemData.imagePath);
            this.minutes = minutes;
        }
    }

    @FXML
    Pane imagePane;

    @FXML
    private TextField minutesTextField;

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
            minutesTextField.setText(mediaItem.getMinutes() != null ? mediaItem.getMinutes().toString() : null);
        }
    }

    public MovieData buildMovieData() {
        return new MovieData(buildProducedMediaItemData(), Integer.parseInt(minutesTextField.getText()));
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
        EditProducedMediaItemController.changeMovie(movie, movieData);
        movie.setMinutes(movieData.minutes);
        ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

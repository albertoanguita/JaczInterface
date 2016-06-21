package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
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
        return new MovieData(buildProducedMediaItemData(), EditMovieController.parseInt(minutesTextField.getText()));
    }

    public static DatabaseItem changeMovie(Movie movie, MovieData movieData) throws IOException {
        EditProducedMediaItemController.changeMovie(movie, movieData);
        movie.setMinutes(movieData.minutes);
        return ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

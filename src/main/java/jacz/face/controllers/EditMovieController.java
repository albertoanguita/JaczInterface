package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.VideoFilesEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends EditProducedCreationItemController {

    public static class MovieData extends ProducedMediaItemData {

        private final Integer minutes;

        public MovieData(ProducedMediaItemData producedMediaItemData, Integer minutes) {
            super(producedMediaItemData, producedMediaItemData.companies, producedMediaItemData.genres, producedMediaItemData.imagePath);
            this.minutes = minutes;
        }
    }

    @FXML
    private TextField minutesTextField;

//    @FXML
//    private ListView<MediaDatabaseProperties.VideoFileModel> filesListView;

    @FXML
    private Button newVideoFileButton;

    @FXML
    private VBox filesListVBox;

    @Override
    public void setMainAndItem(Main main, DatabaseItem item) {
        super.setMainAndItem(main, item);

        // todo get user intention from main
        //if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
        if (item != null) {
            // load the controls data from the edited item
            Movie movie = (Movie) item;
            //MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);

//            titleTextField.setText(movie.getTitle());
//            titleTextField.setEditable(false);
//            originalTitleTextField.setText(movie.getOriginalTitle());
            minutesTextField.setText(movie.getMinutes() != null ? movie.getMinutes().toString() : null);

            // retrieve movie object for populating the video files editor pane
            //Movie movie = Movie.getMovieById(Cl)

            VideoFilesEditor.populateVideoFilesPane(filesListVBox, newVideoFileButton, main, movie, movie.getVideoFiles());
        }
    }

    public MovieData buildMovieData() {
        return new MovieData(buildProducedMediaItemData(), EditMovieController.parseInt(minutesTextField.getText()));
    }

    public static DatabaseItem changeMovie(Movie movie, MovieData movieData) throws IOException {
        EditProducedCreationItemController.changeProducedCreationItem(movie, movieData);
        movie.setMinutes(movieData.minutes);
        return ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

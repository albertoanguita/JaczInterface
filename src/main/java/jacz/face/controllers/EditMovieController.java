package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.main.Main;
import jacz.face.util.VideoFilesEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends EditProducedCreationItemController {

    public static class MovieData extends ProducedMediaItemData {

        private final Integer minutes;

        private final VideoFilesEditor.UpdateResult updatedVideoFiles;

        public MovieData(ProducedMediaItemData producedMediaItemData, Integer minutes, VideoFilesEditor.UpdateResult updatedVideoFiles) {
            super(producedMediaItemData, producedMediaItemData.companies, producedMediaItemData.genres, producedMediaItemData.imagePath);
            this.minutes = minutes;
            this.updatedVideoFiles = updatedVideoFiles;
        }
    }

    @FXML
    private TextField minutesTextField;

//    @FXML
//    private ListView<MediaDatabaseProperties.VideoFileModel> filesListView;

    @FXML
    private Button newVideoFileButton;

    @FXML
    private Tab filesTab;

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
        } else {
            // remove the files pane. We do not have a movie yet, so we cannot know where to place new files
            filesTab.setDisable(true);
        }
    }

    public MovieData buildMovieData(Movie movie) {
        List<VideoFile> oldVideoFiles = movie != null ? movie.getVideoFiles() : new ArrayList<>();
        VideoFilesEditor.UpdateResult updatedVideoFiles = VideoFilesEditor.updateVideoFiles(filesListVBox, oldVideoFiles, ClientAccessor.getInstance().getClient().getDatabases().getLocalDB());
        return new MovieData(buildProducedMediaItemData(), EditMovieController.parseInt(minutesTextField.getText()), updatedVideoFiles);
    }

    public static DatabaseItem changeMovie(Movie movie, MovieData movieData) throws IOException {
        // todo use deferred changes
        EditProducedCreationItemController.changeProducedCreationItem(movie, movieData);
        movie.setMinutes(movieData.minutes);
        if (movieData.updatedVideoFiles.change) {
            System.out.println("new movies: " + movieData.updatedVideoFiles.videoFiles.size());
            movie.setVideoFiles(movieData.updatedVideoFiles.videoFiles);
        }
        return ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

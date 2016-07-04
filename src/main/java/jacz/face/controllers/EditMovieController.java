package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.face.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends EditProducedCreationItemController {

    public static class MovieData extends ProducedMediaItemData {

        private final Integer minutes;

//        private final VideoFilesEditor.UpdateResult updatedVideoFiles;

        public MovieData(ProducedMediaItemData producedMediaItemData, Integer minutes) {
            super(producedMediaItemData, producedMediaItemData.companies, producedMediaItemData.genres, producedMediaItemData.imagePath);
            this.minutes = minutes;
//            this.updatedVideoFiles = updatedVideoFiles;
        }
    }

    @FXML
    private TextField minutesTextField;

//    @FXML
//    private ListView<MediaDatabaseProperties.VideoFileModel> filesListView;

    @Override
    public void setMainAndItem(Main main, DatabaseItem item) throws ItemNoLongerExistsException {
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

//            VideoFilesEditor.populateVideoFilesPane(filesListVBox, newVideoFileButton, main, movie, movie.getVideoFiles());
        }
    }

    public MovieData buildMovieData(Movie movie) {
//        List<VideoFile> oldVideoFiles = movie != null ? movie.getVideoFiles() : new ArrayList<>();
//        VideoFilesEditor.UpdateResult<VideoFile> updatedVideoFiles = VideoFilesEditor.updateVideoFiles(filesListVBox, oldVideoFiles, ClientAccessor.getInstance().getClient().getDatabases().getLocalDB());
        return new MovieData(buildProducedMediaItemData(), EditMovieController.parseInt(minutesTextField.getText()));
    }

    public static DatabaseItem changeMovie(Movie movie, MovieData movieData) throws IOException {
        // todo use deferred changes
        EditProducedCreationItemController.changeProducedCreationItem(movie, movieData);
        movie.setMinutes(movieData.minutes);
//        if (movieData.updatedVideoFiles.change) {
//            System.out.println("new movies: " + movieData.updatedVideoFiles.files.size());
//            movie.setVideoFiles(movieData.updatedVideoFiles.files);
//        }
        return ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

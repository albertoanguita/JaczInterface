package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.main.Main;
import jacz.face.util.VideoFilesEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Alberto on 30/06/2016.
 */
public class EditFilesController extends GenericControllerWithItem {


    @FXML
    private Button newVideoFileButton;

    @FXML
    private VBox filesListVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMainAndItem(Main main, DatabaseItem item) {
        super.setMainAndItem(main, item);

        Movie movie = (Movie) item;
        VideoFilesEditor.populateVideoFilesPane(filesListVBox, newVideoFileButton, main, movie, movie.getVideoFiles());
    }

    public VideoFilesEditor.UpdateResult<VideoFile> buildUpdatedVideoFiles(Movie movie) {
        List<VideoFile> oldVideoFiles = movie != null ? movie.getVideoFiles() : new ArrayList<>();
        return VideoFilesEditor.updateVideoFiles(filesListVBox, oldVideoFiles, ClientAccessor.getInstance().getClient().getDatabases().getLocalDB());
    }

    public static DatabaseItem changeMovie(Movie movie, VideoFilesEditor.UpdateResult<VideoFile> updatedVideoFiles) throws IOException {
        if (updatedVideoFiles.change) {
            System.out.println("new movies: " + updatedVideoFiles.files.size());
            movie.setVideoFiles(updatedVideoFiles.files);
        }
        return ClientAccessor.getInstance().getClient().localItemModified(movie);
    }
}

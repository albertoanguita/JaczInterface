package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.main.Main;
import jacz.face.util.VideoFilesEditor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto on 30/06/2016.
 */
public class EditFilesController extends GenericEditDialogController {


    @FXML
    private Button newVideoFileButton;

    @FXML
    private VBox filesListVBox;

    @Override
    public void setMainItemAndMasker(Main main, DatabaseItem item, Pane rootPane) throws ItemNoLongerExistsException {
        super.setMainItemAndMasker(main, item, rootPane);

        Movie movie = (Movie) item;
        //maskerPane.setText("Processing file...");
        VideoFilesEditor.populateVideoFilesPane(filesListVBox, newVideoFileButton, rootPane, main, movie, movie.getVideoFiles());
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

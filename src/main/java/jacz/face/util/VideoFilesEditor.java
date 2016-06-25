package jacz.face.util;

import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.controllers.ClientAccessor;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.util.lists.tuple.Duple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by alberto on 6/24/16.
 */
public class VideoFilesEditor {

    private static class VideoFileModelCellFactory extends ListCell<MediaDatabaseProperties.VideoFileModel> {

        public VideoFileModelCellFactory() {
        }

        @Override
        protected void updateItem(MediaDatabaseProperties.VideoFileModel videoFileModel, boolean empty) {
            // calling super here is very important - don't skip this!
            super.updateItem(videoFileModel, empty);

            if (videoFileModel == null || empty) {
                setGraphic(null);
            } else {
                // for each video file model (representing one video file), we paint an VBox with two nodes. The first
                // one is an HBox with the basic info of the video file (name, hash, length...). The second one is a
                // list view in which each element represents one subtitle file.
                // todo add a list view above subtitles for the additional sources
                Label nameLabel = new Label(formatName(videoFileModel.getName()));
                Label hashLabel = new Label(formatHash(videoFileModel.getHash()));
                Label sizeLabel = new Label(formatSize(videoFileModel.getLength()));
                VBox nameHash = new VBox(nameLabel, hashLabel);
                HBox basicInfo = new HBox(nameHash, sizeLabel);
                basicInfo.setAlignment(Pos.CENTER_LEFT);
                basicInfo.setSpacing(3d);
                setGraphic(new VBox(basicInfo));
            }
        }
    }


    public static ObservableList<MediaDatabaseProperties.VideoFileModel> populateVideoFilesListView(ListView<MediaDatabaseProperties.VideoFileModel> listView, Button button, Main main, Movie movie, List<MediaDatabaseProperties.VideoFileModel> videoFiles) {
        listView.getItems().clear();
        ObservableList<MediaDatabaseProperties.VideoFileModel> observableVideoFiles = FXCollections.observableList(videoFiles);
        listView.getItems().addAll(observableVideoFiles);
        listView.setCellFactory(videoFileModel -> new VideoFileModelCellFactory());
        setupNewFileButton(button, main, movie, observableVideoFiles);
        return observableVideoFiles;
    }

//    private static ObservableList<MediaDatabaseProperties.VideoFileModel> buildVideoFilesList(List<MediaDatabaseProperties.VideoFileModel> videoFiles) {
//        return FXCollections.observableList(videoFiles.stream()
//                .map(VideoFilesEditor::buildVideoFileVBoxFromModel)
//                .collect(Collectors.toList()));
//    }

    private static VBox buildVideoFileVBoxFromModel(MediaDatabaseProperties.VideoFileModel videoFileModel) {
        Label nameLabel = new Label(formatName(videoFileModel.getName()));
        Label hashLabel = new Label(formatHash(videoFileModel.getHash()));
        Label sizeLabel = new Label(formatSize(videoFileModel.getLength()));
        VBox nameHash = new VBox(nameLabel, hashLabel);
        HBox basicInfo = new HBox(nameHash, sizeLabel);
        basicInfo.setAlignment(Pos.CENTER_LEFT);
        basicInfo.setSpacing(3d);
        return new VBox(basicInfo);
    }

    private static String formatName(String name) {
        return name != null ? name : "unnamed file";
    }

    private static String formatHash(String hash) {
        hash = hash != null ? hash : "";
        return "(MD5: " + hash + ")";
    }

    private static String formatSize(Long length) {
        return length == null ? "?" : length.toString() + " bytes";
    }

    private static void setupNewFileButton(Button button, Main main, Movie movie, ObservableList<MediaDatabaseProperties.VideoFileModel> observableVideoFiles) {
        button.setOnAction(event -> {
            // open a file selector. When a file is selected, load it into the file hash database and use its
            // metadata to populate a new VideoFileModel object that is added to the existing ones
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a video file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Video Files", "*.avi", "*.mkv", "*.mp4"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            java.io.File selectedMovieFile = fileChooser.showOpenDialog(main.getPrimaryStage());
            if (selectedMovieFile != null) {
                MediaDatabaseProperties.VideoFileModel videoFileModel = addNewVideoFile(selectedMovieFile.toString(), movie);
                observableVideoFiles.add(videoFileModel);
            }
        });
    }

    private static MediaDatabaseProperties.VideoFileModel addNewVideoFile(String newMovieFile, Movie movie) {
        try {
            Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalMovieFile(newMovieFile, movie, true);
            MediaDatabaseProperties.VideoFileModel videoFileModel = new MediaDatabaseProperties.VideoFileModel(pathAndHash.element2);
            // todo update automatic metadata (did not find a suitable api for this, skipping...)
            videoFileModel.setLength(new java.io.File(pathAndHash.element1).length());
            return videoFileModel;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<VideoFile> updateVideoFiles(ListView<VBox> listView, List<VideoFile> oldVideoFiles, Consumer<List<VideoFile>> setVideoFiles, String dbPath) {
        // the list of video files models will be checked against the given video files. The new video file models
        // will produce a new video file. For those that already exist, we will check if there are changes, and
        // update the found changes
        // Finally, we will set the whole list of remaining and updated video files using the given consumer function
        List<VideoFile> newVideoFiles = listView.getItems().stream()
                .map(videoFileModelVBox -> buildVideoFile(videoFileModelVBox, oldVideoFiles, dbPath))
                .collect(Collectors.toList());
        setVideoFiles.accept(newVideoFiles);
        return newVideoFiles;
    }

    private static VideoFile buildVideoFile(VBox videoFileModelVBox, List<VideoFile> oldVideoFiles, String dbPath) {
        MediaDatabaseProperties.VideoFileModel videoFileModel = buildVideoFileModel(videoFileModelVBox);
        VideoFile videoFile = findVideoFile(videoFileModel.hash, oldVideoFiles);
        if (videoFile == null) {
            // the video file model was not found in the given list of video files -> build a new Video File
            videoFile = new VideoFile(dbPath, videoFileModel.hash);
        }
        updateVideoFile(videoFile, videoFileModel);
        return null;
    }

    private static MediaDatabaseProperties.VideoFileModel buildVideoFileModel(VBox videoFileModelVBox) {
        return null;
    }

    private static VideoFile findVideoFile(String hash, List<VideoFile> videoFiles) {
        for (VideoFile videoFile : videoFiles) {
            if (hash.equals(videoFile.getHash())) {
                return videoFile;
            }
        }
        return null;
    }

    private static void updateVideoFile(VideoFile videoFile, MediaDatabaseProperties.VideoFileModel videoFileModel) {
        // todo
    }

}

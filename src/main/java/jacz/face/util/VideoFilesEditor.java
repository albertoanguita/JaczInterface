package jacz.face.util;

import jacz.database.File;
import jacz.database.Movie;
import jacz.database.SubtitleFile;
import jacz.database.VideoFile;
import jacz.database.util.LocalizedLanguage;
import jacz.database.util.QualityCode;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by alberto on 6/24/16.
 */
public class VideoFilesEditor {

    public static class FileData {

        public final String hash;

        public final Long length;

        public final String name;

        public final List<String> additionalSources;

        public FileData(String hash, Long length, String name, List<String> additionalSources) {
            this.hash = hash;
            this.length = length;
            this.name = name;
            this.additionalSources = additionalSources;
        }

        public FileData(jacz.database.File file) {
            this(file.getHash(), file.getLength(), file.getName(), file.getAdditionalSources());
        }
    }

    public static class VideoFileData extends FileData {

        public final Integer minutes;

        public final Integer resolution;

        public final QualityCode quality;

        public final List<SubtitleFileData> subtitleFiles;

        public final List<LocalizedLanguage> localizedLanguages;

        public VideoFileData(String hash, Long length, String name, List<String> additionalSources, Integer minutes, Integer resolution, QualityCode quality, List<SubtitleFileData> subtitleFiles, List<LocalizedLanguage> localizedLanguages) {
            super(hash, length, name, additionalSources);
            this.minutes = minutes;
            this.resolution = resolution;
            this.quality = quality;
            this.subtitleFiles = subtitleFiles;
            this.localizedLanguages = localizedLanguages;
        }

        public VideoFileData(VideoFile videoFile) {
            super(videoFile);
            this.minutes = videoFile.getMinutes();
            this.resolution = videoFile.getResolution();
            this.quality = videoFile.getQuality();
            this.subtitleFiles = videoFile.getSubtitleFiles().stream().map(SubtitleFileData::new).collect(Collectors.toList());
            this.localizedLanguages = videoFile.getLocalizedLanguages();
        }
    }

    public static class SubtitleFileData extends FileData {

        public final LocalizedLanguage localizedLanguage;

        public SubtitleFileData(String hash, Long length, String name, List<String> additionalSources, LocalizedLanguage localizedLanguage) {
            super(hash, length, name, additionalSources);
            this.localizedLanguage = localizedLanguage;
        }

        public SubtitleFileData(SubtitleFile subtitleFile) {
            super(subtitleFile);
            this.localizedLanguage = subtitleFile.getLocalizedLanguage();
        }
    }

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


    public static void populateVideoFilesPane(VBox filesListVBox, Button newMovieButton, Main main, Movie movie, List<VideoFile> videoFiles) {
        populateVideoFilesPaneData(filesListVBox, newMovieButton, main, movie, videoFiles.stream().map(VideoFileData::new).collect(Collectors.toList()));
        // todo scroll
        //ScrollPane scrollPane = new ScrollPane(vBox, ScrollPane.SCROLLBARS_AS_NEEDED);
        //scrollPane.scroll
        //pane.getChildren().add(filesListVBox);
    }

    private static void populateVideoFilesPaneData(VBox filesListVBox, Button newMovieButton, Main main, Movie movie, List<VideoFileData> videoFileDataList) {
        filesListVBox.getChildren().clear();
        for (VideoFileData videoFileData : videoFileDataList) {
            filesListVBox.getChildren().add(buildVideoFileVBox(videoFileData));
        }
        setupNewFileButton(filesListVBox, newMovieButton, main, movie, videoFileDataList);
        // todo scroll
        //ScrollPane scrollPane = new ScrollPane(vBox, ScrollPane.SCROLLBARS_AS_NEEDED);
        //scrollPane.scroll
        //pane.getChildren().add(filesListVBox);
    }

    private static VBox buildVideoFileVBox(VideoFileData videoFileData) {
        // for each video file we paint an VBox with two nodes. The first
        // one is an HBox with the basic info of the video file (name, hash, length...). The second one is another
        // VBox composed of HBoxes. Each HBox represents one subtitle file.
        // todo add a list view above subtitles for the additional sources
        Label nameLabel = new Label(formatName(videoFileData.name));
        Label hashLabel = new Label(formatHash(videoFileData.hash));
        Label sizeLabel = new Label(formatSize(videoFileData.length));
        VBox nameHash = new VBox(nameLabel, hashLabel);
        HBox basicInfo = new HBox(nameHash, sizeLabel);
        basicInfo.setAlignment(Pos.CENTER_LEFT);
        basicInfo.setSpacing(3d);
        return new VBox(basicInfo);
    }

    private static void setupNewFileButton(VBox filesListVBox, Button newMovieButton, Main main, Movie movie, List<VideoFileData> videoFileDataList) {
        newMovieButton.setOnAction(event -> {
            // open a file selector. When a file is selected, load it into the file hash database and use its
            // metadata to populate a new VideoFileModel object that is added to the existing ones
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a video file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Video Files", "*.avi", "*.mkv", "*.mp4"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            java.io.File selectedMovieFile = fileChooser.showOpenDialog(main.getPrimaryStage());
            if (selectedMovieFile != null) {
                VideoFileData videoFileData = addNewVideoFile(selectedMovieFile.toString(), movie);
                videoFileDataList.add(videoFileData);
                populateVideoFilesPaneData(filesListVBox, newMovieButton, main, movie, videoFileDataList);
            }
        });
    }

    private static VideoFileData addNewVideoFile(String newMovieFile, Movie movie) {
        try {
            Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalMovieFile(newMovieFile, movie, true);
            Long length = new java.io.File(pathAndHash.element1).length();
            return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
            // todo update automatic metadata (did not find a suitable api for this, skipping...)
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


//    public static ObservableList<MediaDatabaseProperties.VideoFileModel> populateVideoFilesListView(ListView<MediaDatabaseProperties.VideoFileModel> listView, Button button, Main main, Movie movie, List<MediaDatabaseProperties.VideoFileModel> videoFiles) {
//        listView.getItems().clear();
//        ObservableList<MediaDatabaseProperties.VideoFileModel> observableVideoFiles = FXCollections.observableList(videoFiles);
//        listView.getItems().addAll(observableVideoFiles);
//        listView.getItems().get(0).ge
//        listView.setCellFactory(videoFileModel -> new VideoFileModelCellFactory());
//        setupNewFileButton(button, main, movie, observableVideoFiles);
//        return observableVideoFiles;
//    }

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

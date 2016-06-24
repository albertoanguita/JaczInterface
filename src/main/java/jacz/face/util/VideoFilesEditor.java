package jacz.face.util;

import jacz.database.File;
import jacz.database.SubtitleFile;
import jacz.database.VideoFile;
import jacz.database.util.LocalizedLanguage;
import jacz.database.util.QualityCode;
import jacz.face.state.MediaDatabaseProperties;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by alberto on 6/24/16.
 */
public class VideoFilesEditor {





    public static void populateVideoFilesListView(ListView<VBox> listView, List<MediaDatabaseProperties.VideoFileModel> videoFiles) {
        listView.getItems().clear();
        listView.getItems().addAll(buildVideoFilesList(videoFiles));
    }

    private static ObservableList<VBox> buildVideoFilesList(List<MediaDatabaseProperties.VideoFileModel> videoFiles) {
        return FXCollections.observableList(videoFiles.stream()
                .map(VideoFilesEditor::buildVideoFileVBoxFromModel)
                .collect(Collectors.toList()));
    }

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

package jacz.face.util;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.Movie;
import jacz.database.SubtitleFile;
import jacz.database.VideoFile;
import jacz.database.util.LocalizedLanguage;
import jacz.database.util.QualityCode;
import jacz.face.controllers.ClientAccessor;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.peerengineclient.SessionManager;
import jacz.peerengineservice.PeerId;
import jacz.util.lists.tuple.Duple;
import jacz.util.lists.tuple.Triple;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.MaskerPane;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileData)) return false;

            FileData fileData = (FileData) o;

            return hash.equals(fileData.hash);
        }

        @Override
        public int hashCode() {
            return hash.hashCode();
        }

        @Override
        public String toString() {
            return "FileData{" +
                    "hash='" + hash + '\'' +
                    ", length=" + length +
                    ", name='" + name + '\'' +
                    ", additionalSources=" + additionalSources +
                    '}';
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

        @Override
        public String toString() {
            return "VideoFileData{" +
                    super.toString() +
                    "minutes=" + minutes +
                    ", resolution=" + resolution +
                    ", quality=" + quality +
                    ", subtitleFiles=" + subtitleFiles +
                    ", localizedLanguages=" + localizedLanguages +
                    '}';
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

    public static class UpdateResult<T> {

        public final boolean change;

        public final List<T> files;

        public UpdateResult(boolean change, List<T> files) {
            this.change = change;
            this.files = files;
        }

        public static <T> UpdateResult<T> emptyResult() {
            return new UpdateResult<T>(false, new ArrayList<>());
        }
    }

    private static class AddLocalMovieFileService<T extends FileData> extends Service<T> {

        private final StringProperty path;

        private final ObjectProperty<Movie> movie;

        private final ObjectProperty<Function<Duple<Duple<String, String>, Long>, T>> resultCreator;

        public AddLocalMovieFileService(String path, Movie movie, Function<Duple<Duple<String, String>, Long>, T> resultCreator) {
            this.path = new ReadOnlyStringWrapper(path);
            this.movie = new ReadOnlyObjectWrapper<>(movie);
            this.resultCreator = new ReadOnlyObjectWrapper<>(resultCreator);
        }

        @Override
        protected Task<T> createTask() {
            return new Task<T>() {
                @Override
                protected T call()
                        throws IOException {
                    try {
                        Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalMovieFile(path.get(), movie.getValue(), true);
                        Long length = new java.io.File(pathAndHash.element1).length();
                        System.out.println("new file length: " + length);
                        return resultCreator.get().apply(new Duple<>(pathAndHash, length));

                        //return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
                        // todo update automatic metadata (did not find a suitable api for this, skipping...)
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }
    }

//    private static class AddLocalMovieVideoFileService extends AddLocalMovieFileService<VideoFileData> {
//
//        public AddLocalMovieVideoFileService(String path, Movie movie) {
//            super(path, movie);
//        }
//
//        @Override
//        protected VideoFileData createResult(Duple<String, String> pathAndHash, Long length) {
//            return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
//        }
//    }




    public static final String QUALITY_NULL_VALUE = " ";



    public static void populateVideoFilesPane(VBox filesListVBox, Button newMovieButton, Pane rootPane, Main main, Movie movie, List<VideoFile> videoFiles) {
        populateVideoFilesPaneData(filesListVBox, newMovieButton, rootPane, main, movie, videoFiles.stream().map(VideoFileData::new).collect(Collectors.toList()));
        // todo scroll
        //ScrollPane scrollPane = new ScrollPane(vBox, ScrollPane.SCROLLBARS_AS_NEEDED);
        //scrollPane.scroll
        //pane.getChildren().add(filesListVBox);
    }

    private static void populateVideoFilesPaneData(VBox filesListVBox, Button newMovieButton, Pane rootPane, Main main, Movie movie, List<VideoFileData> videoFileDataList) {
        filesListVBox.getChildren().clear();
        for (int i = 0; i < videoFileDataList.size(); i++) {
            VideoFileData videoFileData = videoFileDataList.get(i);
            filesListVBox.getChildren().add(buildVideoFileVBox(filesListVBox, newMovieButton, rootPane, videoFileData, i, main, movie));
        }
        setupNewFileButton(filesListVBox, newMovieButton, rootPane, main, movie);

        // todo scroll
        //ScrollPane scrollPane = new ScrollPane(vBox, ScrollPane.SCROLLBARS_AS_NEEDED);
        //scrollPane.scroll
        //pane.getChildren().add(filesListVBox);
    }

    private static VBox buildVideoFileVBox(VBox filesListVBox, Button newMovieButton, Pane rootPane, VideoFileData videoFileData, int index, Main main, Movie movie) {
        // for each video file we paint an VBox with two nodes. The first
        // one is an HBox with the basic info of the video file (name, hash, length...). The second one is another
        // VBox composed of HBoxes. Each HBox represents one subtitle file.
        // todo add a list view above subtitles for the additional sources
        Label nameLabel = new Label(formatName(videoFileData.name));
        nameLabel.setMaxWidth(100d);
        Label hashLabel = new Label(formatHash(videoFileData.hash));
        VBox nameHash = new VBox(nameLabel, hashLabel);
        Label sizeLabel = new Label(formatSize(videoFileData.length));
        sizeLabel.setMaxWidth(100d);
        Pane minutesPane = buildTextFieldPane(formatDuration(videoFileData.minutes), 40d, 40d, 40d, "min");
        Pane resolutionPane = buildTextFieldPane(formatResolution(videoFileData.resolution), 40d, 40d, 40d, "px");
        Pane qualityPane = buildQualityPane(videoFileData.quality, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, "quality");
        VBox languagesVBox = new VBox();
        Controls.localizedLanguageListPane(languagesVBox, videoFileData.localizedLanguages);
        Button removeButton = new Button("x");
        HBox basicInfo = new HBox(nameHash, sizeLabel, minutesPane, resolutionPane, qualityPane, languagesVBox, removeButton);
        basicInfo.setAlignment(Pos.CENTER_LEFT);
        basicInfo.setSpacing(15d);
        VBox subtitlesVBox = new VBox();
        Button newSubtitleButton = new Button("Add subtitle");
        populateSubtitleFilesPaneData(subtitlesVBox, newSubtitleButton, main, movie, videoFileData.subtitleFiles);
        VBox subtitlesContainer = new VBox(subtitlesVBox, newSubtitleButton);
        AnchorPane subtitlesPane = new AnchorPane(subtitlesContainer);
        AnchorPane.setLeftAnchor(subtitlesContainer, 30d);
        VBox videoFileVBox = new VBox(basicInfo, subtitlesPane);
        removeButton.setOnAction(event -> {
            List<VideoFileData> newVideoFileDataList = parseVideoFileDataList(filesListVBox);
            newVideoFileDataList.remove(index);
            populateVideoFilesPaneData(filesListVBox, newMovieButton, rootPane, main, movie, newVideoFileDataList);
        });
        return videoFileVBox;
    }

//    private static void populateSubtitleFilesPane(VBox subtitlesVBox, Button newSubtitleButton, Main main, Movie movie, List<SubtitleFile> subtitleFiles) {
//        populateSubtitleFilesPaneData(subtitlesVBox, newSubtitleButton, main, movie, subtitleFiles.stream().map(SubtitleFileData::new).collect(Collectors.toList()));
//    }

    private static void populateSubtitleFilesPaneData(VBox subtitlesVBox, Button newSubtitleButton, Main main, Movie movie, List<SubtitleFileData> subtitleFileDataList) {
        subtitlesVBox.getChildren().clear();
        for (SubtitleFileData subtitleFileData : subtitleFileDataList) {
            subtitlesVBox.getChildren().add(buildSubtitleFileHBox(subtitlesVBox, newSubtitleButton, subtitleFileData, main, movie));
        }
        setupNewSubtitleButton(subtitlesVBox, newSubtitleButton, main, movie);
        // todo scroll
        //ScrollPane scrollPane = new ScrollPane(vBox, ScrollPane.SCROLLBARS_AS_NEEDED);
        //scrollPane.scroll
        //pane.getChildren().add(filesListVBox);
        //pane.getChildren().add(filesListVBox);
    }


    private static HBox buildSubtitleFileHBox(VBox subtitlesVBox, Button newSubtitleButton, SubtitleFileData subtitleFileData, Main main, Movie movie) {
        // for each video file we paint an VBox with two nodes. The first
        // one is an HBox with the basic info of the video file (name, hash, length...). The second one is another
        // VBox composed of HBoxes. Each HBox represents one subtitle file.
        // todo add a list view above subtitles for the additional sources
        Label nameLabel = new Label(formatName(subtitleFileData.name));
        nameLabel.setMaxWidth(100d);
        Label hashLabel = new Label(formatHash(subtitleFileData.hash));
        VBox nameHash = new VBox(nameLabel, hashLabel);
        Label sizeLabel = new Label(formatSize(subtitleFileData.length));
        sizeLabel.setMaxWidth(100d);
        HBox localizedLanguageHBox = new HBox();
        Controls.localizedLanguageEditor(localizedLanguageHBox, subtitleFileData.localizedLanguage);
        localizedLanguageHBox.setSpacing(15d);
        Button removeButton = new Button("x");
        HBox basicInfo = new HBox(nameHash, sizeLabel, localizedLanguageHBox, removeButton);
        basicInfo.setAlignment(Pos.CENTER_LEFT);
        basicInfo.setSpacing(15d);
        removeButton.setOnAction(event -> {
            List<SubtitleFileData> newSubtitleFileDataList = parseSubtitleFileDataList(subtitlesVBox);
            newSubtitleFileDataList.remove(subtitleFileData);
            populateSubtitleFilesPaneData(subtitlesVBox, newSubtitleButton, main, movie, newSubtitleFileDataList);
        });
        return basicInfo;
    }

    private static Pane buildTextFieldPane(String text, Double minWidth, Double prefWidth, Double maxWidth, String postLabel) {
        HBox pane = new HBox();
        pane.setAlignment(Pos.CENTER_LEFT);
        TextField textField = new TextField(text);
        textField.setAlignment(Pos.CENTER_RIGHT);
        if (minWidth != null) {
            textField.setMinWidth(minWidth);
        }
        if (prefWidth != null) {
            textField.setPrefWidth(prefWidth);
        }
        if (maxWidth != null) {
            textField.setMaxWidth(maxWidth);
        }
        pane.getChildren().add(textField);
        if (postLabel != null) {
            Label label = new Label(postLabel);
            label.setMinWidth(Control.USE_COMPUTED_SIZE);
            label.setPrefWidth(Control.USE_COMPUTED_SIZE);
            label.setMaxWidth(Control.USE_COMPUTED_SIZE);
            pane.getChildren().add(label);
        }
        return pane;
    }

    private static <T> T parseTextFieldPane(Pane pane, Function<String, T> parser) {
        TextField textField = (TextField) pane.getChildren().get(0);
        return parser.apply(textField.getText());
    }

    private static Pane buildQualityPane(QualityCode quality, Double minWidth, Double prefWidth, Double maxWidth, String postLabel) {
        HBox pane = new HBox();
        pane.setAlignment(Pos.CENTER_LEFT);
        ChoiceBox<String> qualityChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Util.getQualitiesNames(QUALITY_NULL_VALUE)));
        qualityChoiceBox.setValue(quality == null ? QUALITY_NULL_VALUE : quality.name());
        if (minWidth != null) {
            qualityChoiceBox.setMinWidth(minWidth);
        }
        if (prefWidth != null) {
            qualityChoiceBox.setPrefWidth(prefWidth);
        }
        if (maxWidth != null) {
            qualityChoiceBox.setMaxWidth(maxWidth);
        }
        pane.getChildren().add(qualityChoiceBox);
        if (postLabel != null) {
            Label label = new Label(postLabel);
            label.setMinWidth(Control.USE_COMPUTED_SIZE);
            label.setPrefWidth(Control.USE_COMPUTED_SIZE);
            label.setMaxWidth(Control.USE_COMPUTED_SIZE);
            pane.getChildren().add(label);
        }
        return pane;
    }

    private static QualityCode parseQualityPane(Pane pane) {
        ChoiceBox<String> qualityChoiceBox = (ChoiceBox<String>) pane.getChildren().get(0);
        return Util.getQualityFromName(qualityChoiceBox.getValue());
    }

    private static void setupNewFileButton(VBox filesListVBox, Button newMovieButton, Pane rootPane, Main main, Movie movie) {
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
                List<VideoFileData> newVideoFileDataList = parseVideoFileDataList(filesListVBox);

                MaskerPane maskerPane = new MaskerPane();
                maskerPane.setText("Processing file...");
                maskerPane.setPrefWidth(rootPane.getWidth());
                maskerPane.setPrefHeight(rootPane.getHeight());
                rootPane.getChildren().add(maskerPane);


                maskerPane.setVisible(true);
                AddLocalMovieFileService<VideoFileData> addLocalMovieFileService = new AddLocalMovieFileService<>(
                        selectedMovieFile.toString(),
                        movie,
                        o -> {
                            Duple<String, String> pathAndHash = o.element1;
                            Long length = o.element2;
                            return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
                        });
                addLocalMovieFileService.setOnSucceeded(t -> {
                    VideoFileData videoFileData = (VideoFileData) t.getSource().getValue();
                    newVideoFileDataList.add(videoFileData);
                    Platform.runLater(() -> populateVideoFilesPaneData(filesListVBox, newMovieButton, rootPane, main, movie, newVideoFileDataList));
                    maskerPane.setVisible(false);
                    rootPane.getChildren().remove(rootPane.getChildren().size() - 1);
                });
                addLocalMovieFileService.start();
            }
        });
    }

    private static void setupNewSubtitleButton(VBox subtitlesVBox, Button newSubtitleButton, Main main, Movie movie) {
        newSubtitleButton.setOnAction(event -> {
            // open a file selector. When a file is selected, load it into the file hash database and use its
            // metadata to populate a new VideoFileModel object that is added to the existing ones
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a subtitle file");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Subtitle Files", "*.srt", "*.sub"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            java.io.File selectedSubtitleFile = fileChooser.showOpenDialog(main.getPrimaryStage());
            if (selectedSubtitleFile != null) {
                // todo use service
                List<SubtitleFileData> newSubtitleFileDataList = parseSubtitleFileDataList(subtitlesVBox);
                SubtitleFileData subtitleFileData = addNewSubtitleFile(selectedSubtitleFile.toString(), movie);
                newSubtitleFileDataList.add(subtitleFileData);
                populateSubtitleFilesPaneData(subtitlesVBox, newSubtitleButton, main, movie, newSubtitleFileDataList);
            }
        });
    }

    private static VideoFileData addNewVideoFile(String newMovieFile, Movie movie) {
        try {
//            AddLocalMovieFileService addLocalMovieFileService = new AddLocalMovieFileService(newMovieFile, movie, true);
//            addLocalMovieFileService.setOnSucceeded(t -> {
//                Duple<String, String> pathAndHash = (Duple<String, String>) t.getSource().getValue();
//                masker.setVisible(false);
//                Long length = new java.io.File(pathAndHash.element1).length();
//                System.out.println("new file length: " + length);
//                return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
//            });
//            addLocalMovieFileService.
//            addLocalMovieFileService.start();


            Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalMovieFile(newMovieFile, movie, true);
            Long length = new java.io.File(pathAndHash.element1).length();
            System.out.println("new file length: " + length);

            return new VideoFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null, null, null, new ArrayList<>(), new ArrayList<>());
            // todo update automatic metadata (did not find a suitable api for this, skipping...)
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SubtitleFileData addNewSubtitleFile(String newSubtitleFile, Movie movie) {
        try {
            Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalMovieFile(newSubtitleFile, movie, true);
            Long length = new java.io.File(pathAndHash.element1).length();
            System.out.println("new file length: " + length);
            return new SubtitleFileData(pathAndHash.element2, length, Paths.get(pathAndHash.element1).getFileName().toString(), new ArrayList<>(), null);
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

//    private static VBox buildVideoFileVBoxFromModel(MediaDatabaseProperties.VideoFileModel videoFileModel) {
//        Label nameLabel = new Label(formatName(videoFileModel.getName()));
//        Label hashLabel = new Label(formatHash(videoFileModel.getHash()));
//        Label sizeLabel = new Label(formatSize(videoFileModel.getLength()));
//        VBox nameHash = new VBox(nameLabel, hashLabel);
//        HBox basicInfo = new HBox(nameHash, sizeLabel);
//        basicInfo.setAlignment(Pos.CENTER_LEFT);
//        basicInfo.setSpacing(3d);
//        return new VBox(basicInfo);
//    }

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

    private static String formatDuration(Integer minutes) {
        return minutes == null ? "" : minutes.toString();
    }

    private static String formatResolution(Integer resolution) {
        return resolution == null ? "" : resolution.toString();
    }

    private static String parseName(String text) {
        return text.equals("unnamed file") ? null : text;
    }

    private static String parseHash(String text) {
        String hash = text.substring("(MD5: ".length(), text.length() - 1);
        return !hash.isEmpty() ? hash : null;
    }

    private static Long parseSize(String text) {
        if (text.equals("?")) {
            return null;
        } else {
            String sizeText = text.substring(0, text.length() - " bytes".length());
            return Long.parseLong(sizeText);
        }
    }

    private static Integer parseMinutes(String text) {
        return Util.parseInteger(text);
    }

    private static Integer parseResolution(String text) {
        return Util.parseInteger(text);
    }


    public static UpdateResult<VideoFile> updateVideoFiles(VBox filesListVBox, List<VideoFile> oldVideoFiles, String dbPath) {
        return updateVideoFiles(oldVideoFiles, parseVideoFileDataList(filesListVBox), dbPath);
    }

    public static UpdateResult<VideoFile> updateVideoFiles(List<VideoFile> oldVideoFiles, List<VideoFileData> videoFileDataList, String dbPath) {
        System.out.println(videoFileDataList);
        if (videoFileDataList.isEmpty() && oldVideoFiles.isEmpty()) {
            // no items in any list -> no changes
            return UpdateResult.emptyResult();
        } else if (videoFileDataList.isEmpty()) {
            // the remaining video files must be deleted
            for (VideoFile videoFile : oldVideoFiles) {
                ClientAccessor.getInstance().getClient().removeLocalItem(videoFile);
            }
            // the list of video files has changed (the resulting list is an empty list)
            return new UpdateResult<>(true, new ArrayList<>());
        } else if (oldVideoFiles.isEmpty()) {
            // there are new files to add to the video file list
            List<VideoFile> newVideoFiles = videoFileDataList.stream().map(videoFileData -> createNewVideoFile(videoFileData, dbPath)).collect(Collectors.toList());
            return new UpdateResult<>(true, newVideoFiles);
        } else {
            // both lists have remaining elements
            // pick the first video file and search it among the video file data list elements
            // if it exists, update. If not, remove.
            // Also check if the order is the same
            VideoFile videoFile = oldVideoFiles.remove(0);
            int index = videoFileDataList.indexOf(new VideoFileData(videoFile));
//            int index = find(videoFile.getHash(), videoFileDataList);
            if (index < 0) {
                // there is no such video file -> remove
                // keep searching recursively. Change will be true
                ClientAccessor.getInstance().getClient().removeLocalItem(videoFile);
                return new UpdateResult<>(true, updateVideoFiles(oldVideoFiles, videoFileDataList, dbPath).files);
            } else {
                // there is such a file -> update the video file and keep searching
                // the change will be true if this element is not at the beginning
                VideoFileData videoFileData = videoFileDataList.remove(index);
                updateVideoFile(videoFile, videoFileData);
                UpdateResult<VideoFile> updatedVideoFiles = updateVideoFiles(oldVideoFiles, videoFileDataList, dbPath);
                // add the updated video file as first element again
                updatedVideoFiles.files.add(0, videoFile);
                return new UpdateResult<>(index != 0 || updatedVideoFiles.change, updatedVideoFiles.files);
            }
        }
    }

//    private static int find(String hash, List<VideoFileData> videoFileDataList) {
//        for (int i = 0; i < videoFileDataList.size(); i++) {
//            if (videoFileDataList.get(i).hash.equals(hash)) {
//                return i;
//            }
//        }
//        return -1;
//    }


    private static List<VideoFileData> parseVideoFileDataList(VBox filesListVBox) {
        return filesListVBox.getChildren().stream().map(videoFileVBox -> parseVideoFileNode((VBox) videoFileVBox)).collect(Collectors.toList());
    }

    private static VideoFileData parseVideoFileNode(VBox videoFileVBox) {
        // todo languages
        HBox basicInfoHBox = (HBox) videoFileVBox.getChildren().get(0);
        List<Node> nodeList = basicInfoHBox.getChildren();
        VBox nameHash = (VBox) nodeList.get(0);
        Label nameLabel = (Label) nameHash.getChildren().get(0);
        Label hashLabel = (Label) nameHash.getChildren().get(1);
        String name = parseName(nameLabel.getText());
        String hash = parseHash(hashLabel.getText());
        Label sizeLabel = (Label) nodeList.get(1);
        Long length = parseSize(sizeLabel.getText());
        Pane minutesPane = (Pane) nodeList.get(2);
        Integer minutes = parseTextFieldPane(minutesPane, VideoFilesEditor::parseMinutes);
        Pane resolutionPane = (Pane) nodeList.get(3);
        Integer resolution = parseTextFieldPane(resolutionPane, VideoFilesEditor::parseResolution);
        Pane qualityPane = (Pane) nodeList.get(4);
        QualityCode quality = parseQualityPane(qualityPane);
        AnchorPane subtitlesPane = (AnchorPane) videoFileVBox.getChildren().get(1);
        VBox subtitlesContainer = (VBox) subtitlesPane.getChildren().get(0);
        VBox subtitlesVBox = (VBox) subtitlesContainer.getChildren().get(0);
        List<SubtitleFileData> subtitleFileDataList = parseSubtitleFileDataList(subtitlesVBox);
        VBox languagesVBox = (VBox) nodeList.get(5);
        List<LocalizedLanguage> localizedLanguageList = Controls.parseLocalizedLanguageList(languagesVBox);
        return new VideoFileData(hash, length, name, new ArrayList<>(), minutes, resolution, quality, subtitleFileDataList, localizedLanguageList);
    }

    private static List<SubtitleFileData> parseSubtitleFileDataList(VBox subtitlesVBox) {
        return subtitlesVBox.getChildren().stream().map(subtitleFileHBox -> parseSubtitleFileNode((HBox) subtitleFileHBox)).collect(Collectors.toList());
    }

    private static SubtitleFileData parseSubtitleFileNode(HBox subtitleFileHBox) {
        //HBox basicInfoHBox = (HBox) videoFileVBox.getChildren().get(0);
        List<Node> nodeList = subtitleFileHBox.getChildren();
        VBox nameHash = (VBox) nodeList.get(0);
        Label nameLabel = (Label) nameHash.getChildren().get(0);
        Label hashLabel = (Label) nameHash.getChildren().get(1);
        String name = parseName(nameLabel.getText());
        String hash = parseHash(hashLabel.getText());
        Label sizeLabel = (Label) nodeList.get(1);
        Long length = parseSize(sizeLabel.getText());
        HBox localizedLanguageHBox = (HBox) nodeList.get(2);
        LocalizedLanguage localizedLanguage = Controls.parseLocalizedLanguage(localizedLanguageHBox);
        return new SubtitleFileData(hash, length, name, new ArrayList<>(), localizedLanguage);
    }

    private static VideoFile createNewVideoFile(VideoFileData videoFileData, String dbPath) {
        VideoFile videoFile = new VideoFile(dbPath, videoFileData.hash);
        updateVideoFile(videoFile, videoFileData);
        return videoFile;
    }


    private static void updateVideoFile(VideoFile videoFile, VideoFileData videoFileData) {
        // todo subtitles
        boolean changes = false;
        if (!jacz.util.objects.Util.equals(videoFile.getLength(), videoFileData.length)) {
            videoFile.setLengthPostponed(videoFileData.length);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(videoFile.getName(), videoFileData.name)) {
            videoFile.setNamePostponed(videoFileData.name);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(videoFile.getAdditionalSources(), videoFileData.additionalSources)) {
            videoFile.setAdditionalSourcesPostponed(videoFileData.additionalSources);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(videoFile.getMinutes(), videoFileData.minutes)) {
            videoFile.setMinutesPostponed(videoFileData.minutes);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(videoFile.getResolution(), videoFileData.resolution)) {
            videoFile.setResolutionPostponed(videoFileData.resolution);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(videoFile.getQuality(), videoFileData.quality)) {
            videoFile.setQualityPostponed(videoFileData.quality);
            changes = true;
        }

        UpdateResult<SubtitleFile> updatedSubtitleFiles = updateSubtitleFiles(videoFile.getSubtitleFiles(), videoFileData.subtitleFiles, ClientAccessor.getInstance().getClient().getDatabases().getLocalDB());
        if (updatedSubtitleFiles.change) {
            videoFile.setSubtitleFiles(updatedSubtitleFiles.files);
            changes = true;
        }


        if (!jacz.util.objects.Util.equals(videoFile.getLocalizedLanguages(), videoFileData.localizedLanguages)) {
            videoFile.setLocalizedLanguagesPostponed(videoFileData.localizedLanguages);
            changes = true;
        }
        if (changes) {
            videoFile.flushChanges();
            ClientAccessor.getInstance().getClient().localItemModified(videoFile);
        }
    }





//    private static UpdateResult<SubtitleFile> updateSubtitleFiles(VBox subtitlesVBox, List<SubtitleFile> oldSubtitleFiles, String dbPath) {
//        return updateSubtitleFiles(oldSubtitleFiles, parseSubtitleFileDataList(subtitlesVBox), dbPath);
//    }

    private static UpdateResult<SubtitleFile> updateSubtitleFiles(List<SubtitleFile> oldSubtitleFiles, List<SubtitleFileData> subtitleFileDataList, String dbPath) {
        System.out.println(subtitleFileDataList);
        if (subtitleFileDataList.isEmpty() && oldSubtitleFiles.isEmpty()) {
            // no items in any list -> no changes
            return UpdateResult.emptyResult();
        } else if (subtitleFileDataList.isEmpty()) {
            // the remaining video files must be deleted
            for (SubtitleFile subtitleFile : oldSubtitleFiles) {
                ClientAccessor.getInstance().getClient().removeLocalItem(subtitleFile);
            }
            // the list of video files has changed (the resulting list is an empty list)
            return new UpdateResult<>(true, new ArrayList<>());
        } else if (oldSubtitleFiles.isEmpty()) {
            // there are new files to add to the video file list
            List<SubtitleFile> newSubtitleFiles = subtitleFileDataList.stream().map(subtitleFileData -> createNewSubtitleFile(subtitleFileData, dbPath)).collect(Collectors.toList());
            return new UpdateResult<>(true, newSubtitleFiles);
        } else {
            // both lists have remaining elements
            // pick the first video file and search it among the video file data list elements
            // if it exists, update. If not, remove.
            // Also check if the order is the same
            SubtitleFile subtitleFile = oldSubtitleFiles.remove(0);
            int index = subtitleFileDataList.indexOf(new SubtitleFileData(subtitleFile));
//            int index = find(videoFile.getHash(), videoFileDataList);
            if (index < 0) {
                // there is no such video file -> remove
                // keep searching recursively. Change will be true
                ClientAccessor.getInstance().getClient().removeLocalItem(subtitleFile);
                return new UpdateResult<>(true, updateSubtitleFiles(oldSubtitleFiles, subtitleFileDataList, dbPath).files);
            } else {
                // there is such a file -> update the video file and keep searching
                // the change will be true if this element is not at the beginning
                SubtitleFileData subtitleFileData = subtitleFileDataList.remove(index);
                updateSubtitleFile(subtitleFile, subtitleFileData);
                UpdateResult<SubtitleFile> updatedSubtitleFiles = updateSubtitleFiles(oldSubtitleFiles, subtitleFileDataList, dbPath);
                // add the updated video file as first element again
                updatedSubtitleFiles.files.add(0, subtitleFile);
                return new UpdateResult<>(index != 0 || updatedSubtitleFiles.change, updatedSubtitleFiles.files);
            }
        }
    }

    private static SubtitleFile createNewSubtitleFile(SubtitleFileData subtitleFileData, String dbPath) {
        SubtitleFile subtitleFile = new SubtitleFile(dbPath, subtitleFileData.hash);
        updateSubtitleFile(subtitleFile, subtitleFileData);
        return subtitleFile;
    }

    private static boolean updateSubtitleFile(SubtitleFile subtitleFile, SubtitleFileData subtitleFileData) {
        boolean changes = false;
        if (!jacz.util.objects.Util.equals(subtitleFile.getLength(), subtitleFileData.length)) {
            subtitleFile.setLengthPostponed(subtitleFileData.length);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(subtitleFile.getName(), subtitleFileData.name)) {
            subtitleFile.setNamePostponed(subtitleFileData.name);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(subtitleFile.getAdditionalSources(), subtitleFileData.additionalSources)) {
            subtitleFile.setAdditionalSourcesPostponed(subtitleFileData.additionalSources);
            changes = true;
        }
        if (!jacz.util.objects.Util.equals(subtitleFile.getLocalizedLanguage(), subtitleFileData.localizedLanguage)) {
            subtitleFile.setLocalizedLanguagePostponed(subtitleFileData.localizedLanguage);
            changes = true;
        }
        if (changes) {
            subtitleFile.flushChanges();
            ClientAccessor.getInstance().getClient().localItemModified(subtitleFile);
        }
        return changes;
    }






//    public static List<VideoFile> updateVideoFiles(ListView<VBox> listView, List<VideoFile> oldVideoFiles, Consumer<List<VideoFile>> setVideoFiles, String dbPath) {
//        // the list of video files models will be checked against the given video files. The new video file models
//        // will produce a new video file. For those that already exist, we will check if there are changes, and
//        // update the found changes
//        // Finally, we will set the whole list of remaining and updated video files using the given consumer function
//        List<VideoFile> newVideoFiles = listView.getItems().stream()
//                .map(videoFileModelVBox -> buildVideoFile(videoFileModelVBox, oldVideoFiles, dbPath))
//                .collect(Collectors.toList());
//        setVideoFiles.accept(newVideoFiles);
//        return newVideoFiles;
//    }

//    private static VideoFile buildVideoFile(VBox videoFileModelVBox, List<VideoFile> oldVideoFiles, String dbPath) {
//        MediaDatabaseProperties.VideoFileModel videoFileModel = buildVideoFileModel(videoFileModelVBox);
//        VideoFile videoFile = findVideoFile(videoFileModel.hash, oldVideoFiles);
//        if (videoFile == null) {
//            // the video file model was not found in the given list of video files -> build a new Video File
//            videoFile = new VideoFile(dbPath, videoFileModel.hash);
//        }
//        //updateVideoFile(videoFile, videoFileModel);
//        return null;
//    }

//    private static MediaDatabaseProperties.VideoFileModel buildVideoFileModel(VBox videoFileModelVBox) {
//        return null;
//    }

//    private static VideoFile findVideoFile(String hash, List<VideoFile> videoFiles) {
//        for (VideoFile videoFile : videoFiles) {
//            if (hash.equals(videoFile.getHash())) {
//                return videoFile;
//            }
//        }
//        return null;
//    }

}

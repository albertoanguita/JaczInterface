package jacz.face.controllers;

import jacz.database.DatabaseMediator;
import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.FilesStateProperties;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.Util;
import jacz.face.util.VideoFilesEditor;
import jacz.peerengineclient.DownloadInfo;
import jacz.util.concurrency.task_executor.ThreadExecutor;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alberto on 19/06/2016.
 */
public class MovieController extends ProducedMediaItemController {

    private static final String UNKNOWN_FILE_NAME = "?";

    private static final String UNKNOWN_FILE_SIZE = "?";

    private static final String UNKNOWN = "?";


    @FXML
    private Label durationLabel;

    @FXML
    private TableView<MediaDatabaseProperties.VideoFileModel> filesTableView;

    @Override
    public void setMain(Main main) throws ItemNoLongerExistsException {
        super.setMain(main);
        if (mediaItem == null) {
            // the item no longer exists -> go back instead
            try {
                System.out.println("auto back!!!");
                main.navigateBackwards();
            } catch (IOException e) {
                // todo
                e.printStackTrace();
            }
            return;
        }


        durationLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.minutesProperty());
            }

            @Override
            protected String computeValue() {
                return formatNumber(mediaItem.getMinutes());
            }
        });

        filesTableView.setItems(mediaItem.videoFilesProperty());

        // state column
        TableColumn<MediaDatabaseProperties.VideoFileModel, VBox> stateColumn = new TableColumn<>("state");
        stateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MediaDatabaseProperties.VideoFileModel, VBox>, ObservableValue<VBox>>() {
            @Override
            public ObservableValue<VBox> call(TableColumn.CellDataFeatures<MediaDatabaseProperties.VideoFileModel, VBox> p) {
                ObjectProperty<VBox> op = new SimpleObjectProperty<>();
                String hash = p.getValue().getHash();
                FilesStateProperties.FileInfo fileInfo = PropertiesAccessor.getInstance().getFilesStateProperties().getFileInfo(hash);
                op.bind(new ObjectBinding<VBox>() {
                    {
                        super.bind(fileInfo.stateProperty(), fileInfo.downloadProgressProperty(), fileInfo.speedProperty());
                    }
                    @Override
                    protected VBox computeValue() {
                        Label stateLabel = new Label(fileInfo.getState().toString());
                        if (fileInfo.getState() == FilesStateProperties.FileState.DOWNLOADING) {
                            Label speedLabel = new Label(Util.formatSpeed(fileInfo.getSpeed()));
                            Label percentageLabel = new Label(Util.formatPerTenThousand(fileInfo.getDownloadProgress(), null));
                            return new VBox(stateLabel, speedLabel, percentageLabel);
                        } else {
                            return new VBox(stateLabel);
                        }
                    }
                });
                return op;
            }
        });
        // name column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName() != null ? p.getValue().getName() : UNKNOWN_FILE_NAME));
        // size column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> sizeColumn = new TableColumn<>("size");
        sizeColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().lengthProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getLength() == null ? UNKNOWN_FILE_SIZE : Long.toString(p.getValue().getLength() / 1024) + "KB";
                }
            });
            return sp;
        });
        // duration column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> durationColumn = new TableColumn<>("duration");
        durationColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().minutesProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getMinutes() == null ? UNKNOWN : Integer.toString(p.getValue().getMinutes()) + " m";
                }
            });
            return sp;
        });
        // resolution column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> resolutionColumn = new TableColumn<>("res");
        resolutionColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().resolutionProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getResolution() == null ? UNKNOWN : Integer.toString(p.getValue().getResolution()) + " px";
                }
            });
            return sp;
        });
        // quality column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> qualityColumn = new TableColumn<>("quality");
        qualityColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().qualityProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getQuality() == null ? UNKNOWN : p.getValue().getQuality().toString();
                }
            });
            return sp;
        });
        // localized languages column
        TableColumn<MediaDatabaseProperties.VideoFileModel, String> localizedLanguagesColumn = new TableColumn<>("languages");
        localizedLanguagesColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().getLanguages());
                }

                @Override
                protected String computeValue() {
                    return Util.formatLocalizedLanguageList(p.getValue().getLanguages(), Util.LocalizedLanguageFormat.SHORT);
                }
            });
            return sp;
        });

        //noinspection unchecked
        filesTableView.getColumns().setAll(stateColumn, nameColumn, sizeColumn, durationColumn, resolutionColumn, qualityColumn, localizedLanguagesColumn);

        filesTableView.setRowFactory(tableView -> {
            final TableRow<MediaDatabaseProperties.VideoFileModel> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    final ContextMenu contextMenu = new ContextMenu();
                    String hash = newValue.getHash();
                    FilesStateProperties.FileInfo fileInfo = PropertiesAccessor.getInstance().getFilesStateProperties().getFileInfo(hash);
                    contextMenu.getItems().addAll(getContextMenuItems(newValue, fileInfo, mediaItem.getId()));
                    row.setContextMenu(contextMenu);
                    fileInfo.stateProperty().addListener((observable1, oldValue1, newValue2) -> {
                        // we are not in the javafx thread
                        Platform.runLater(() -> {
                            row.getContextMenu().getItems().clear();
                            row.getContextMenu().getItems().addAll(getContextMenuItems(newValue, fileInfo, mediaItem.getId()));
                        });
                    });
                }
            });
            return row;
        });



//        filesTableView.setRowFactory(tableView -> {
//            final TableRow<TransferStatsProperties.DownloadPropertyInfo> row = new TableRow<>();
//            final ContextMenu contextMenu = new ContextMenu();
//            final MenuItem resumeMenuItem = new MenuItem("Resume");
//            resumeMenuItem.setOnAction(event -> resume(row.getItem()));
//            final MenuItem pauseMenuItem = new MenuItem("Pause");
//            pauseMenuItem.setOnAction(event -> pause(row.getItem()));
//            final MenuItem stopMenuItem = new MenuItem("Stop");
//            stopMenuItem.setOnAction(event -> stop(row.getItem()));
//            final MenuItem cancelMenuItem = new MenuItem("Cancel");
//            cancelMenuItem.setOnAction(event -> cancel(row.getItem()));
//
//            contextMenu.getItems().addAll(resumeMenuItem, pauseMenuItem, stopMenuItem, cancelMenuItem);
//            row.setContextMenu(contextMenu);
//            return row;
//        });

    }


    private static Collection<MenuItem> getContextMenuItems(
            MediaDatabaseProperties.VideoFileModel videoFileModel,
            FilesStateProperties.FileInfo fileInfo,
            int containerId) {
        final MenuItem openItem = new MenuItem("Open");
        final MenuItem deleteItem = new MenuItem("Delete");
        final MenuItem downloadItem = new MenuItem("Download");
        final MenuItem stopItem = new MenuItem("Stop");
        final MenuItem resumeItem = new MenuItem("Resume");
        final MenuItem cancelItem = new MenuItem("Cancel");
        List<MenuItem> menuItems = new ArrayList<>();
        openItem.setOnAction(actionEvent -> {
            ThreadExecutor.submit(() -> {
                try {
                    java.awt.Desktop.getDesktop().open(new File(fileInfo.getPath()).getParentFile());
                } catch (IOException e) {
                    // todo
                    e.printStackTrace();
                }
            });
        });
        deleteItem.setOnAction(actionEvent -> {
            ClientAccessor.getInstance().getClient().removeLocalFile(fileInfo.getHash(), true);
        });
        downloadItem.setOnAction(actionEvent -> {
            try {
                ClientAccessor.getInstance().getClient().downloadMediaFile(DownloadInfo.Type.VIDEO_FILE, DatabaseMediator.ItemType.MOVIE, containerId, null, videoFileModel.getItemId());
            } catch (Exception e) {
                // todo report to user
                e.printStackTrace();
            }
        });
        stopItem.setOnAction(actionEvent -> {
            fileInfo.getDownloadManager().stop();
        });
        resumeItem.setOnAction(actionEvent -> {
            fileInfo.getDownloadManager().resume();
        });
        cancelItem.setOnAction(actionEvent -> {
            fileInfo.getDownloadManager().cancel();
        });
        switch (fileInfo.getState()) {

            case LOCAL:
                menuItems.add(openItem);
                menuItems.add(deleteItem);
                break;
            case REMOTE:
                menuItems.add(downloadItem);
                break;
            case DOWNLOADING:
                menuItems.add(stopItem);
                menuItems.add(cancelItem);
                break;
            case PAUSED:
                // not used for now
                break;
            case STOPPED:
                menuItems.add(resumeItem);
                menuItems.add(cancelItem);
                break;
        }
        return menuItems;
    }


    public void editMovie() {
        Movie localMovie;
        if (mediaItem.getLocalId() == null) {
            // there is no local movie associated to this integrated move -> copy the integrated content to a new
            // local movie that is associated with this integrated move
            Movie integratedMovie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), mediaItem.getId());
            localMovie = (Movie) ClientAccessor.getInstance().getClient().copyIntegratedItemToLocalItem(integratedMovie);
            main.getNavigationHistory().getCurrentElement().updateLocalId(localMovie.getId());
        } else {
            // retrieve the existing local movie
            localMovie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getLocalId());
        }
        Optional<EditMovieController.MovieData> result = main.editMovie(NavigationHistory.DialogIntention.EDIT, localMovie);
        result.ifPresent(editMovie -> {
            System.out.println(editMovie.toString());

            //Movie movie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getLocalId());
            try {
                EditMovieController.changeMovie(localMovie, editMovie);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void editMovieFiles() {
        // todo DRY
        Movie localMovie;
        if (mediaItem.getLocalId() == null) {
            // there is no local movie associated to this integrated move -> copy the integrated content to a new
            // local movie that is associated with this integrated move
            Movie integratedMovie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(), mediaItem.getId());
            localMovie = (Movie) ClientAccessor.getInstance().getClient().copyIntegratedItemToLocalItem(integratedMovie);
            main.getNavigationHistory().getCurrentElement().updateLocalId(localMovie.getId());
        } else {
            // retrieve the existing local movie
            localMovie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getLocalId());
        }
        Optional<VideoFilesEditor.UpdateResult<VideoFile>> result = main.editMovieFiles(NavigationHistory.DialogIntention.EDIT, localMovie);
        result.ifPresent(editMovie -> {
            System.out.println(editMovie.toString());

            //Movie movie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getLocalId());
            try {
                EditFilesController.changeMovie(localMovie, editMovie);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void clickAnchor() {
        System.out.println("click anchor");
    }

    public void clickFiles() {
        System.out.println("click files");
    }
}

package jacz.face.controllers;

import jacz.database.Movie;
import jacz.database.VideoFile;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.FilesStateProperties;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.Util;
import jacz.face.util.VideoFilesEditor;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
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
    public void setMain(Main main) {
        super.setMain(main);

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
                        return new VBox(stateLabel);
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

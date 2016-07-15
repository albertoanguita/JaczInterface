package jacz.face.controllers;

import jacz.face.state.PropertiesAccessor;
import jacz.face.state.TransferStatsProperties;
import jacz.face.util.Util;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/7/16.
 */
public class TransfersController extends GenericController {

    private static final String UNKNOWN_ITEM_TITLE = "?";

    private static final String UNKNOWN_FILE_NAME = "?";

    private static final String UNKNOWN_FILE_SIZE = "?";

    private static final String UNKNOWN_PERCENTAGE = "-";

    private static final String UNKNOWN_ETA = "?";

    @FXML
    private TableView<TransferStatsProperties.DownloadPropertyInfo> downloadsTableView;

    @SuppressWarnings("FieldCanBeLocal")
    private ListChangeListener<TransferStatsProperties.DownloadPropertyInfo> downloadsTableViewChangeListener = null;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindings


        downloadsTableView.setItems(PropertiesAccessor.getInstance().getTransferStatsProperties().getObservedDownloads());

        downloadsTableViewChangeListener = new WeakListChangeListener<>(c -> downloadsTableView.refresh());
        PropertiesAccessor.getInstance().getTransferStatsProperties().getObservedDownloads().addListener(downloadsTableViewChangeListener);

        // downloads table
        // title column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> titleColumn = new TableColumn<>("item");
        titleColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getContainerTitle() != null ? p.getValue().getContainerTitle() : UNKNOWN_ITEM_TITLE));
        // file name column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> fileNameColumn = new TableColumn<>("file");
        fileNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFileName() != null ? p.getValue().getFileName() : UNKNOWN_FILE_NAME));
        // file size column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> fileSizeColumn = new TableColumn<>("total size");
        fileSizeColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().fileSizeProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getFileSize() == null ? UNKNOWN_FILE_SIZE : Long.toString(p.getValue().getFileSize() / 1024) + "KB";
                }
            });
            return sp;
        });
        // downloaded size column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> downloadedSizeColumn = new TableColumn<>("downloaded size");
        downloadedSizeColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().downloadStateProperty());
                }

                @Override
                protected String computeValue() {
                    return Long.toString(p.getValue().getTransferredSize() / 1024) + "KB";
                }
            });
            return sp;
        });
        // percentage column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> percentageColumn = new TableColumn<>("%");
        percentageColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().perTenThousandDownloadedProperty());
                }

                @Override
                protected String computeValue() {
                    Integer perTenThousand = p.getValue().getPerTenThousandDownloaded();
                    return Util.formatPerTenThousand(perTenThousand, UNKNOWN_PERCENTAGE);
                }
            });
            return sp;
        });
        // speed column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> speedColumn = new TableColumn<>("speed");
        speedColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().speedProperty());
                }

                @Override
                protected String computeValue() {
                    return Util.formatSpeed(p.getValue().getSpeed());
                }
            });
            return sp;
        });
        // eta column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> etaColumn = new TableColumn<>("eta");
        etaColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().fileSizeProperty(), p.getValue().speedProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getFileSize() != null ? Double.toString(p.getValue().getFileSize().doubleValue() / p.getValue().getSpeed() / 60) + "m" : UNKNOWN_ETA;
                }
            });
            return sp;
        });
        // providers column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> providersColumn = new TableColumn<>("providers");
        providersColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().fileSizeProperty(), p.getValue().providersCountProperty());
                }

                @Override
                protected String computeValue() {
                    return Integer.toString(p.getValue().getProvidersCount());
                }
            });
            return sp;
        });



        // action column
//        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> actionColumn = new TableColumn<>("eta");
//        fileSizeColumn.setCellValueFactory(p -> {
//            StringProperty sp = new SimpleStringProperty();
//            sp.bind(new StringBinding() {
//                {
//                    super.bind(p.getValue().fileSizeProperty(), p.getValue().speedProperty());
//                }
//
//                @Override
//                protected String computeValue() {
//                    return p.getValue().getFileSize() != null ? Double.toString(p.getValue().getFileSize().doubleValue() / p.getValue().getSpeed() / 60) + "m" : UNKNOWN_ETA;
//                }
//            });
//            return sp;
//        });

        //noinspection unchecked
        downloadsTableView.getColumns().setAll(titleColumn, fileNameColumn, fileSizeColumn, downloadedSizeColumn, percentageColumn, speedColumn, etaColumn, providersColumn);
        downloadsTableView.setRowFactory(tableView -> new TableRow<TransferStatsProperties.DownloadPropertyInfo>() {

            @Override
            protected void updateItem(TransferStatsProperties.DownloadPropertyInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    final ContextMenu contextMenu = new ContextMenu();
                    final MenuItem resumeMenuItem = new MenuItem("Resume");
                    resumeMenuItem.setOnAction(event -> resume(item));
                    final MenuItem pauseMenuItem = new MenuItem("Pause");
                    pauseMenuItem.setOnAction(event -> pause(item));
                    final MenuItem stopMenuItem = new MenuItem("Stop");
                    stopMenuItem.setOnAction(event -> stop(item));
                    final MenuItem cancelMenuItem = new MenuItem("Cancel");
                    cancelMenuItem.setOnAction(event -> cancel(item));

                    switch (item.downloadStateProperty().getValue()) {

                        case RUNNING:
                            contextMenu.getItems().addAll(pauseMenuItem, stopMenuItem, cancelMenuItem);
                            break;
                        case PAUSED:
                            contextMenu.getItems().addAll(resumeMenuItem, stopMenuItem, cancelMenuItem);
                            break;
                        case STOPPED:
                            contextMenu.getItems().addAll(resumeMenuItem, cancelMenuItem);
                            break;
                    }


                    //contextMenu.getItems().addAll(resumeMenuItem, pauseMenuItem, stopMenuItem, cancelMenuItem);
                    setContextMenu(contextMenu);
                }
            }
        });
    }

    private void resume(TransferStatsProperties.DownloadPropertyInfo downloadPropertyInfo) {
        downloadPropertyInfo.getDownloadManager().resume();
    }

    private void pause(TransferStatsProperties.DownloadPropertyInfo downloadPropertyInfo) {
        downloadPropertyInfo.getDownloadManager().pause();
    }

    private void stop(TransferStatsProperties.DownloadPropertyInfo downloadPropertyInfo) {
        downloadPropertyInfo.getDownloadManager().stop();
    }

    private void cancel(TransferStatsProperties.DownloadPropertyInfo downloadPropertyInfo) {
        downloadPropertyInfo.getDownloadManager().cancel();
    }
}

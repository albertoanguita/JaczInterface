package jacz.face.controllers;

import jacz.face.state.PeersStateProperties;
import jacz.face.state.TransferStatsProperties;
import jacz.peerengineservice.util.datatransfer.master.DownloadState;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/7/16.
 */
public class TransfersController extends MainController {

    private static final String UNKNOWN_ITEM_TITLE = "?";

    private static final String UNKNOWN_FILE_NAME = "?";

    private static final String UNKNOWN_FILE_SIZE = "?";

    private static final String UNKNOWN_ETA = "?";

    @FXML
    private TableView<TransferStatsProperties.DownloadPropertyInfo> downloadsTableView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindings

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
        fileSizeColumn.setCellValueFactory(p -> {
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
        fileSizeColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().perTenThousandDownloadedProperty());
                }

                @Override
                protected String computeValue() {
                    return Long.toString(p.getValue().getPerTenThousandDownloaded() / 100) + "." + Long.toString(p.getValue().getPerTenThousandDownloaded() % 100);
                }
            });
            return sp;
        });
        // speed column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> speedColumn = new TableColumn<>("speed");
        fileSizeColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().speedProperty());
                }

                @Override
                protected String computeValue() {
                    return Double.toString(p.getValue().getSpeed() / 1024) + "KB/s";
                }
            });
            return sp;
        });
        // eta column
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> etaColumn = new TableColumn<>("eta");
        fileSizeColumn.setCellValueFactory(p -> {
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
        downloadsTableView.getColumns().setAll(titleColumn, fileNameColumn, fileSizeColumn, downloadedSizeColumn, percentageColumn, speedColumn, etaColumn);

        downloadsTableView.setRowFactory(tableView -> {
            final TableRow<TransferStatsProperties.DownloadPropertyInfo> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem resumeMenuItem = new MenuItem("Resume");
            resumeMenuItem.setOnAction(event -> resume(row.getItem()));
            final MenuItem pauseMenuItem = new MenuItem("Pause");
            pauseMenuItem.setOnAction(event -> pause(row.getItem()));
            final MenuItem stopMenuItem = new MenuItem("Stop");
            stopMenuItem.setOnAction(event -> stop(row.getItem()));
            final MenuItem cancelMenuItem = new MenuItem("Cancel");
            cancelMenuItem.setOnAction(event -> cancel(row.getItem()));

            switch (row.getItem().downloadStateProperty().getValue()) {

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
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
//            row.contextMenuProperty().bind(
//                    Bindings.when(row.emptyProperty())
//                            .then((ContextMenu) null)
//                            .otherwise(contextMenu)
//            );
            row.setContextMenu(contextMenu);
            return row ;
        });
    }

    private void resume(TransferStatsProperties.DownloadPropertyInfo downloadPropertyInfo) {
        if (downloadPropertyInfo.downloadStateProperty().get() != DownloadState.STOPPED) {
            downloadPropertyInfo.getDownloadManager().resume();
        } else {
            // a new download is created, and the existing one is deleted
            // todo
        }
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

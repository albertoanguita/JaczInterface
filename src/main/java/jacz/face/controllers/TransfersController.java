package jacz.face.controllers;

import jacz.face.state.PeersStateProperties;
import jacz.face.state.TransferStatsProperties;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    private TableView<PeersStateProperties.PeerPropertyInfo> downloadsTableView;


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
        TableColumn<TransferStatsProperties.DownloadPropertyInfo, String> actionColumn = new TableColumn<>("eta");
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

    }
}

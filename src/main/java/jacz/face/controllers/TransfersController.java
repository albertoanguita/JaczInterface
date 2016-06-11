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

    }
}

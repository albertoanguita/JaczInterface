package jacz.face.controllers;

import jacz.face.state.PeersStateProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/7/16.
 */
public class PeersController extends MainController {

    @FXML
    private Label nickLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Button copyIdToClipboardButton;

    @FXML
    private Label creationDateLabel;

    @FXML
    private TableView<PeersStateProperties.PeerPropertyInfo2> peersTableView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindingds

        //nickLabel.setText(ClientAccessor.getInstance().getClient().getOwnNick());
        idLabel.setText(ClientAccessor.getInstance().getClient().getOwnPeerId().toString());


        nickLabel.textProperty().bind(PropertiesAccessor.getInstance().getGeneralStateProperties().ownNickPropertyProperty());

        copyIdToClipboardButton.setGraphic(new Glyph("FontAwesome", "CLIPBOARD"));

        try {
            creationDateLabel.setText(ClientAccessor.getInstance().getClient().profileCreationDate().toString());
        } catch (IOException e) {
            creationDateLabel.setText("-");
        }

        peersTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("relationProperty"));
        peersTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nickProperty"));
        //peersTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("shortIdProperty"));
        //peersTableView.getColumns().get(2).setCellValueFactory(param -> new SimpleObjectProperty<String>(param.getValue().getPeerIdProperty().toString()));
        TableColumn<PeersStateProperties.PeerPropertyInfo2, String> peerColumn = (TableColumn<PeersStateProperties.PeerPropertyInfo2, String>) peersTableView.getColumns().get(2);
        peerColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPeerIdProperty().toString()));

        TableColumn<PeersStateProperties.PeerPropertyInfo2, Button> countryColumn = (TableColumn<PeersStateProperties.PeerPropertyInfo2, Button>) peersTableView.getColumns().get(3);
        countryColumn.setCellFactory(param -> new TableCell<>());

        peersTableView.setItems(PropertiesAccessor.getInstance().getPeersStateProperties().observedPeers());
    }

    public void changeOwnNick() {
        TextInputDialog dlg = new TextInputDialog(ClientAccessor.getInstance().getClient().getOwnNick());
        //dlg.setTitle("Name Guess");
        dlg.setHeaderText("Change own nick");
        //String optionalMasthead = "Name Guess";
        dlg.getDialogPane().setContentText("Enter your new nick");
        Optional<String> result = dlg.showAndWait();
        result.ifPresent(name -> ClientAccessor.getInstance().getClient().setOwnNick(name));
    }

    public void copyIdToClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(ClientAccessor.getInstance().getClient().getOwnPeerId().toString());
        clipboard.setContent(content);
    }
}

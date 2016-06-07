package jacz.face.controllers;

import jacz.face.state.PropertiesAccessor;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindingds

        //nickLabel.setText(ClientAccessor.getInstance().getClient().getOwnNick());
        idLabel.setText(ClientAccessor.getInstance().getClient().getOwnPeerId().toString());


        nickLabel.textProperty().bind(PropertiesAccessor.getInstance().getGeneralStateProperties().ownNickPropertyProperty());

        copyIdToClipboardButton.setGraphic(new Glyph("FontAwesome", "CLIPBOARD"));
//        nickLabel.setOnMouseClicked(mouseEvent -> {
//            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
//                if(mouseEvent.getClickCount() == 2){
//                    changeOwnNick();
//                }
//            }
//        });
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

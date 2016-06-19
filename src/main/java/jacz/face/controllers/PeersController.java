package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.face.state.PeersStateProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.peerengineservice.PeerId;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by alberto on 6/7/16.
 */
public class PeersController extends GenericController {

    private static class CountryCell extends TableCell<PeersStateProperties.PeerPropertyInfo, CountryCode> {

        public CountryCell() {
        }

        @Override
        protected void updateItem(CountryCode item, boolean empty) {
            // calling super here is very important - don't skip this!
            super.updateItem(item, empty);

            // format the number as if it were a monetary value using the
            // formatting relevant to the current locale. This would format
            // 43.68 as "$43.68", and -23.67 as "-$23.67"
            setText(item == null ? "" : item.getName());

            // change the text fill based on whether it is positive (green)
            // or negative (red). If the cell is selected, the text will
            // always be white (so that it can be read against the blue
            // background), and if the value is zero, we'll make it black.
//            if (item != null) {
//                double value = item.doubleValue();
//                setTextFill(isSelected() ? Color.WHITE :
//                        value == 0 ? Color.BLACK :
//                                value < 0 ? Color.RED : Color.GREEN);
//            }
        }
    }


    /**
     * A table cell containing a button for adding a new person.
     */
    private class ChangeRelationshipCell extends TableCell<PeersStateProperties.PeerPropertyInfo, Boolean> {
        // a button for adding a new person.
        //private PeersStateProperties.PeerPropertyInfo peerPropertyInfo;
        final MenuItem setFavoriteItem = new MenuItem("Set as favorite");
        final MenuItem removeFavoriteItem = new MenuItem("Remove as favorite");
        final MenuItem blockItem = new MenuItem("Block");
        final MenuItem unblockItem = new MenuItem("Unblock");
        final MenuButton actionButton = new MenuButton("Action");
        final Button addButton = new Button("Add");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();
        // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
        final DoubleProperty buttonY = new SimpleDoubleProperty();

        /**
         * AddPersonCell constructor
         */
        ChangeRelationshipCell() {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(actionButton);
            actionButton.getItems().addAll(setFavoriteItem, removeFavoriteItem, blockItem, unblockItem);

            addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });
            setFavoriteItem.setOnAction(actionEvent -> {
                PeersStateProperties.PeerPropertyInfo p = (PeersStateProperties.PeerPropertyInfo) getTableRow().getItem();
                ClientAccessor.getInstance().getClient().addFavoritePeer(p.getPeerId());
            });
            removeFavoriteItem.setOnAction(actionEvent -> {
                PeersStateProperties.PeerPropertyInfo p = (PeersStateProperties.PeerPropertyInfo) getTableRow().getItem();
                ClientAccessor.getInstance().getClient().removeFavoritePeer(p.getPeerId());
            });
            blockItem.setOnAction(actionEvent -> {
                PeersStateProperties.PeerPropertyInfo p = (PeersStateProperties.PeerPropertyInfo) getTableRow().getItem();
                ClientAccessor.getInstance().getClient().addBlockedPeer(p.getPeerId());
            });
            unblockItem.setOnAction(actionEvent -> {
                PeersStateProperties.PeerPropertyInfo p = (PeersStateProperties.PeerPropertyInfo) getTableRow().getItem();
                ClientAccessor.getInstance().getClient().removeBlockedPeer(p.getPeerId());
            });
            actionButton.setOnMouseEntered(event -> {
                PeersStateProperties.PeerPropertyInfo p = (PeersStateProperties.PeerPropertyInfo) getTableRow().getItem();
                setFavoriteItem.disableProperty().setValue(p.getRelation().isFavorite());
                removeFavoriteItem.disableProperty().setValue(!p.getRelation().isFavorite());
                blockItem.disableProperty().setValue(p.getRelation().isBlocked());
                unblockItem.disableProperty().setValue(!p.getRelation().isBlocked());
            });
        }

        /**
         * places an add button in the row only if the row is not empty.
         */
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private static final String UNKNOWN_VALUE = "?";

    @FXML
    private Label nickLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Button copyIdToClipboardButton;

    @FXML
    private Label creationDateLabel;

    @FXML
    private TableView<PeersStateProperties.PeerPropertyInfo> peersTableView;

    @FXML
    private Button addFavoriteButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickLabel.textProperty().bind(PropertiesAccessor.getInstance().getGeneralStateProperties().ownNickPropertyProperty());
        idLabel.setText(ClientAccessor.getInstance().getClient().getOwnPeerId().toString());
        copyIdToClipboardButton.setGraphic(new Glyph("FontAwesome", "CLIPBOARD"));
        try {
            creationDateLabel.setText(ClientAccessor.getInstance().getClient().profileCreationDate().toString());
        } catch (IOException e) {
            creationDateLabel.setText("-");
        }

        // peers table
        // relation column
        TableColumn<PeersStateProperties.PeerPropertyInfo, String> relationColumn = new TableColumn<>("rel");
        relationColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().relationProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getRelation().toString();
                }
            });
            return sp;
        });
        // nick column
        TableColumn<PeersStateProperties.PeerPropertyInfo, String> nickColumn = new TableColumn<>("nick");
        nickColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().nickProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getNick() == null ? UNKNOWN_VALUE : p.getValue().getNick();
                }
            });
            return sp;
        });
        // id column
        TableColumn<PeersStateProperties.PeerPropertyInfo, String> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPeerId().toString().substring(38)));
        // country column
        TableColumn<PeersStateProperties.PeerPropertyInfo, String> countryColumn = new TableColumn<>("country");
        countryColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().countryProperty());
                }

                @Override
                protected String computeValue() {
                    return p.getValue().getCountry() == null ? UNKNOWN_VALUE : p.getValue().getCountry().getName();
                }
            });
            return sp;
        });
        // affinity column
        TableColumn<PeersStateProperties.PeerPropertyInfo, String> affinityColumn = new TableColumn<>("affinity");
        affinityColumn.setCellValueFactory(p -> {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(new StringBinding() {
                {
                    super.bind(p.getValue().affinityProperty());
                }

                @Override
                protected String computeValue() {
                    return Integer.toString(p.getValue().getAffinity());
                }
            });
            return sp;
        });

        // action example
        // todo this column is no longer used. We keep the code as example of how to add a button to a cell in a table view
        TableColumn<PeersStateProperties.PeerPropertyInfo, Boolean> actionCol = new TableColumn<>("Action");
        actionCol.setSortable(false);
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        actionCol.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        // create a cell value factory with an add button for each row in the table.
        actionCol.setCellFactory(personBooleanTableColumn -> new ChangeRelationshipCell());


        SortedList<PeersStateProperties.PeerPropertyInfo> sortedPeers =
                new SortedList<>(
                        PropertiesAccessor.getInstance().getPeersStateProperties().observedPeers(),
                        (o1, o2) -> {
                            int o1Priority = o1.getConnected() ? 100 : 0;
                            int o2Priority = o2.getConnected() ? 100 : 0;
                            if (o1.getRelation().isFavorite()) {
                                o1Priority += 50;
                            } else if (o1.getRelation().isRegular()) {
                                o1Priority += 25;
                            }
                            if (o2.getRelation().isFavorite()) {
                                o2Priority += 50;
                            } else if (o2.getRelation().isRegular()) {
                                o2Priority += 25;
                            }
                            return o2Priority - o1Priority;
                        });
        peersTableView.setItems(sortedPeers);
        //noinspection unchecked
        peersTableView.getColumns().setAll(relationColumn, nickColumn, idColumn, countryColumn, affinityColumn);


        peersTableView.setRowFactory(tableView -> {
            final TableRow<PeersStateProperties.PeerPropertyInfo> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    final ContextMenu contextMenu = new ContextMenu();
                    contextMenu.getItems().addAll(getContextMenuItems(newValue));
                    row.setContextMenu(contextMenu);
                    newValue.relationProperty().addListener((observable1, oldValue1, newValue2) -> {
                        row.getContextMenu().getItems().clear();
                        row.getContextMenu().getItems().addAll(getContextMenuItems(newValue));
                    });
                }
            });
            return row;
        });


        //peersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }



    private static Collection<MenuItem> getContextMenuItems(PeersStateProperties.PeerPropertyInfo peerPropertyInfo) {
        final MenuItem setFavoriteItem = new MenuItem("Set as favorite");
        final MenuItem removeFavoriteItem = new MenuItem("Remove as favorite");
        final MenuItem blockItem = new MenuItem("Block");
        final MenuItem unblockItem = new MenuItem("Unblock");
        List<MenuItem> menuItems = new ArrayList<>();
        setFavoriteItem.setOnAction(actionEvent -> {
            ClientAccessor.getInstance().getClient().addFavoritePeer(peerPropertyInfo.getPeerId());
        });
        removeFavoriteItem.setOnAction(actionEvent -> {
            ClientAccessor.getInstance().getClient().removeFavoritePeer(peerPropertyInfo.getPeerId());
        });
        blockItem.setOnAction(actionEvent -> {
            ClientAccessor.getInstance().getClient().addBlockedPeer(peerPropertyInfo.getPeerId());
        });
        unblockItem.setOnAction(actionEvent -> {
            ClientAccessor.getInstance().getClient().removeBlockedPeer(peerPropertyInfo.getPeerId());
        });
        if (!peerPropertyInfo.getRelation().isFavorite()) {
            menuItems.add(setFavoriteItem);
        }
        if (peerPropertyInfo.getRelation().isFavorite()) {
            menuItems.add(removeFavoriteItem);
        }
        if (!peerPropertyInfo.getRelation().isBlocked()) {
            menuItems.add(blockItem);
        }
        if (peerPropertyInfo.getRelation().isBlocked()) {
            menuItems.add(unblockItem);
        }
        return menuItems;
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

    public void addFavorite() {
        TextInputDialog dlg = new TextInputDialog();
        //dlg.setTitle("Name Guess");
        dlg.setHeaderText("Add a new favorite peer");
        //String optionalMasthead = "Name Guess";
        dlg.getDialogPane().setContentText("Enter the peer id");
        Optional<String> result = dlg.showAndWait();
        result.ifPresent(peerIdString -> {
            try {
                PeerId peerId = new PeerId(peerIdString);
                if (peerId.equals(ClientAccessor.getInstance().getClient().getOwnPeerId())) {
                    throw new IllegalArgumentException();
                }
                ClientAccessor.getInstance().getClient().addFavoritePeer(peerId);
            } catch (IllegalArgumentException e) {
                // wrong peer id value!
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                //alert.setHeaderText("Look, an Error Dialog");
                alert.setContentText("The given string is not a valid id value");
                alert.showAndWait();
            }
        });
    }
}

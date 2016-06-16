package jacz.face.controllers;

import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/7/16.
 */
public class MoviesController extends MainController {

    private static class MediaItemGridCell extends GridCell<MediaDatabaseProperties.MediaItem> {
        private AnchorPane rootPane;

        public MediaItemGridCell() {
            ImageView image = new ImageView();
            Label title = new Label();
            Label year = new Label();

            VBox vBoxPane = new VBox(image, title, year);

            rootPane = new AnchorPane(vBoxPane);
            AnchorPane.setLeftAnchor(vBoxPane, 10d);
            AnchorPane.setTopAnchor(vBoxPane, 10d);
            AnchorPane.setRightAnchor(vBoxPane, 10d);
            AnchorPane.setBottomAnchor(vBoxPane, 10d);
        }

        protected void updateItem(MediaDatabaseProperties.MediaItem item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                this.setGraphic(null);
            } else {
                VBox vBoxPane = (VBox) rootPane.getChildren().get(0);
                ImageView image = (ImageView) vBoxPane.getChildren().get(0);
                Label title = (Label) vBoxPane.getChildren().get(1);
                Label year = (Label) vBoxPane.getChildren().get(2);
                title.setText(item.getTitle());
                if (item.getYear() != null) {
                    year.setText("(" + item.getYear().toString() + ")");
                } else {
                    year.setText("?");
                }
                this.setGraphic(rootPane);
            }
        }
    }

    @FXML
    GridView<MediaDatabaseProperties.MediaItem> moviesGridView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // todo rest of bindings

        moviesGridView.setItems(PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMovies());

        moviesGridView.setCellFactory(new Callback<GridView<MediaDatabaseProperties.MediaItem>, GridCell<MediaDatabaseProperties.MediaItem>>() {
            @Override public GridCell<MediaDatabaseProperties.MediaItem> call(GridView<MediaDatabaseProperties.MediaItem> mediaItem) {
                return new MediaItemGridCell();
            }
        });
    }
}

package jacz.face.controllers;

import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ColorGridCell;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/7/16.
 */
public class MoviesController extends MainController {

    private static class MediaItemGridCell extends GridCell<MediaDatabaseProperties.MediaItem> {
        private Rectangle colorRect;

        public MediaItemGridCell(MediaDatabaseProperties.MediaItem mediaItem) {
            this.getStyleClass().add("color-grid-cell");
            this.colorRect = new Rectangle();
            this.colorRect.setStroke(Color.BLACK);
            this.colorRect.heightProperty().bind(this.heightProperty());
            this.colorRect.widthProperty().bind(this.widthProperty());
            this.setGraphic(this.colorRect);
        }

        protected void updateItem(MediaDatabaseProperties.MediaItem item, boolean empty) {
            super.updateItem(item, empty);
            if(empty) {
                this.setGraphic((Node)null);
            } else {
                //this.colorRect.setFill(item);
                this.setGraphic(this.colorRect);
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
                return new MediaItemGridCell(mediaItem.get);
            }
        });
    }
}

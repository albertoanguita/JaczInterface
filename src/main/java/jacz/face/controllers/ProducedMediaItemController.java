package jacz.face.controllers;

import jacz.database.util.GenreCode;
import jacz.face.main.Main;
import jacz.face.util.Util;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;

/**
 * Created by Alberto on 21/06/2016.
 */
public class ProducedMediaItemController extends MediaItemController {

    @FXML
    private Label productionCompaniesLabel;

    @FXML
    private Label genresLabel;

    @FXML
    private Pane imagePane;


    @Override
    public void setMain(Main main) throws ItemNoLongerExistsException {
        super.setMain(main);

        productionCompaniesLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.productionCompaniesProperty());
            }

            @Override
            protected String computeValue() {
                return formatStringList(mediaItem.getProductionCompanies());
            }
        });
        genresLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.genresProperty());
            }

            @Override
            protected String computeValue() {
                return formatGenres(mediaItem.getGenres());
            }
        });
        if (mediaItem.getImagePath() != null) {
            Util.displayImage(imagePane, mediaItem.getImagePath());
        }
        mediaItem.imagePathProperty().addListener((observable, oldValue, newValue) -> {
            Util.displayImage(imagePane, newValue);
        });
    }

}

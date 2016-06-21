package jacz.face.controllers;

import jacz.database.util.GenreCode;
import jacz.face.main.Main;
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
    public void setMain(Main main) {
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
                return formatList(mediaItem.getGenres(), Enum::name);
            }
        });
        if (mediaItem.getImagePath() != null) {
            displayImage(mediaItem.getImagePath());
        }
        mediaItem.imagePathProperty().addListener((observable, oldValue, newValue) -> {
            displayImage(newValue);
        });

//        ImageView imageView = new ImageView();
//        imagePane.getChildren().clear();
//        imagePane.getChildren().add(imageView);
//        imageView.fitWidthProperty().bind(imagePane.widthProperty());
//        imageView.fitHeightProperty().bind(imagePane.heightProperty());
//        if (mediaItem.getImagePath() != null) {
//            File imageFile = new File(mediaItem.getImagePath());
//            Image image = new Image(imageFile.toURI().toString());
//            imageView.setImage(image);
//            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
//            imageView.setPreserveRatio(true);
//            imageView.setSmooth(true);
//        }
    }

    private void displayImage(String path) {
        if (path != null) {
            imagePane.setPrefWidth(140);
            imagePane.setPrefHeight(180);
            ImageView imageView = new ImageView();
            Image image = new Image(new File(path).toURI().toString(), 140, 180, true, true);
            imageView.setImage(image);
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
            imagePane.getChildren().addAll(imageView);
        } else {
            imagePane.getChildren().clear();
        }
    }

}

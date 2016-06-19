package jacz.face.controllers;

import jacz.face.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/17/16.
 */
public class EditMovieController extends GenericController {

    public static class MovieData {

        public final String title;

        public final String originalTitle;

        public final Integer year;

        public MovieData(String title, String originalTitle, Integer year) {
            this.title = title;
            this.originalTitle = originalTitle;
            this.year = year;
        }

        @Override
        public String toString() {
            return "MovieData{" +
                    "title='" + title + '\'' +
                    ", originalTitle='" + originalTitle + '\'' +
                    ", year=" + year +
                    '}';
        }
    }

    @FXML
    Pane imagePane;

    @FXML
    TextField titleTextField;

    @FXML
    TextField originalTitleTextField;

    @FXML
    TextField yearTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //super.initialize(url, resourceBundle);
    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        // todo get user intention from main
    }

    public MovieData buildMovieData() {
        return new MovieData(titleTextField.getText(), originalTitleTextField.getText(), Integer.parseInt(yearTextField.getText()));
    }

    public void chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set movie poster");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (selectedFile != null) {
            System.out.println(selectedFile);
            imagePane.setPrefWidth(140);
            imagePane.setPrefHeight(180);
            ImageView imageView = new ImageView();
            Image image = new Image(selectedFile.toURI().toString(), 140, 180, true, true);
            imageView.setImage(image);
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
            imagePane.getChildren().addAll(imageView);
        }

    }
}

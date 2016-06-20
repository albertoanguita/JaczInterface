package jacz.face.controllers;

import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.Optional;

/**
 * Created by Alberto on 19/06/2016.
 */
public class MovieController extends MediaItemController {

    @FXML
    private Label durationLabel;

    @FXML
    private Label countriesLabel;

    @FXML
    private Label directorsLabel;

    @FXML
    private Label actorsLabel;

    @FXML
    private Label genresLabel;

    @FXML
    private TextArea synopsisTextArea;

    @FXML
    private Pane imagePane;

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        // todo
//    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);

        durationLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.minutesProperty());
            }

            @Override
            protected String computeValue() {
                return formatNumber(mediaItem.getMinutes());
            }
        });
        countriesLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.countriesProperty());
            }

            @Override
            protected String computeValue() {
                return formatCountries(mediaItem.getCountries());
            }
        });
        directorsLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.creatorsProperty());
            }

            @Override
            protected String computeValue() {
                return formatStringList(mediaItem.getCreators());
            }
        });
        actorsLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.actorsProperty());
            }

            @Override
            protected String computeValue() {
                return formatStringList(mediaItem.getActors());
            }
        });
        // todo
        genresLabel.setText("");
        synopsisTextArea.setText("");


        ImageView imageView = new ImageView();
        imagePane.getChildren().clear();
        imagePane.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());
        if (mediaItem.getImagePath() != null) {
            File imageFile = new File(mediaItem.getImagePath());
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);
            imageView.setStyle("-fx-border-color: black; -fx-border-width: 2px");
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }

        // todo companies
    }


    public void editMovie() {
        System.out.println("edit");



        Optional<EditMovieController.MovieData> result = main.editMovie(NavigationHistory.DialogIntention.EDIT);

        result.ifPresent(editMovie -> {
            System.out.println(editMovie.toString());

            Movie movie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getId());
            EditMovieController.changeMovie(movie, editMovie);
        });

    }

}

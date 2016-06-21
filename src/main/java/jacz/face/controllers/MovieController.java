package jacz.face.controllers;

import jacz.database.Movie;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Alberto on 19/06/2016.
 */
public class MovieController extends ProducedMediaItemController {

    @FXML
    private Label durationLabel;

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
    }


    public void editMovie() {
        Optional<EditMovieController.MovieData> result = main.editMovie(NavigationHistory.DialogIntention.EDIT);
        result.ifPresent(editMovie -> {
            System.out.println(editMovie.toString());

            Movie movie = Movie.getMovieById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getId());
            try {
                EditMovieController.changeMovie(movie, editMovie);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

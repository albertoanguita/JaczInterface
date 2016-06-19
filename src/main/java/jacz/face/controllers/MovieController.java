package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.Movie;
import jacz.database.util.GenreCode;
import jacz.face.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Created by Alberto on 19/06/2016.
 */
public class MovieController extends GenericController {

    private Movie movie;

    @FXML
    private Label titleLabel;

    @FXML
    private Label originalTitleLabel;

    @FXML
    private Label yearLabel;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // todo
    }

    @Override
    public void setMain(Main main) {
        super.setMain(main);
        movie = Movie.getMovieById(
                ClientAccessor.getInstance().getClient().getDatabases().getIntegratedDB(),
                main.getNavigationHistory().getCurrentElement().itemId);
        updateData();
    }

    private void updateData() {
        titleLabel.setText(movie.getTitle());
        originalTitleLabel.setText(movie.getOriginalTitle());
        yearLabel.setText(formatNumber(movie.getYear()));
        durationLabel.setText(formatNumber(movie.getMinutes()));
        countriesLabel.setText(formatCountries(movie.getCountries()));
        directorsLabel.setText(formatStringList(movie.getCreators()));
        actorsLabel.setText(formatStringList(movie.getActors()));
        genresLabel.setText(formatGenres(movie.getGenres()));
        synopsisTextArea.setText(movie.getSynopsis());
        // todo companies
    }

    private String formatNumber(Integer year) {
        return year != null ? year.toString() : null;
    }

    private String formatCountries(List<CountryCode> countries) {
        return formatList(countries, CountryCode::getName);
    }

    private String formatGenres(List<GenreCode> genres) {
        return formatList(genres, Enum::name);
    }

    private String formatStringList(List<String> list) {
        return formatList(list, name -> name);
    }

    private <E> String formatList(List<E> list, Function<E, String> mapper) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return "";
        } else {
            String listStr = mapper.apply(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                listStr += ", " + mapper.apply(list.get(i));
            }
            return listStr;
        }
    }


}

package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.util.GenreCode;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Created by alberto on 6/20/16.
 */
public class MediaItemController extends GenericController {

    protected MediaDatabaseProperties.MediaItem mediaItem;

    @FXML
    protected Label titleLabel;

    @FXML
    protected Label originalTitleLabel;

    @FXML
    protected Label yearLabel;

    @FXML
    private TextArea synopsisTextArea;

    @FXML
    private Label countriesLabel;

    @FXML
    private Label creatorsLabel;

    @FXML
    private Label actorsLabel;

    @FXML
    Button editButton;

    @FXML
    Button removeLocalContentButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMain(Main main) throws ItemNoLongerExistsException {
        super.setMain(main);
        mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);
        if (mediaItem == null) {
            // the item no longer exists
            throw new ItemNoLongerExistsException();
        }

        titleLabel.textProperty().bind(mediaItem.titleProperty());
        originalTitleLabel.textProperty().bind(mediaItem.originalTitleProperty());
        yearLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.yearProperty());
            }

            @Override
            protected String computeValue() {
                return formatNumber(mediaItem.getYear());
            }
        });
        synopsisTextArea.textProperty().bind(mediaItem.synopsisProperty());
        countriesLabel.textProperty().bind(new StringBinding() {
            {
                super.bind(mediaItem.countriesProperty());
            }

            @Override
            protected String computeValue() {
                return formatCountries(mediaItem.getCountries());
            }
        });
        creatorsLabel.textProperty().bind(new StringBinding() {
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

        // if there is no local content, the remove local content button must be disabled
        removeLocalContentButton.disableProperty().bind(mediaItem.localIdProperty().isNull().and(mediaItem.deletedIdProperty().isNull()));
    }

    protected String formatNumber(Integer year) {
        return year != null ? year.toString() : null;
    }

    protected String formatCountries(List<CountryCode> countries) {
        return formatList(countries, CountryCode::getName);
    }

    protected String formatGenres(List<GenreCode> genres) {
        return formatList(genres, Enum::name);
    }

    protected String formatStringList(List<String> list) {
        return formatList(list, name -> name);
    }

    protected <E> String formatList(List<E> list, Function<E, String> mapper) {
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

    public void removeLocalContent() throws IOException {
        if (ClientAccessor.getInstance().getClient().removeLocalContent(mediaItem.getItem())) {
            // the integrated item is totally removed
            // todo this code is in main controller -> DRY
            main.navigateBackwards();
            //main.getNavigationHistory().backwards();
            //main.displayCurrentNavigationWindow();
        }
    }
}

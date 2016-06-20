package jacz.face.controllers;

import jacz.database.Movie;
import jacz.database.util.GenreCode;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;
import jacz.face.state.MediaDatabaseProperties;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.Controls;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 6/20/16.
 */
public abstract class EditProducedMediaItemController extends EditMediaItemController {

    public static class ProducedMediaItemData extends MediaItemData {

        protected final List<String> companies;

        protected final List<GenreCode> genres;

        protected final String imagePath;

        public ProducedMediaItemData(MediaItemData mediaItemData, List<String> companies, List<GenreCode> genres, String imagePath) {
            super(mediaItemData.title, mediaItemData.originalTitle, mediaItemData.year, mediaItemData.synopsis, mediaItemData.countries, mediaItemData.creators, mediaItemData.actors);
            this.companies = companies;
            this.genres = genres;
            this.imagePath = imagePath;
        }
    }


    @FXML
    FlowPane productionCompaniesFlowPane;

    @FXML
    FlowPane genresFlowPane;


    @Override
    public void setMain(Main main) {
        super.setMain(main);

        // todo get user intention from main
        if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
            // load the controls data from the edited item
            MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);

            Controls.stringListPane(productionCompaniesFlowPane, mediaItem.getProductionCompanies());
            Controls.genreListPane(genresFlowPane, mediaItem.getGenres());
        } else {
            Controls.stringListPane(productionCompaniesFlowPane, new ArrayList<>());
            Controls.genreListPane(genresFlowPane, new ArrayList<>());
        }
    }

    public ProducedMediaItemData buildProducedMediaItemData() {
        return new ProducedMediaItemData(buildMediaItemData(), Controls.getSelectedStringValues(productionCompaniesFlowPane), Controls.getSelectedGenres(genresFlowPane));
    }


    public static void changeMovie(Movie movie, ProducedMediaItemData producedMediaItemData) {
        EditMediaItemController.changeMovie(movie, producedMediaItemData);
        movie.setProductionCompanies(producedMediaItemData.companies);
        movie.setGenres(producedMediaItemData.genres);
    }
}

package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
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
public class EditProducedMediaItemController extends GenericEditMediaItemController {

    public static class ProducedMediaItemData extends MediaItemData {

        public final List<String> companies;

        public final List<GenreCode> genres;

        public ProducedMediaItemData(String title, String originalTitle, Integer year, String synopsis, List<CountryCode> countries, List<String> creators, List<String> actors, List<String> companies, List<GenreCode> genres) {
            super(title, originalTitle, year, synopsis, countries, creators, actors);
            this.companies = companies;
            this.genres = genres;
        }
    }


    @FXML
    FlowPane productionCompaniesFlowPane;


    @Override
    public void setMain(Main main) {
        super.setMain(main);

        // todo get user intention from main
        if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
            // load the controls data from the edited item
            MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);

            Controls.stringListPane(productionCompaniesFlowPane, mediaItem.getProductionCompanies());
        } else {
            Controls.stringListPane(productionCompaniesFlowPane, new ArrayList<>());
        }

    }

}

package jacz.face.controllers;

import jacz.database.TVSeries;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.main.Main;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Alberto on 21/06/2016.
 */
public class TVSeriesController extends ProducedMediaItemController {

    @Override
    public void setMain(Main main) {
        super.setMain(main);
    }

    public void editTVSeries() {
        // todo load local tvSeries
        Optional<EditTVSeriesController.TVSeriesData> result = main.editTVSeries(NavigationHistory.DialogIntention.EDIT, null);

        result.ifPresent(editTVSeries -> {
            System.out.println(editTVSeries.toString());
            TVSeries tvSeries = TVSeries.getTVSeriesById(ClientAccessor.getInstance().getClient().getDatabases().getLocalDB(), mediaItem.getId());
            try {
                EditTVSeriesController.changeTVSeries(tvSeries, editTVSeries);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

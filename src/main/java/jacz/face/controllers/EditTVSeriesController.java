package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.database.TVSeries;
import jacz.face.main.Main;

import java.io.IOException;

/**
 * Created by Alberto on 21/06/2016.
 */
public class EditTVSeriesController extends EditProducedCreationItemController {

    public static class TVSeriesData extends ProducedMediaItemData {

        public TVSeriesData(ProducedMediaItemData producedMediaItemData) {
            super(producedMediaItemData, producedMediaItemData.companies, producedMediaItemData.genres, producedMediaItemData.imagePath);
        }
    }

    @Override
    public void setMain(Main main) {
        System.out.println("open edit tv series controller");
        super.setMain(main);
    }

    public TVSeriesData buildTVSeriesData() {
        return new TVSeriesData(buildProducedMediaItemData());
    }

    public static DatabaseItem changeTVSeries(TVSeries tvSeries, TVSeriesData tvSeriesData) throws IOException {
        EditProducedCreationItemController.changeProducedCreationItem(tvSeries, tvSeriesData);
        return ClientAccessor.getInstance().getClient().localItemModified(tvSeries);
    }
}

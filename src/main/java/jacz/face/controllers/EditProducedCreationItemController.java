package jacz.face.controllers;

import org.apache.commons.io.FilenameUtils;
import jacz.database.DatabaseItem;
import jacz.database.ProducedCreationItem;
import jacz.database.util.GenreCode;
import jacz.database.util.ImageHash;
import jacz.face.main.Main;
import jacz.face.util.Controls;
import jacz.face.util.Util;
import org.aanguita.jacuzzi.lists.tuple.Duple;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 6/20/16.
 */
public abstract class EditProducedCreationItemController extends EditCreationItemController {

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
    private FlowPane productionCompaniesFlowPane;

    @FXML
    private FlowPane genresFlowPane;

    @FXML
    private Pane imagePane;

    private String selectedImage = null;


    @Override
    public void setMainItemAndMasker(Main main, DatabaseItem item, Pane rootPane) throws ItemNoLongerExistsException {
        super.setMainItemAndMasker(main, item, rootPane);

        //if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
        if (item != null) {
            // load the controls data from the edited item
            ProducedCreationItem producedCreationItem = (ProducedCreationItem) item;
            //MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);

            Controls.stringListPane(productionCompaniesFlowPane, producedCreationItem.getProductionCompanies());
            Controls.genreListPane(genresFlowPane, producedCreationItem.getGenres());
            selectedImage = Util.buildImagePath(producedCreationItem);
            Util.displayImage(imagePane, selectedImage);
        } else {
            Controls.stringListPane(productionCompaniesFlowPane, new ArrayList<>());
            Controls.genreListPane(genresFlowPane, new ArrayList<>());
        }
    }

    public void chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set movie poster");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedImageFile = fileChooser.showOpenDialog(main.getPrimaryStage());
        selectedImage = selectedImageFile != null ? selectedImageFile.toString() : null;
        System.out.println(selectedImage);
        Util.displayImage(imagePane, selectedImage);
    }

    public void clearImageFile() {
        selectedImage = null;
        Util.displayImage(imagePane, null);
    }

    public ProducedMediaItemData buildProducedMediaItemData() {
        return new ProducedMediaItemData(buildMediaItemData(), Controls.getSelectedStringValues(productionCompaniesFlowPane), Controls.getSelectedGenres(genresFlowPane), selectedImage);
    }


    protected static void changeProducedCreationItem(ProducedCreationItem producedCreationItem, ProducedMediaItemData producedMediaItemData) throws IOException {
        EditCreationItemController.changeCreationItem(producedCreationItem, producedMediaItemData);
        producedCreationItem.setProductionCompanies(producedMediaItemData.companies);
        producedCreationItem.setGenres(producedMediaItemData.genres);
        ImageHash imageHash = null;
        if (producedMediaItemData.imagePath != null) {
            Duple<String, String> pathAndHash = ClientAccessor.getInstance().getClient().addLocalImageFile(producedMediaItemData.imagePath, true);
            String extension = FilenameUtils.getExtension(pathAndHash.element1);
            System.out.println("Extension: " + extension);
            imageHash = new ImageHash(pathAndHash.element2, extension);
        }
        producedCreationItem.setImageHash(imageHash);
    }
}

package jacz.face.controllers;

import com.neovisionaries.i18n.CountryCode;
import jacz.database.CreationItem;
import jacz.database.DatabaseItem;
import jacz.face.main.Main;
import jacz.face.util.Controls;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by alberto on 6/20/16.
 */
public abstract class EditCreationItemController extends GenericEditDialogController {

    public static class MediaItemData {

        public final String title;

        protected final String originalTitle;

        protected final Integer year;

        protected final String synopsis;

        protected final List<CountryCode> countries;

        protected final List<String> creators;

        protected final List<String> actors;

        public MediaItemData(String title, String originalTitle, Integer year, String synopsis, List<CountryCode> countries, List<String> creators, List<String> actors) {
            this.title = title;
            this.originalTitle = originalTitle;
            this.year = year;
            this.synopsis = synopsis;
            this.countries = countries;
            this.creators = creators;
            this.actors = actors;
        }
    }

    @FXML
    TextField titleTextField;

    @FXML
    TextField originalTitleTextField;

    @FXML
    TextField yearTextField;

    @FXML
    TextArea synopsisTextArea;

    @FXML
    HBox countriesHBox;

    @FXML
    FlowPane creatorsFlowPane;

    @FXML
    FlowPane actorsFlowPane;

    @Override
    public void setMainItemAndMasker(Main main, DatabaseItem item, Pane rootPane) throws ItemNoLongerExistsException {
        super.setMainItemAndMasker(main, item, rootPane);

        //if (main.getNavigationHistory().getCurrentDialogIntention() == NavigationHistory.DialogIntention.EDIT) {
        if (item != null) {
            // load the controls data from the edited item
            CreationItem creationItem = (CreationItem) item;
            //MediaDatabaseProperties.MediaItem mediaItem = PropertiesAccessor.getInstance().getMediaDatabaseProperties().getMediaItem(main.getNavigationHistory().getCurrentElement().mediaItemType, main.getNavigationHistory().getCurrentElement().itemId);
            System.out.println(creationItem.getItemType());
            System.out.println(creationItem.getTitle());

            titleTextField.setText(creationItem.getTitle() != null ? creationItem.getTitle() : "");
            titleTextField.setEditable(false);
            originalTitleTextField.setText(creationItem.getOriginalTitle() != null ? creationItem.getOriginalTitle() : "");
            yearTextField.setText(creationItem.getYear() != null ? creationItem.getYear().toString() : "");
            synopsisTextArea.setText(creationItem.getSynopsis() != null ? creationItem.getSynopsis() : "");
            Controls.countryListPane(countriesHBox, creationItem.getCountries());
            Controls.stringListPane(creatorsFlowPane, creationItem.getCreators());
            Controls.stringListPane(actorsFlowPane, creationItem.getActors());
        } else {
            Controls.countryListPane(countriesHBox, new ArrayList<>());
            Controls.stringListPane(creatorsFlowPane, new ArrayList<>());
            Controls.stringListPane(actorsFlowPane, new ArrayList<>());
        }

    }

    @Override
    public void registerValidators() {
        super.registerValidators();
        Platform.runLater(() -> {
            validationSupport.registerValidator(titleTextField, true, new Validator<String>() {
                @Override
                public ValidationResult apply(Control control, String newValue) {
                    return ValidationResult.fromErrorIf(control, "A title is required. Title cannot be changed afterwards", !titleIsCorrect(newValue));
                }
            });
            validationSupport.registerValidator(yearTextField, false, new Validator<String>() {
                @Override
                public ValidationResult apply(Control control, String newValue) {
                    return ValidationResult.fromErrorIf(control, "Empty for unknown, number above 1900 otherwise", !yearIsCorrect(newValue));
                }
            });
        });
    }

    private boolean titleIsCorrect(String text) {
        return !text.isEmpty();
    }

    private boolean yearIsCorrect(String text) {
        if (text.isEmpty()) {
            return true;
        } else {
            try {
                int port = Integer.parseInt(text);
                return port > 1900;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }


    public MediaItemData buildMediaItemData() {
        return new MediaItemData(parseText(titleTextField.getText()), parseText(originalTitleTextField.getText()), parseInt(yearTextField.getText()), parseText(synopsisTextArea.getText()), Controls.getSelectedCountries(countriesHBox), Controls.getSelectedStringValues(creatorsFlowPane), Controls.getSelectedStringValues(actorsFlowPane));
    }

    public static Integer parseInt(String text) {
        return text != null && !text.isEmpty() ? Integer.parseInt(text) : null;
    }

    public static String parseText(String text) {
        return text != null && !text.isEmpty() ? text : null;
    }

    protected static void changeCreationItem(CreationItem creationItem, MediaItemData mediaItemData) {
        creationItem.setOriginalTitle(mediaItemData.originalTitle);
        creationItem.setYear(mediaItemData.year);
        creationItem.setSynopsis(mediaItemData.synopsis);
        creationItem.setCountries(mediaItemData.countries);
        creationItem.setCreators(mediaItemData.creators);
        creationItem.setActors(mediaItemData.actors);
    }
}

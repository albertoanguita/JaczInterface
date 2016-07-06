package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.face.main.Main;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.layout.Pane;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.validation.ValidationSupport;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by alberto on 7/6/16.
 */
public class GenericEditDialogController extends GenericControllerWithItem {

    protected ValidationSupport validationSupport;

    //protected MaskerPane maskerPane;
    protected Pane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // todo validators do not decorate initially. See if it can be fixed
        validationSupport = new ValidationSupport();
        registerValidators();
    }

    public void setMainItemAndMasker(Main main, DatabaseItem item, Pane rootPane) throws ItemNoLongerExistsException {
        super.setMainAndItem(main, item);
        this.rootPane = rootPane;
    }

    public void registerValidators() {

    }

    public final ReadOnlyBooleanProperty invalidProperty() {
        return validationSupport.invalidProperty();
    }
}

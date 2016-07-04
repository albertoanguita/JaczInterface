package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.face.main.Main;

/**
 * Created by Alberto on 26/06/2016.
 */
public abstract class GenericControllerWithItem extends GenericController {

    protected DatabaseItem item;

    public void setMainAndItem(Main main, DatabaseItem item) throws ItemNoLongerExistsException {
        setMain(main);
        this.item = item;
    }
}

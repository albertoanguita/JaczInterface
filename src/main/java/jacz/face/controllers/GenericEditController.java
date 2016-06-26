package jacz.face.controllers;

import jacz.database.DatabaseItem;
import jacz.face.main.Main;

/**
 * Created by Alberto on 26/06/2016.
 */
public abstract class GenericEditController extends GenericController {

    protected DatabaseItem item;

    public void setMainAndItem(Main main, DatabaseItem item) {
        setMain(main);
        this.item = item;
    }
}

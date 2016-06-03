package jacz.face.controllers;

import jacz.face.main.Main;
import javafx.fxml.Initializable;

/**
 * Created by alberto on 6/3/16.
 */
public abstract class GenericController implements Initializable {

    protected Main main;

    public void setMain(Main main) {
        this.main = main;
    }
}

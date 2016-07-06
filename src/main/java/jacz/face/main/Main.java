package jacz.face.main;

import jacz.database.DatabaseItem;
import jacz.database.Movie;
import jacz.database.TVSeries;
import jacz.database.VideoFile;
import jacz.face.controllers.*;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.MediaItemType;
import jacz.face.util.VideoFilesEditor;
import jacz.util.concurrency.task_executor.ThreadExecutor;
import jacz.util.lists.tuple.Duple;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    public enum MediaView {
        MOVIES,
        SERIES,
        FAVORITES
    }

    private static final String BASE_DIR = "./etc";

    private Stage primaryStage;

    private Duple<MainController, Parent> mainController;

    private Duple<CreateConfigController, Parent> createConfigController;

    private NavigationHistory navigationHistory;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("media manager");

        // build the singleton instance of the properties accessor
        PropertiesAccessor.getInstance();


        if (!mainController.element1.listAvailableConfigs(BASE_DIR).isEmpty()) {
            // there is a profile available
            //mainController.element1.loadConfig(BASE_DIR);
            //primaryStage.setScene(new Scene(mainController.element2, 1000, 675));
            gotoMain();
        } else {
            // we need to create a profile
            //primaryStage.setScene(new Scene(createConfigController.element2, 1000, 675));
            gotoCreateConfig();
        }
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void gotoMain() throws IOException {
        mainController.element1.loadConfig(BASE_DIR);
        gotoScene(new Scene(mainController.element2, 1000, 675));
        displayCurrentNavigationWindow();
    }

    public void displayCurrentNavigationWindow() throws IOException {
        try {
            mainController.element1.moveToNavigationElement(navigationHistory.getCurrentElement());
        } catch (ItemNoLongerExistsException e) {
            System.out.println("auto back");
            navigationHistory.destroyCurrentElementAndMoveBack();
            displayCurrentNavigationWindow();
        }
    }

    public void gotoCreateConfig() {
        primaryStage.setScene(new Scene(createConfigController.element2, 1000, 675));
    }

    private void gotoScene(Scene scene) {
        String style = getClass().getResource("/styles/default.css").toExternalForm();
        scene.getStylesheets().addAll(style);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("INIT");

        navigationHistory = new NavigationHistory(NavigationHistory.Element.mediaList(MediaItemType.MOVIE));

        // load and store available scenes with their controllers
        mainController = loadController("/view/main.fxml");
        createConfigController = loadController("/view/create_profile.fxml");
    }

    private <T extends GenericController> Duple<T, Parent> loadController(String resourcePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource(resourcePath).openStream());
        try {
            ((GenericController) fxmlLoader.getController()).setMain(this);
        } catch (ItemNoLongerExistsException e) {
            // ignore, cannot happen
        }
        return new Duple<>(fxmlLoader.getController(), root);
    }


    public NavigationHistory getNavigationHistory() {
        return navigationHistory;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("END");
        mainController.element1.stop();
        System.out.println("main controller stopped. Stopping properties...");
        PropertiesAccessor.getInstance().stop();
        System.out.println("Properties stopped");

        System.out.println(ThreadExecutor.getRegisteredClients());
    }

    public static void main(String[] args) {
        launch(args);

    }

    public Optional<EditMovieController.MovieData> editMovie(NavigationHistory.DialogIntention dialogIntention, Movie movie) {
        Duple<Dialog<EditMovieController.MovieData>, EditMovieController> dialogAndController = editDialog(dialogIntention, movie, "/view/edit_movie_2.fxml", "new movie");
        dialogAndController.element1.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialogAndController.element2.buildMovieData(movie);
            } else {
                return null;
            }
        });
        //dialogAndController.element2.registerValidators();
        Optional<EditMovieController.MovieData> result = dialogAndController.element1.showAndWait();
        //dialogAndController.element2.registerValidators();
        return result;
    }

    public Optional<EditTVSeriesController.TVSeriesData> editTVSeries(NavigationHistory.DialogIntention dialogIntention, TVSeries tvSeries) {
        Duple<Dialog<EditTVSeriesController.TVSeriesData>, EditTVSeriesController> dialogAndController = editDialog(dialogIntention, tvSeries, "/view/edit_tvseries.fxml", "new tv series");
        dialogAndController.element1.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialogAndController.element2.buildTVSeriesData();
            } else {
                return null;
            }
        });
        return dialogAndController.element1.showAndWait();
    }

    public Optional<VideoFilesEditor.UpdateResult<VideoFile>> editMovieFiles(NavigationHistory.DialogIntention dialogIntention, Movie movie) {
        Duple<Dialog<VideoFilesEditor.UpdateResult<VideoFile>>, EditFilesController> dialogAndController = editDialog(dialogIntention, movie, "/view/edit_files.fxml", "movie files");
        dialogAndController.element1.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialogAndController.element2.buildUpdatedVideoFiles(movie);
            } else {
                return null;
            }
        });
        return dialogAndController.element1.showAndWait();
    }

    private  <T, Y extends GenericEditDialogController> Duple<Dialog<T>, Y> editDialog(NavigationHistory.DialogIntention dialogIntention, DatabaseItem item, String fxmlPath, String title) {
        try {
            navigationHistory.setCurrentDialogIntention(dialogIntention);
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane newMoviePane = fxmlLoader.load(getClass().getResource(fxmlPath).openStream());

            Dialog<T> newMovieDialog = new Dialog<>();
            newMovieDialog.setTitle(title);
            newMovieDialog.getDialogPane().setContent(newMoviePane);

            // Set the button types.
            newMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            Node okButton = newMovieDialog.getDialogPane().lookupButton(ButtonType.OK);


            Y controller = fxmlLoader.getController();
            try {
                controller.setMainItemAndMasker(this, item, newMoviePane);
                okButton.disableProperty().bind(controller.invalidProperty());
            } catch (ItemNoLongerExistsException e) {
                // todo can happen???
                e.printStackTrace();
            }
            return new Duple<>(newMovieDialog, controller);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void navigateBackwards() throws IOException {
        getNavigationHistory().backwards();
        displayCurrentNavigationWindow();
    }

    public void navigateForward() throws IOException {
        getNavigationHistory().forward();
        displayCurrentNavigationWindow();
    }


}


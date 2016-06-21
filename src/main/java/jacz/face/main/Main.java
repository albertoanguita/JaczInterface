package jacz.face.main;

import jacz.database.Movie;
import jacz.face.controllers.*;
import jacz.face.controllers.navigation.NavigationHistory;
import jacz.face.state.PropertiesAccessor;
import jacz.face.util.MediaItemType;
import jacz.util.concurrency.task_executor.ThreadExecutor;
import jacz.util.lists.tuple.Duple;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        mainController.element1.moveToNavigationElement(navigationHistory.getCurrentElement());
    }

    public void gotoCreateConfig() {
        primaryStage.setScene(new Scene(createConfigController.element2, 1000, 675));
    }

    private void gotoScene(Scene scene) {
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
        ((GenericController) fxmlLoader.getController()).setMain(this);
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

    public Optional<EditMovieController.MovieData> editMovie(NavigationHistory.DialogIntention dialogIntention) {
        Duple<Dialog<EditMovieController.MovieData>, EditMovieController> dialogAndController = editItem(dialogIntention, "/view/edit_movie.fxml", "new movie");
        dialogAndController.element1.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialogAndController.element2.buildMovieData();
            } else {
                return null;
            }
        });
        return dialogAndController.element1.showAndWait();
    }

//    public Optional<EditMovieController.MovieData> editMovie2(NavigationHistory.DialogIntention dialogIntention) {
//        try {
//            navigationHistory.setCurrentDialogIntention(dialogIntention);
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            AnchorPane newMoviePane = fxmlLoader.load(getClass().getResource("/view/edit_movie.fxml").openStream());
//            //AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("view/settings.fxml").openStream());
//            final EditMovieController editMovieController = fxmlLoader.getController();
//            editMovieController.setMain(this);
//
//            Dialog<EditMovieController.MovieData> newMovieDialog = new Dialog<>();
//            newMovieDialog.setTitle("new movie");
//            newMovieDialog.getDialogPane().setContent(newMoviePane);
//
//            // Set the button types.
//            newMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
//
//            // Convert the result to a settings value when the ok button is clicked.
//            newMovieDialog.setResultConverter(dialogButton -> {
//                if (dialogButton == ButtonType.OK) {
//                    return editMovieController.buildMovieData();
//                } else {
//                    return null;
//                }
//            });
//
//            return newMovieDialog.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

//    public Optional<EditTVSeriesController.TVSeriesData> editTVSeries(NavigationHistory.DialogIntention dialogIntention) {
//        try {
//            navigationHistory.setCurrentDialogIntention(dialogIntention);
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            AnchorPane newMoviePane = fxmlLoader.load(getClass().getResource("/view/edit_movie.fxml").openStream());
//            //AnchorPane settingsPane = fxmlLoader.load(getClass().getResource("view/settings.fxml").openStream());
//            final EditMovieController editMovieController = fxmlLoader.getController();
//            editMovieController.setMain(this);
//
//            Dialog<EditMovieController.MovieData> newMovieDialog = new Dialog<>();
//            newMovieDialog.setTitle("new movie");
//            newMovieDialog.getDialogPane().setContent(newMoviePane);
//
//            // Set the button types.
//            newMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
//
//            // Convert the result to a settings value when the ok button is clicked.
//            newMovieDialog.setResultConverter(dialogButton -> {
//                if (dialogButton == ButtonType.OK) {
//                    return editMovieController.buildMovieData();
//                } else {
//                    return null;
//                }
//            });
//
//            return newMovieDialog.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

    public Optional<EditTVSeriesController.TVSeriesData> editTVSeries(NavigationHistory.DialogIntention dialogIntention) {
        Duple<Dialog<EditTVSeriesController.TVSeriesData>, EditTVSeriesController> dialogAndController = editItem(dialogIntention, "/view/edit_tvseries.fxml", "new tv series");
        dialogAndController.element1.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return dialogAndController.element2.buildTVSeriesData();
            } else {
                return null;
            }
        });
        return dialogAndController.element1.showAndWait();
    }

    public <T extends EditMediaItemController.MediaItemData, Y extends GenericController> Duple<Dialog<T>, Y> editItem(NavigationHistory.DialogIntention dialogIntention, String fxmlPath, String title) {
        try {
            navigationHistory.setCurrentDialogIntention(dialogIntention);
            FXMLLoader fxmlLoader = new FXMLLoader();
            AnchorPane newMoviePane = fxmlLoader.load(getClass().getResource(fxmlPath).openStream());

            Dialog<T> newMovieDialog = new Dialog<>();
            newMovieDialog.setTitle(title);
            newMovieDialog.getDialogPane().setContent(newMoviePane);

            // Set the button types.
            newMovieDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            // Convert the result to a settings value when the ok button is clicked.
//            newMovieDialog.setResultConverter(dialogButton -> {
//                if (dialogButton == ButtonType.OK) {
//                    switch (mediaItemType) {
//                        case MOVIE:
//                            final EditMovieController editMovieController = fxmlLoader.getController();
//                            editMovieController.setMain(this);
//                            return editMovieController.buildMovieData();
//                        case TV_SERIES:
//                            final EditTVSeriesController editTVSeriesController = fxmlLoader.getController();
//                            editTVSeriesController.setMain(this);
//                            return editTVSeriesController.buildTVSeriesData();
//                        case CHAPTER:
//                            return null;
//                        default:
//                            return null;
//                    }
//                } else {
//                    return null;
//                }
//            });
            Y controller = fxmlLoader.getController();
            controller.setMain(this);
            return new Duple<>(newMovieDialog, controller);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}


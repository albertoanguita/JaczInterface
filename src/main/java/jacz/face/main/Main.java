package jacz.face.main;

import jacz.face.controllers.CreateConfigController;
import jacz.face.controllers.GenericController;
import jacz.face.controllers.MainController;
import jacz.face.state.PropertiesAccessor;
import jacz.util.concurrency.task_executor.ThreadExecutor;
import jacz.util.lists.tuple.Duple;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

    private MediaView currentMediaView;

    @Override
    public void start(Stage primaryStage) throws Exception{
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

        // load and store available scenes with their controllers
        mainController = loadController("/view/main.fxml");
        createConfigController = loadController("/view/create_profile.fxml");
    }

    private <T extends GenericController> Duple<T, Parent> loadController(String resourcePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource(resourcePath).openStream());
        ((GenericController)fxmlLoader.getController()).setMain(this);
        return new Duple<>(fxmlLoader.getController(), root);
    }


    public MediaView getCurrentMediaView() {
        return currentMediaView;
    }

    public void setCurrentMediaView(MediaView currentMediaView) {
        this.currentMediaView = currentMediaView;
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
}


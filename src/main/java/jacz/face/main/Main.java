package jacz.face.main;

import jacz.face.controllers.CreateConfigController;
import jacz.face.controllers.MainController;
import jacz.util.lists.tuple.Duple;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static final String BASE_DIR = "./etc";

    private Duple<MainController, Parent> mainController;

    private Duple<CreateConfigController, Parent> createConfigController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("media manager");

        if (!mainController.element1.listAvailableConfigs(BASE_DIR).isEmpty()) {
            // there is a profile available
            mainController.element1.loadConfig(BASE_DIR);
            primaryStage.setScene(new Scene(mainController.element2, 1000, 675));
        } else {
            // we need to create a profile
            primaryStage.setScene(new Scene(createConfigController.element2, 1000, 675));
        }
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("INIT");

        // load and store available scenes with their controllers
        mainController = loadController("/view/JacuzziMain.fxml");
        createConfigController = loadController("/view/createConfig.fxml");
    }

    private <T> Duple<T, Parent> loadController(String resourcePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource(resourcePath).openStream());
        return new Duple<>(fxmlLoader.getController(), root);
    }




    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("END");
        mainController.element1.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


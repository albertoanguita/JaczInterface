package jacz.face.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/JacuzziMain.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/view/createConfig.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1000, 675));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("INIT");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("END");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package jacz.face.actions;

import jacz.face.controllers.MainController;
import javafx.application.Platform;

/**
 * Created with IntelliJ IDEA.
 * User: Alberto
 * Date: 30/12/13
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class BuildStringTask implements Runnable {

    private UserOfTask userOfTask;

    private MainController jacuzziMainController;

    private String str;

    public BuildStringTask(UserOfTask userOfTask, MainController jacuzziMainController, String str) {
        this.userOfTask = userOfTask;
        this.jacuzziMainController = jacuzziMainController;
        this.str = str;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            final String result = "-" + str + "/";
            System.out.println("end");
            userOfTask.setStr(result);
            jacuzziMainController.setText(result);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    userOfTask.setStr(result);
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

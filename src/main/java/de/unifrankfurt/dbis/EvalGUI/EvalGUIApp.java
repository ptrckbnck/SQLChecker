package de.unifrankfurt.dbis.EvalGUI;

import de.unifrankfurt.dbis.StudentGUI.StudentGUIApp;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.PrintStream;
import java.net.URL;
import java.util.List;


public class EvalGUIApp extends Application {
    private static PrintStream sysOut;
    private static Stage primaryStage;
    private static List<String> parameters;
    private static HostServices hostServices;


    public static Stage getPrimaryStage() {
        return EvalGUIApp.primaryStage;
    }

    private static void showError(Thread thread, Throwable throwable) {
        StudentGUIApp.showError(thread, throwable); //TODO maybe inline
    }

    @Override
    public void start(Stage stage) throws Exception {

        EvalGUIApp.sysOut = System.out;
        EvalGUIApp.primaryStage = stage;
        EvalGUIApp.parameters = getParameters().getRaw();
        EvalGUIApp.hostServices = getHostServices();
        Thread.currentThread().setUncaughtExceptionHandler(EvalGUIApp::showError);


        URL fxml = getClass().getResource("/EvalGUIMain.fxml");
        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/sql-icon.png")));

        primaryStage.show();

    }
}
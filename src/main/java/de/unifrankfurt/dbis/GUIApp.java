package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.GUI.ExceptionAlert;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;


public class GUIApp extends Application {
    private static Stage primaryStage;
    private static HostServices hostServices;
    private static List<String> parameters;
    private static PrintStream sysOut;

    private static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            ExceptionAlert alert = new ExceptionAlert(e);
            e.printStackTrace(sysOut);
            alert.showAndWait();
        } else {
            e.printStackTrace();

        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        GUIApp.sysOut = System.out;
        GUIApp.primaryStage = primaryStage;
        GUIApp.parameters = getParameters().getRaw();
        GUIApp.hostServices = getHostServices();
        Thread.currentThread().setUncaughtExceptionHandler(GUIApp::showError);


        URL fxml = getClass().getResource("/mainPane.fxml");
        Parent root = FXMLLoader.load(fxml);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/sql-icon.png")));

        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return GUIApp.primaryStage;
    }

    public static HostServices getHostServicesStatic() {
        return hostServices;
    }

    public static List<String> getRunnerParameters() {
        return parameters;
    }
}
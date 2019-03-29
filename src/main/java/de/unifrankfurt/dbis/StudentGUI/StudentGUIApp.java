package de.unifrankfurt.dbis.StudentGUI;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
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


public class StudentGUIApp extends Application {
    private static PrintStream sysOut;
    private static List<String> parameters;
    private static Stage primaryStage;
    private static HostServices hostServices;

    public static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            ExceptionAlert alert = new ExceptionAlert(e);
            e.printStackTrace(sysOut);
            alert.showAndWait();
        } else {
            e.printStackTrace();

        }
    }

    public static Stage getPrimaryStage() {
        return StudentGUIApp.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        StudentGUIApp.sysOut = System.out;
        StudentGUIApp.primaryStage = primaryStage;
        StudentGUIApp.parameters = getParameters().getRaw();
        StudentGUIApp.hostServices = getHostServices();
        Thread.currentThread().setUncaughtExceptionHandler(StudentGUIApp::showError);


        URL fxml = getClass().getResource("/StudentGUIMain.fxml");
        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/sql-icon.png")));

        primaryStage.show();
    }

    public static HostServices getHostServicesStatic() {
        return hostServices;
    }

    public static List<String> getRunnerParameters() {
        return parameters;
    }

    public void stop() {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
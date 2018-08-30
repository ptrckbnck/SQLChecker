package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.GUI.ExceptionAlert;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class Main extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage;
        Thread.currentThread().setUncaughtExceptionHandler(Main::showError);


        URL fxml = getClass().getResource("/mainPane.fxml");
        Parent root = FXMLLoader.load(fxml);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/sql-icon.png")));

        primaryStage.show();
    }

    private static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            ExceptionAlert alert = new ExceptionAlert(e);
            alert.showAndWait();
        } else {
            e.printStackTrace();

        }
    }

    public static Stage getPrimaryStage() {
        return Main.primaryStage;
    }


}
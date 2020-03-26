package de.unifrankfurt.dbis.StudentGUI;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class StudentGUIApp extends Application {

    public static void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            ExceptionAlert alert = new ExceptionAlert(e);
            e.printStackTrace(System.out);
            alert.showAndWait();
        } else {
            e.printStackTrace();

        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        SvgImageLoaderFactory.install();
        Thread.currentThread().setUncaughtExceptionHandler(StudentGUIApp::showError);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/StudentGUIMain.fxml"));

        Parent root = fxmlLoader.load();
        StudentGUIController controller = fxmlLoader.getController();
        controller.setParameters(this.getParameters());
        controller.setHostServices(this.getHostServices());
        controller.setPrimaryStage(stage);
        controller.start();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/logo_studentgui.svg")));

        stage.show();
    }


    public void stop() {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
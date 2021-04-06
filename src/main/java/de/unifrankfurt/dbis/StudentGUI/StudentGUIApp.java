package de.unifrankfurt.dbis.StudentGUI;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class StudentGUIApp extends Application {

    private StudentGUIController controller;

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
        controller = fxmlLoader.getController();
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

    @Override
    public void stop() {
        AbandonedConnectionCleanupThread.checkedShutdown();
        if (Objects.nonNull(controller)) {
            try {
                if (Objects.nonNull(controller.getProjectPath())) {
                    String s = "Möchten Sie das aktuelle Projekt speichern bevor Sie das Programm beenden?";
                    ButtonType beenden = new ButtonType("Beenden");
                    ButtonType ok = ButtonType.OK;
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, s, beenden, ok);
                    alert.setTitle("Projekt speichern");
                    Optional<ButtonType> result = alert.showAndWait();
                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        controller.saveProject(controller.getProjectPath());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
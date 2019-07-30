package de.unifrankfurt.dbis.EvalGUI;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.unifrankfurt.dbis.StudentGUI.StudentGUIApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Application for Evaluation of Submissions
 */
public class EvalGUIApp extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        SvgImageLoaderFactory.install();
        Thread.currentThread().setUncaughtExceptionHandler(StudentGUIApp::showError);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EvalGUIMain.fxml"));

        Parent root = fxmlLoader.load();
        EvalGUIController controller = fxmlLoader.getController();
        controller.setParameters(this.getParameters());
        controller.setHostServices(this.getHostServices());
        controller.setPrimaryStage(stage);
        controller.start();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream("/images/logo_evalgui.svg")));

        stage.show();

    }

    public void stop() {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
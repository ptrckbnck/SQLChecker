package de.unifrankfurt.dbis.GUI;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for AboutBox only initializes text.
 */
public class AboutController implements Initializable {
    public VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AboutPage.setText(vBox);

    }
}

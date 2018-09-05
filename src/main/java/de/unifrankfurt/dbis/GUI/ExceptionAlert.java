package de.unifrankfurt.dbis.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Creates and shows ErrorAlert for given exception.
 * An ExceptionAlert Object is created for every unhandled Exception in GUI.
 */
public class ExceptionAlert extends Alert {

    public ExceptionAlert(Throwable ex) {
        super(Alert.AlertType.ERROR);
        this.setTitle(ex.getMessage());
        this.setHeaderText("Exception");
        this.setContentText(ex.toString());

        // Create expandable Exception.
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        ex.printStackTrace(ps);
        String exceptionText = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        this.getDialogPane().setExpandableContent(expContent);
    }


}

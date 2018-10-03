package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.GUIApp;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

/**
 * class provides setText, which intilizes Text for AboutPage.
 */
public class AboutPage {

    /**
     * intialies Text for Aboutbox.
     *
     * @param vbox VBox which will be filled with Text.
     */
    public static void setText(VBox vbox) {

        String text = "Dieses Programm wurde zur Unterstützung von Datenbanken-Vorlesungen an der Goethe Universität " +
                "Frankfurt entwickelt. Es soll das Einreichen von SQL-bezogenen Aufgaben erleichtern.";
        String text2 = "Sollten Sie Fragen haben, lesen Sie bitte die Nutzer-Dokumentation oder wenden Sie sich an die Betreuer Ihrer Übung.";
        String text31 = "Alternativ können Sie Bug-Reporte oder Vorschläge direkt auf";
        String text33 = "einreichen.";
        String text5 = "Viel Erfolg beim Nutzen der Software.";
        Hyperlink hyperlink = new Hyperlink("SQLChecker");
        hyperlink.setOnAction(event -> GUIApp.getHostServicesStatic()
                .showDocument("https://github.com/ptrckbnck/SQLChecker"));

        List<Node> nodes1 = List.of(text.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        List<Node> nodes2 = List.of(text2.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        List<Node> nodes3 = List.of(text31.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        List<Node> nodes5 = List.of(text5.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        nodes3.add(hyperlink);
        nodes3.addAll(List.of(text33.split(" ")).stream().map(Label::new).collect(Collectors.toList()));


        vbox.getChildren().add(initFlowPane(nodes1));
        vbox.getChildren().add(initFlowPane(nodes2));
        vbox.getChildren().add(initFlowPane(nodes3));
        vbox.getChildren().add(initFlowPane(nodes5));


    }

    /**
     * creates new Flowpane with given Nodes and preset Settings..
     *
     * @param nodes List if Node
     * @return new Flowpane
     */
    private static FlowPane initFlowPane(List<Node> nodes) {
        return initFlowPane(nodes.toArray(new Node[0]));

    }

    /**
     * creates new Flowpane with given Nodes and preset Settings..
     * @param nodes array of Nodes
     * @return new Flowpane
     */
    private static FlowPane initFlowPane(Node... nodes) {
        FlowPane flowPane = new FlowPane(nodes);
        flowPane.setHgap(5);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(5, 5, 5, 5));
        return flowPane;
    }
}

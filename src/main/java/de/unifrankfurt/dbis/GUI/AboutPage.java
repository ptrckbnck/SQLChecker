package de.unifrankfurt.dbis.GUI;

import de.unifrankfurt.dbis.Main;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class AboutPage {

    static void setText(VBox vbox) {

        String text = "Dieses Programm wurde zur Unterstützung von Datenbanken-Vorlesungen an der Goethe Universität " +
                "Frankfurt am Main entwickelt. Es soll das Einreichen von SQL-bezogenen Aufgaben erleichtern.";
        String text2 = "Sollten Sie Fragen oder Anregungen haben, wenden sie sich bitte an die Betreuer Ihrer Übung.";
        String text31 = "Alternativ können Sie Bug-Reporte direkt auf";
        String text33 = "einreichen.";
        String text5 = "Viel Erfolg beim Nutzen der Software.";
        Hyperlink hyperlink = new Hyperlink("SQLChecker");
        hyperlink.setOnAction(event -> {
            Main.getHostServicesStatic().showDocument("https://github.com/ptrckbnck/SQLChecker");
        });

        List<Node> nodes1 = List.of(text.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        List<Node> nodes2 = List.of(text2.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        List<Node> nodes3 = List.of(text31.split(" ")).stream().map(Label::new).collect(Collectors.toList());
        nodes3.add(hyperlink);
        nodes3.addAll(List.of(text33.split(" ")).stream().map(Label::new).collect(Collectors.toList()));


        vbox.getChildren().add(initFlowPane(nodes1));
        vbox.getChildren().add(initFlowPane(nodes2));
        vbox.getChildren().add(initFlowPane(nodes3));


    }

    private static FlowPane initFlowPane(List<Node> nodes) {
        return initFlowPane(nodes.toArray(new Node[0]));

    }

    private static FlowPane initFlowPane(Node... nodes) {
        FlowPane flowPane = new FlowPane(nodes);
        flowPane.setHgap(5);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(5, 5, 5, 5));
        return flowPane;
    }
}

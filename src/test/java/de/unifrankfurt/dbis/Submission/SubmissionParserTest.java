package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.TestResources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SubmissionParserTest {

    String test = "/*authors*/\n" +
            "Katja Korolew;kkorolew@web.de;6983419\n" +
            "\n" +
            "/*1a*/\n" +
            "/*Beim Einfügen einer neuer ID ist es auch zwingend notwendig den weiteren Attributen Werte hinzufügen, \n" +
            "da alle Attribute die Eigenschaft NOT NULL besitzen*/\n" +
            "insert into plane values(5, 100, 'Airberlin', 'Airberling88');\n" +
            "\n" +
            "/*1b*/\n" +
            "/*Für das Erstellen einer Tablle müssen für die einzelnen Attribute dessen Datentyp festgelegt werden*/\n" +
            "create table crewhotel (\n" +
            "ID int(11) NOT NULL,\n" +
            "Name varchar(50) NOT NULL,\n" +
            "AdressID int(11) NOT NULL;)\n" +
            "\n" +
            "/*1c*/ \n" +
            "/*Für die obige erstellte Tabelle werden nun Werte für die jeweiligen Attribute eingesetzt*/\n" +
            "insert into crewhotel values(55, 'Mueller', 233), (100, 'Hof', 546), (300, 'Mustermann', 111);\n" +
            "\n" +
            "/*2a*/\n" +
            "/*Um  die komplette Tabelle Flugzeugausführung zu projezieren, selektieren wir alle Attribute mit * \n" +
            "und setzen die Bedingung, das nur Flugzeuge angezeigt werden, die eine ID von 2 haben*/\n" +
            "select * \n" +
            "from flightexecution\n" +
            "where PlaneID = 2;\n" +
            "\n" +
            "/*2b*/\n" +
            "/*Neben dem Flugzeugen mit einer ID von 2 wird zusätzlich die Bedingungen gesetzt das alle Flugzeuge weniger als 130\n" +
            "Minuten benötigen, dafür muss der Vergleichsoperator < eingesetzt werden. \n" +
            "Die Tabelle wird nach der Flugnummer projeziert und sortiert*/\n" +
            "select FlightNo\n" +
            "from flightexecution\n" +
            "where PlaneID = 2 and FlightDurationInMinutes<130\n" +
            "order by FlightNo;";
    @Test
    public void tokenizer() throws IOException, SubmissionParseException {
        String path = "/home/xyntek/Dropbox/SQLChecker/Bewertung/Blatt1/Abgaben/Katja_Korolew_s6929499_rz.uni-frankfurt.de/6983419.txt";
        String toParse = Files.readString(Paths.get(path), StandardCharsets.ISO_8859_1);
        //TODO set assert
    }


    public void parseAuthor() {
    }


    public void parse() throws IOException, SubmissionParseException {
        TestResources.DBFitSubmissionData s = TestResources.getSubmissionWAuthor();
        List<String> a = Files.readAllLines(s.getPath());
        Submission b = SubmissionParser.parseLines(a, StandardCharsets.ISO_8859_1);
        //System.out.println(b);
    }

    @Test
    public void fromToken() {
    }

    @Test
    public void splitBody() {
    }

    @Test
    void tokenizer1() {
    }
}
# SQLChecker
Ein wichtiger Bestandteil der Vorlesungungen zu Datenbanksystemen ist das Erlernen von SQL. Dazu müssen die Studenten praxisorientierte Aufgaben bewältigen. Der SQLChecker ist ein handliches Werkzeug mit dem SQL-Aufgaben erstellt und von den Studenten bearbeitet werden können. Der SQLChecker übernimmt zudem automatisiert die Auswertung der Abgaben der Studenten. 
Der SQLChecker wurde bereits mehrfach im Kurs "Datenbanksysteme 1" und im Kurs "Programmierung von Datenbanken (PDB)" an der Goethe-Universität erfolgreich eingesetzt.  

## Installation
Der SQLChecker ist vollständig in Java 11 erstellt worden. Das Projekt ist nicht mit älteren Versionen von Java kompatibel. Aktuelle Java Versionen finden sich auf z.B. unter https://adoptopenjdk.net/.
  
 Um SQLChecker vollständig nutzen zu können, müssen Sie ein SQL-Datenbankverwaltungssysteme installieren. Empfohlen wird [MariaDB Community Server-10.5.9](https://mariadb.com/downloads/) sein. Sie können überprüfen, ob Ihr Datenbanksystem korrekt installiert ist und läuft, indem Sie 
 `mysql --version` ausführen und ihr Root-Password eingeben. Für die Installation des gewählten Verwaltungssystem richten Sie sich bitte an dessen Anleitung.
 
 Studenten sind in der Lage alle Aufgaben durch Verwendung des SQLCheckers lösen. Da dieser allerdings nur über eine eingeschränkte grafische Oberfläche verfügt, empfiehlt es sich zusätzlich einen Datenbank-Clienten zu installieren, um einfacher SQL-Statements zu testen. [Workbench](https://www.mysql.com/de/products/workbench/) und [HeidiSQL](https://www.heidisql.com/) sind zwei gängige Programme.
 
 Laden Sie abschließend das aktuelle Build des [SQLCheckers](https://github.com/ptrckbnck/SQLChecker/releases) herunter. Dieses Jar-File führen Sie mit Java im Terminal aus:
 
 `java -jar sqlchecker-<Versionsnummer>.jar`
 
 Achten Sie darauf die korrekte Version von Java (mindestens 11) zu nutzen. Sie können die Version von Java mit `java --version` überprüfen. Haben Sie mehrere Versionen von Java installiert, können sie statt `java` den expliziten Pfad zur richtigen Java-Installation angeben.
 
 
  Möchten Sie SQLChecker per Doppelklick starten, müssen Sie die Datei ggf. noch ausführbar machen.  Auf den meißten Linux Systemen können sie dies mit folgendem Befehl:
  
  `sudo chmod +x sqlchecker-<Versionsnummer>.jar`
 
## Verwendung
Diese Anleitung richtet sich an Studenten, welche den SQL-Checker zur Bearbeitung der Aufgaben nutzen wollen. An Übungsleiter und andere Interessierte zur Erstellung von Aufgaben richtet sich diese [Anleitung](README-ADMIN.md) (nicht aktuell). 

 Nachdem Sie `java -jar sqlchecker-<Versionsnummer>.jar` ausgeführt haben erscheint die GUI des SQLCheckers.
  
 ### SQLChecker konfigurieren
 Der erste Schritt sollte stets sein, die Einstellung anzupassen. Dazu klicken Sie auf den Reiter Einstellung. Dort gibt es zwei Einträge, Datenbank und Student. Unter Datenbank setzen Sie alle Informationen, die Sie für eine Datenbankverbindung brauchen.
  
 Sie sollen für die Benutzung der Datenbank durch den SQLChecker einen eigenen Nutzer anlegen und es vermeiden den root Nutzer zu verwenden. 
   
 Alle Felder außer dem Reset Skript sind Pflichtfelder. Lassen sie dieses Feld zunächst frei.
  
 Unter dem Eintrag Student legen Sie die Informationen fest, die Sie brauchen um Ihre Abgabe einzureichen. Bitte füllen sie jedes Feld aus und falls Sie mit einem Partner arbeiten, auch die Felder Ihres Partners. Abgaben ohne diese Einträge werden nicht bewertet.

### Ein Projekt bearbeiten
Unter einem Projekt versteht sich die Bearbeitung eines Aufgabenblatts. Jedes Aufgabenblatt kommt mit zwei Dateien. Einem SQL-Checker-Template (\*.sqlt) und einem SQL-Skript (\*.sql).

Um ein neues Projekt anzulegen, klicken sie auf `Datei` `Neu`. Zunächst wählen sie die Template-Datei ihres Aufgabenblattes aus. Das Template definiert die Aufgabenstellung. Als nächstes geben Sie den Ort an, wo sie Ihr Projekt speichern wollen.

Nachdem sie erfolgreich ein Projekt angelegt haben, sehen Sie auf der linken Seite des Reiters Übung eine Liste der Aufgaben.

Nun legen Sie das Reset Skript fest, dass Sie zuvor übersprungen haben. Dazu gehen sie wieder zu `Einstellung` `Datenbank` und wählen sie den Pfad zum zur Übung mitgeliefertem SQL-Skript. Das Skript hat die Aufgabe, die nötigen Datenbank-Einträge für die Bearbeitung der Aufgabenstellung zu erzeugen und die Datenbank nach belieben in diesen Zustand zurückzuversetzen zu können.

Jetzt können Sie die Aufgaben bearbeiten. Wechseln dazu wieder zu `Übung` `Aufgaben`. Klicken Sie auf die Aufgabe, die Sie Bearbeiten möchten. Auf der rechten Seite, dem Textfeld namens `SQL`, können sie ihren SQL-Code zur Lösung der Aufgabe eintragen. Sie können die Code testen, indem sie in der Leiste unterhalb von `Übung` auf den Button `Ausführen` klicken. Das Ergebnis der Auswertung des SQL-Befehls erscheint im Textfeld mit dem Namen `Ausgabe`.

Nachdem Sie jede Aufgabe bearbeitet haben, können sie die Abgabe exportieren. Dazu klicken Sie auf `Abgabe` `Exportieren` und anschließend auf speichern. Nennen Sie die Datei am besten so wie vorgeschlagen. Die erzeugte Textdatei reichen Sie bei ihrem Übungsleiter ein. 

 



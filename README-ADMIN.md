#ADMIN DOC
Diese Anleitung richtet sich an diejenigen, die Übungen erstellen und auswerten möchten. Studenten die Abgaben erstellen möchte, sollten sich diese [Anleitung](README.md) durchlesen.

##SQLChecker in Console
Neben der StudentGUI und der EvaluationsGUI kann der SQLChecker auch als Terminal-Programm genutzt werden. Über das Terminal können die GUIs mit zusätzlichen Optionen gestartet werden. Auch kann die Auswertung der Studentenabgaben vollständig über Console ausgeführt werden. So kann dieser Vorgang leicht in andere Skripte eingebettet werden. Eine Übersicht der Befehle sieht man hier:   

```
sqlchecker [-c <Path>] [-csv <Path>] [-e | -s <Path (*.sqlc)>] [-h]
       [-noGui] [-onlyBest]  [-v] [--version]

SQLChecker
A tool a create and evaluate exercises for SQL.
 -c,--config <Path>           Path to config file. (*.ini) or (*.conf),
                              depends if you run Evaluation or StudentGUI.
 -csv,--csv <Path>            puts csv-report of evaluations to file at
                              Path or System.out by default.
 -e,--evaluate                starts the evaluation process of
                              submissions.
 -h,--help                    prints this help
 -noGui,--noGui               Evaluation without GUI. You need to define a
                              valid config file
 -onlyBest,--onlyBest         in csv mode, do not print all evaluations,
                              only the best of each student.
 -s,--start <Path (*.sqlc)>   runs SQLChecker-StudentGUI. This parameter
                              can be omitted. Use this if you want to
                              solve an exercise.
                              You can directly load a project file via
                              argument Path.
 -v,--verbose                 verbose mode. Prints a lot of information,
                              mainly for debugging.
    --version                 prints version of SQLChecker
```
 
##EvaluationsGUI
Die EvaluationsGUI (EGUI) startet man über den Terminal-Befehl  `sqlchecker -e`.

Ähnlich wie die bei der SGUI benötigt die EGUI diverse Informationen für die Datenbankverbindung. Diese erreicht man über den Tab Einstellungen. Zusätzlich wird für die Durchführung der Auswertung der Abgaben folgende Angaben benötigt.
- Der Pfad zum Reset Skript
- Der Pfad zu einer oder mehrer Solution Dateien der selben Übung, mit Komma getrennt
- Der Pfad zu den Abgaben, entweder direkt zu einer Datei oder zu einem Order, welcher Abgaben beinhaltet.

Die Config kann als INI-Datei exportiert werden. Dazu muss man auf `Datei` `Speichere Config` klicken. Gleichermaßen kann man eine - auch manuell erstellte - Config wieder einlesen.

Nun kann man auf dem Tab Submissions die eigentliche Evaluation durchführen. Zunächst müssen die in der Config angegeben Submissions geladen werden. Dazu klickt man auf `Load Submissions`. Es sollte eine Liste mit Abgaben in der Tabelle unten erscheinen.

Die angezeigten Abgaben können gefiltert werden. Dazu kann ein Filterterm im TextFeld Filter eingetragen werden. Wird eine Zeichenkette eingetragen und auf den Button `Filter` geklickt, werden nur die Abgaben angezeigt, welche auch die Zeichenkette beinhalten. Alternativ kann rechts, die RegEx CheckBox aktiviert werden. Nun wird die Zeichenkette als RegEx interpretiert. Nur Abgaben die über den RegEx gefunden werden werden angezeigt.

Im Textfeld CSVOutPath kann der Pfad angeben, an dem Resultat der Auswertung als CSV gespeichert werden soll. Wird nicht angegeben, wird das Ergebnis in Std.Out geschrieben.

Ist mindestens eine gültige Abgabe vorhanden, kann über den Button `Run` die Evaluation durchgeführt werden.


##Übungen definieren
Aus Sicht eines Übungsgruppenleiters sind vier Dateien erforderlich, um eine
Aufgabe erfolgreich durchzuführen: Eine Template-Datei(.sqlt), ein Reset-
skript(.sql), eine Lösungs-Datei (.sql) und eine Evaluation-Konfigurationsdatei
(.ini). Beispiele dieser Dateien sind unter [examples](/examples) zu finden.

###Die Tag-Annotation
Die Template-Datei, die Studentenabgabe und die Lösungsdatei haben eine spezielle Syntax. Sie besitzen Tag-Elemente, welche folgende Form haben:

`/*%%<name>%%<type>%%<extra>%%*/` 

Diese bestehen aus einem SQL Kommentar und stören daher nicht bei der Ausführung der Datei als SQL-Skript.
Es gibt mehrere Regeln die zu beachten sind.

- Der Tag-Name und der Tag-Type dürfen keine Whitespaces besitzen.
- Zur Zeit sind drei Typen möglich:`head`,`task`,`static`. Ein leeres Feld steht für task.
- Tag-Extra kann alles enthalten außer `%%`, `/*` und `*/`.
- Der Default-Typ ist `task`.
- Leere Elemente können weggelassen werden, wenn alle folgenden Elemente ebenfalls leer sind. `/*%%<name>%%*/` ist ein gültiger Tag.

Text zwischen zwei Tag-Elementen beinhaltet SQL-Code. Dieser Code ist dem vorherigen Tag-Element zugeordnet und wird als Body des Tag-Element bezeichnet.
####head
 Ein Tag mit Type `head` beinhaltet die Definition der Datei. Er sollte nur einmal vorhanden sein und möglichst an erster Stelle stehen. Der Name des Tags wird ignoriert. Extra beinhaltet ein Json-String. Drei Json-Attribute sind relevant `name`, `type` und `authors`. Authors sind nur für Submission relevant. Name beinhaltet den Namen der Übung. Ein Template, eine Submission und eine Solution der selben Übung müssen den gleichen Namen tragen. Type beinhaltet den Typ der Datei. Es gibt zur Zeit vier gültige Bezeichner: `submission`, `solution`, `template` und `unkown`. Authors definiert die Studenten, welche an der Submission beteiligt sind. Dies ist eine List von Listen. Eine innere Liste bezeichnet je einen Studenten. Eine solche Liste besteht aus Name, Emailadresse und Matrikelnummer des Studenten in dieser Reihenfolge.
 
 Folgendes Beispiel zeigt einen gültigen Head Tag:
  ```
 /*%%%%head%%
 {
 "name": "test",
 "type": "submission",
 "authors": [["Olaf Olafson","olafson@yahoo.de","1234567"],["Otto Normal","normal@gmx.de","1111111"]]
 }
 %%*/
 ```
 #### task & static
 Task und Static Tags haben die selbe Syntax, werden nur intern anders verwendet. Studentenabgaben beinhalten nie Static-Tags.
  Task und Static Tags können beliebige, nicht-leere Namen haben, müssen aber in der Datei einzigartig sein. 
  
  Extra ist nur in der Definition der Solution interessant. Extra besteht aus vier Felder die jeweils durch einen Punkt getrennt werden. Punkte können weggelassen werden, wenn alle folgenden Extra-Felder leer sind. Ein Task-Tag hat folgende Syntax:  
  
  `/*%%<name>%%task%%<score>.<group>.<schema>.<order>%%*/`
  
  Beispielsweise `/*%%Name%%task%%1.gruppe%%*/` ist gültiger Task-Tag.
  
- `score` ist das erste Feld. Hier wird eine positive Ganzzahl erwartet. Diese gibt an, wie viele Punkte für die Lösung der Aufgabe bzw. Gruppe angerechnet werden. Wird einer Gruppe durch verschiedenen Tags unterschiedliche Punkte zugewiesen, wird der erste gefundene Wert genommen. TODO check wie genau dies funktioniert. Wird das Feld leer gelassen, wird der Aufgabe ein Punkte zugewiesen. Eine Gruppe erhält nur Punkte, wenn alle Aufgaben der Gruppe gelöst wurden.
- Das zweite Feld `group` bezeichnet eine Gruppe von Aufgaben, die zusammengefasst werden. Um zu der gleichen Gruppe zu gehören, müssen alle müssen die Tags die gleichen Gruppennamen definieren.
- Das `schema` definiert das erwartete Schema der definierten SQL-Abfrage als Liste von Werten. Diese hat folgende Form: 

  `["<Wert1>","<Wert1>", ... ,"<WertN>"]`
  
  Sie ist angelehnt an die Json-Syntax. Die Aufgabe gilt nur als bestanden, wenn das Schema des Ergebnis der SQL-Anfrage identisch mit dem hier angegeben Schema ist.
- `order` legt fest, ob bei der Beurteilung des Ergebnis eine Ordnung zu beachten ist. Per Default wird das Ergebnis als ungeordnet betrachtet. Die Definition der Ordnung hat die selbe Form wie das Schema. Als Werte sind die Indizes der Spalten des Schemas erwartet. Kein Index sollte mehr als einmal vorkommen, kann aber weggelassen werden. Die Indizes sind in absteigender Relevanz aufgeführt

  `["<Index1>","<Index2>", ... ,"<IndexN>"]`
 
 Beispiel:
 Vergleiche folgende Tabellen:
 
| A | B | C |
|---|---|---|
| 1 | 2 | 4 |
| 1 | 3 | 4 |
| 2 | 3 | 4 |

und 

| A | B | C |
|---|---|---|
| 1 | 2 | 4 |
| 1 | 3 | 4 |
| 2 | 3 | 4 |

Die beiden Tabellen sind im unordered Mode gleich. Auch unter der Ordnung `["0"]` werden beide als gleich betrachtet. Die Tabelle wird dabei nach Spalte `A` sortiert. Da Zeile eins und zwei die gleichen Werte in Spalte `A` haben, haben die Zeilen gleiche Priorität. Unter der Ordnung `["0","1"]` werden die Spalten `A` und `B` betrachtet. Da nun auch Zeile `B` betrachtet wird, unterscheiden sich beide Tabellen. 

  
  
###Das Template
Das Template ist eine Tag-annotierte Datei. Sie enthält genau ein Head-Tag und beliebig viele Task-Tags. Eine Template-Datei wird von Studenten zum Initialisieren einer neuen Übung benötigt. Die Datei definiert, welche Aufgaben im SQLChecker zum Bearbeiten angezeigt.
 
 Das Template wird einer Solution zugeordnet und muss genau so viele Task-Tags in gleicher Reihenfolge und gleichem Namen, wie das Solution-File besitzen. Der im Head-Tag definierte Name, ist der Name der Übung und muss dem angegeben Namen im Solution-File entsprechen. Für die Task-Tags ist nur der Name relevant, die restlichen Felder können weggelassen werden. Der Body der Task-Tags ist der Code der einzelnen Aufgaben, den die Studenten beim Initialisieren der Übung im SQL-Checker angezeigt bekommen.

###Das Resetskript
Das Resetskript ist ein SQL-Skript. Der SQLChecker führt dieses Skript wärhend der Evaluation vor jedem Test einer Abgabe aus. Studenten können das Resetskript in der Student-GUI ausführen. Es sollte darauf geachtet werden, dass kein Statement SQL-Fehler verursacht. Zum Beispiel sollte `DROP DATABASE
IF EXISTS <name> ;`, statt `DROP DATABASE <name> ;` genutzt werden, damit kein Fehler auftritt, wenn noch keine entsprechende Datenbank existiert. Damit der SQL-Checker das Skript bei der Initialisierung der Übung in der StudentGUI automatisch findet, muss der Name der Datei folgendermaßen aussehen: `<name>_reset.sql`. `<name>` ist der Name der Übung, welcher in dem den Studenten mitgelieferten Template angegeben ist. Außerdem muss sich das Skript im selber Ordner befinden, in dem das Projekt initialisiert wird.

###Die Solution
Die Solution-Datei ist eine weitere Tag-annotierte Datei. Sie besitzt genau einen Head-Tag und beliebig viele Static- und Task-Tags. Im Head der Solution wird der Name der Übung definiert. Zugehörige Templates und Studenabgaben müssen sich nach diesen Namen richtigen und den gleichen Namen besitzen.
 
 Die Task-Tags definieren die Aufgaben, welche die Studenten bearbeiten sollen. Der Body des Tasks enthält ein SQL-Statement. Ist dieses Statement ein Select-Befehl wird auf Basis des Ergebnis der Ausführung des Befehls, das erwartete Ergebnis der Aufgabe generiert, nach der die Abgaben der Studenten gegen geprüft werden. Ist das Ergebnis des Tasks der Solution identisch mit dem Task der Abgabe, so gilt die Aufgabe als bestanden. Im Task-Tag können noch weitere Angaben gesetzt werden, wann eine Abgabe als bestanden gilt (siehe Task-Tag).
 
 Static-Tags beinhalten ebenfalls SQL-Statements, welche zur Überprüfung genutzt werden. Diese sind nicht für die Studenten sichtbar. Sie eignen sich um Tasks ohne Ergebnis, wie z.B. Insert-Befehle, zu testen. Nachdem Task kann der Static ausgeführt werden. Ist das Ergebnis des Static dasselbe bei der Abgabe des Studenten und der Lösung, gilt das Static als bestanden. Um Task und Static zu binden, können diese über das Extra-Feld als Zugehörige der gleichen Gruppe definiert werden.

###Die Konfigurationsdatei der Evaluation
Die Konfigurationsdatei der Evaluation ist eine INI-Datei, welche die nötigen Informationen für die Auswertung der Abgaben enthält. Diese Datei wird nur für die Evaluation und nicht die Erstellung der Abgabe benötigt. Die Schlüssel der Evaluation `database`, `username`, `password`, `hostname` und `port` werden für die Datenbankverbindung benötigt. `resetPath` gibt die Pfad zum Resetskript an. `solutionPaths` gibt den Pfad zu einer oder mehreren Lösungs-Dateien an. Mehrere Lösungen werden mit Komma separiert. `submissionPath` gibt den Pfad zu Abgaben an. Falls der angegebene Pfad ein ordner ist, wird dieser bis zu einer Tiefe von zwei nach Abgaben durchsucht.


##TODO
Die Ausführung läuft ebenfalls über die SQLChecker Jar Datei. Das Pro-
gramm wird über das Terminal gestartet und benötigt die korrekten Argu-
mente.
42.1
Relevante Argumente
-c,--config <Path> Der Pfad zur .ini Datei. Muss gesetzt werden.
-csv,--csv <Path> Wenn angegeben, wird am Ende das Ergebnis in Form
von CSV dargestellt. Bei Angabe eines Pfads, wird versucht das Re-
sultat an diesen Ort zu schreiben. Ohne Angabe der Datei, wird das
Ergebnis auf dem Terminal ausgegeben.
-e,--evaluate Das Argument, um die Evaluation zu starten. Ohne dieses
wird die GUI ausgeführt.
-onlyBest,--onlyBest Wenn gesetzt, wird pro Student nur das Ergebnis
mit der höchsten Punktzahl vermerkt. Es macht nur Sinn bei Angabe
mehrerer Solution-Files.
-v,--verbose Verbose-Mode, zusätzliche Information wird ausgegeben.
2.2
Beispiel-Aufruf
Der folgende Aufruf startet den Evaluationprozess mit der Config am Ort
/simple/config.ini im csv-Mode.
1
java -jar SQLChecker-1.0.2.jar -c ~/simple/config.ini -csv -e
Die nachfolgende Ausgabe wird erzeugt:
1
2
3
4
5
Submission loaded:
, →
/home/xyntek/simple/submission/sample_with_author.sql
Loading class ` com.mysql.jdbc.Driver'. This is deprecated. The new
, →
driver class is `com.mysql.cj.jdbc.Driver' . The driver is
, →
automatically registered via the SPI and manual loading of the
, →
driver class is generally unnecessary.
SUCCESS Path:/home/xyntek/simple/submission/sample_with_author.sql
, →
Authors:[Max Mustermann max_mustermann@gmail.de 5727685, Nadine
Mustermann nadine_mustermann@gmail.de 1234567]
, →
Solution:sample_with_author Evaluation:[1a:pass, 1b:pass,
, →
, →
1c:pass, 1d:pass, 2a:pass, 2b:pass]
SUCCESS Path:/home/xyntek/simple/submission/sample_with_author.sql
, →
Authors:[Max Mustermann max_mustermann@gmail.de 5727685, Nadine
Mustermann nadine_mustermann@gmail.de 1234567]
, →
Solution:sample2_with_author Evaluation:[1a:pass, 1b:pass,
, →
, →
1c:pass, 1d:pass, 2a:pass, 2b:fail]
"Path" , "Authors" , "Solution" , "1a" , "1b" , "1c" , "1d" , "2a" , "2b" ,
, →
"#Success" , "ErrorMsg"
5
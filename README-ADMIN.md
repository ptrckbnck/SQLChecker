#ADMIN DOC
Diese Anleitung richtet sich an diejenigen, die Übungen erstellen und auswerten möchten. Studenten die Abgaben erstellen möchte, sollten sich diese [Anleitung](README.md) durchlesen.

##SQLChecker in Console

##Evaluation GUI

##Übungen definieren
Aus Sicht eines Übungsgruppenleiters sind vier Dateien erforderlich, um eine
Aufgabe erfolgreich durchzuführen: Eine Template-Datei(.sqlt), ein Reset-
skript(.sql), eine Lösungs-Datei (.sql) und eine Evaluation-Konfigurationsdatei
(.ini).

###Die Tag-Annotation
Die Template-Datei, die Studentenabgabe und die Lösungsdatei haben ei-
ne spezielle Form. Sie besitzen Tag-Elemente, welche folgende Form haben:

`/*%%<name>%%<type>%%<extra>%%*/` 

Diese bestehen aus einem SQL Kommentar und stören daher nicht bei der Ausführung der Datei als SQL-Skript.
Es gibt mehrere Regeln die zu beachten sind.

- Der Tag-Name und der Tag-Type dürfen keine Whitespaces besitzen.
- Zur Zeit sind drei Typen möglich:`head`,`task`,`static`. Ein leeres Feld steht für task.
- Tag-Extra kann alles enthalten außer `%%`, `/*` und `*/`.
- Name und Extra sind optional, abhängig vom Type.

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
  Task und Static Tags können beliebige, nicht-leere Namen haben, müssen aber in der Datei einzigartig sein. Extra ist nur in der Definition der Solution interessant. Extra besteht aus vier Felder die jeweils durch einen Punkt getrennt werden. Punkte können weggelassen werden, wenn alle folgenden Felder leer sind.
- `score` ist das erste Feld. Hier wird eine positive Ganzzahl erwartet. Diese gibt an, wie viele Punkte für die Lösung der Aufgabe bzw. Gruppe angerechnet werden. Wird einer Gruppe durch verschiedenen Tags unterschiedliche Punkte zugewiesen, wird der erste gefundene Wert genommen. TODO check wie genau dies funktioniert. Wird das Feld leer gelassen, wird der Aufgabe ein Punkte zugewiesen. Eine Gruppe erhält nur Punkte, wenn alle Aufgaben der Gruppe gelöst wurden.
- Das zweite Feld `group` bezeichnet eine Gruppe von Aufgaben, die zusammengefasst werden. Um zu der gleichen Gruppe zu gehören, müssen alle müssen die Tags die gleichen Gruppennamen definieren.
- Das `schema` definiert das erwartete Schema der definierten SQL-Abfrage als Liste von Werten. Dieses hat folgende Form `["<Wert1>","<Wert1>", ... , "<WertN>"]`, angelehnt an die Json-Syntax. Die Aufgabe gilt nur als bestanden, wenn das Schema des Ergebnis der SQL-Anfrage identisch mit dem hier angegeben Schema ist.
- `òrder` legt fest, ob bei der Beurteilung des Ergebnis eine Ordnung zu beachten ist. Per Default wird das Ergebnis als ungeordnet betrachtet. Die Definition der Ordnung hat die selbe Form wie das Schema. Als Werte sind die Indizies der Spalten des Schemas erwartet. Kein Index sollte mehr als einmal vorkommen, kann aber weggelassen werden. Die Indizies sind in absteigender Relevanz aufgeführt.


      protected final String group;
      protected final List<String> schema;
      protected final List<Integer> order;
  
  
Das Template
Das Template ist eine Tag-annotierte Datei. Die Datei sollte einen submission_name
definieren und keine static-Tags verwenden.
Eine Template-Datei wird von Studenten zum Initialisieren einer neuen
Übung benötigt. Die Datei definiert, welche Aufgaben im SQLChecker zum
Bearbeiten angezeigt werden und welcher Text als Code dieser Aufgaben
beim Initialisieren der Aufgabe angezeigt werden soll.
Alle Tags, die keine Schlüsselwörter beinhalten, werden als Aufgaben
interpretiert. Kein Name von Aufgaben darf doppelt verwendet werden. Der
Rumpf eines Aufgaben-Tags ist der Code, der beim Initialisieren der Aufgabe
angezeigt wird. Da der SQL-Checker die Aufgaben sequentiell abarbeitet, ist
Reihenfolge der Aufgaben relevant. Ein Beispiel-Template Datei ist 1.
1.3
Das Resetskript
Das Reset-Skript besteht aus einer Reihe vom SQL-Statements. Der SQL-
Checker führt dieses Skript vor jedem Test aus. Studenten können das Reset-
skript in der SQLChecker-GUI ausführen. Es sollte darauf geachtet werden,
2/*submission_name*/
Blatt1
/*1a*/
/* Kommentar zu Aufgabe 1a
ueber mehrere Zeilen */
CREATE...
/*1b*/
-- Kommentar zu Aufgabe 1b
INSERT ...
Listing 1: Template-Beispiel
dass kein Statement Fehler verursacht. Zum Beispiel sollte DROP DATABASE
IF EXISTS <name> ;, statt DROP DATABASE <name> ; genutzt werden, da-
mit kein Fehler auftritt, wenn noch keine entsprechende Datenbank exis-
tiert. Damit der SQL-Checker das Skript bei der Initialisierung der Übung
automatisch findet, muss der Name der Datei folgendermaßen aussehen:
<submission_name> _reset.sql. submission_name wird in der Template-
Datei definiert.
1.4
Die Lösung
Die Lösung ist eine weitere Tag-annotierte Datei. Die Lösung muss den selben
submission_name und die gleichen Aufgaben-Tags in gleicher Reihenfolge
definieren wie das dazugehörige Template. Der SQLChecker bewertet auf
der Basis dieser Datei die Studentenabgaben. Der Code im Rumpf eines
Aufgaben-Tags sollte eine gültige Lösung der entsprechenden Aufgabe sein.
Das Programm überprüft, ob die von Studenten erzeugten Statements die
gleichen Resultate erzielen, wie diese Statements der Lösung. Zusätzlich
kann diese Datei static-Tags definieren. Das SQLStatement im Rumpf
dieser Tags wird sowohl bei der Lösung als auch der Abgabe ausgeführt und
die Resultate verglichen. Ist das Ergebnis in beiden Fällen gleich, gilt der
Test dieses Tags als bestanden.
1.5
Die Konfigurationsdatei
Die Konfigurationsdatei ist eine INI-Datei, welche die nötigen Informationen
für die Auswertung der Abgaben enthält. Diese Datei wird nur für die Evalua-
tion und nicht die Erstellung der Abgabe benötigt. Die Schlüssel database,
username, password, hostname und port werden für die Datenbankverbin-
3/*submission_name*/
Blatt1
/*1a*/
CREATE TABLE tische (name varchar(40), beine int(6));
/*1b*/
INSERT INTO tische VALUES('KNARREVIK', 4);
/*static*/
SELECT COUNT(*) FROM tische;
Listing 2: Lösung-Beispiel
1
2
3
4
5
6
7
#settings for database connection
[db]
\database = airport
username = airportuser
password = airportuser
hostname = localhost
port = 3306
8
9
10
11
12
13
#pathes to files
[files]
resetPath = ~/Dropbox/SQLChecker/aufgaben/Blatt1/Blatt1_reset.sql
solutionPaths =
, →
~/Dropbox/SQLChecker/aufgaben/Blatt1/Blatt1_solution.sql,
, →
~/Dropbox/SQLChecker/aufgaben/Blatt1/Blatt1_solution2.sql
submissionPath = ~/Dropbox/SQLChecker/aufgaben/Blatt1/submissions/
Listing 3: Konfigurations-Beispiel
dung benötigt. resetPath gibt die Pfad zum Resetskript an. solutionPaths
gibt den Pfad zu einer oder mehreren Lösungs-Dateien an. Mehrere Lösungen
werden mit Komma separiert. submissionPath gibt den Pfad zu Abgaben
an. Der Pfad wird bis zu einer Tiefe von zwei nach Abgaben durchsucht. 3
ist ein Beispiel einer Konfigurationsdatei.
2
Die Auswertung
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
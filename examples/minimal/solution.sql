/*%%%%head%%
{
"type": "solution",
"name": "test_uebung"
}
%%*/

/*%%1%%task%%1.Aufgabe1%%*/
/* Fügen Sie ein Zeile der Tabelle plane hinzu. */
insert into plane
values (5, 2, 'Cessna', 'Cessna 162 Skycatcher');


/*%%1static%%static%%.Aufgabe1%%*/
/* Testet ob die Anzahl der Flugzeug der Erwartung entspricht*/
SELECT count(*)
FROM plane;

/*%%2%%task%%2.Aufgabe2%%*/
/* Erzeugen Sie eine Tabelle crewhotel mit den Feldern: ID, Name und AddressID.
Beide ID sind Zahlen, während Name ein String ist */
CREATE TABLE `crewhotel`
(
    `ID`        int(11)     NOT NULL,
    `Name`      varchar(45) NOT NULL,
    `AddressID` int(11)     NOT NULL,
    PRIMARY KEY (ID)
);

/*%%2static%%static%%2.Aufgabe2%%*/
/* Testet Erzeugung der Tabelle indem ein Dummy hinzugefügt wird. */
insert into crewhotel (ID, Name, AddressID)
values (100, 'Dummy', 1234567);



/*%%3%%task%%2.Aufgabe3%%*/
/* Fügen Sie drei Zeilen der Tabelle crewhotel zu */

insert into crewhotel
values (0, 'Hilton', 1234),
       (1, 'Steigenberger', 0001),
       (2, 'Bobs Burger', 1010101);

/*%%3static1%%static%%.Aufgabe3%%*/
/* Testet ob die tatsächliche Anzahl an Zeilen der erwarteten entspricht.*/
SELECT count(*)
FROM crewhotel;


/*%%4%%task%%1.Aufgabe4.["FlightNo", "DepartureDateAndTimeUTC"]%%*/
/* Zeige FlightNo und FlightDurationInMinutes aller Flüge mit PlaneID=2*/
SELECT FlightNo,
       DepartureDateAndTimeUTC
FROM flightexecution
WHERE PlaneID = 2;

/*%%5%%task%%2.Aufgabe5.["FlightNo", "DepartureDateAndTimeUTC"].[0]%%*/
/* Zeige FlightNo und PlaneID aller Flüge die kürzer als 130 Minuten sind
 geordnet nach FlightNo */
SELECT f.FlightNo,
       f.PlaneID
FROM flightexecution f
WHERE FlightDurationInMinutes < 130
ORDER BY FlightNo;
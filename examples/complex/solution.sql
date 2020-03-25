/*%%%%head%%
{
"type": "solution",
"name": "name_solution"
}
%%*/

/*%%1a%%task%%1.G1a%%*/
/* Kommentar zu Aufgabe 1a
ueber mehrere Zeilen */
insert into plane
values (5, 2, 'Cessna', 'Cessna 162 Skycatcher');
/*%%1as%%static%%.TROLL%%*/
SELECT count(*)
FROM airport.plane
WHERE PlaneID = 1;

/*%%1b%%task%%2.G1b%%*/
/* Kommentar zu Aufgabe 1b */
CREATE TABLE `crewhotel`
(
    `ID`        int(11)     NOT NULL,
    `Name`      varchar(45) NOT NULL,
    `AddressID` int(11)     NOT NULL,
    PRIMARY KEY (ID)
);
/*%%1bs%%static%%2.G1b%%*/
SELECT COUNT(*)
FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = 'airport'
  and table_name = 'crewhotel';

/*%%1c%%task%%3.G1c%%*/
/* Kommentar */
insert into crewhotel
values (0, 'Hilton', 1234),
       (1, 'Steigenberger', 0001),
       (2, 'Bobs Burger', 1010101);
/*%%1c1%%static%%.G1c%%*/
SELECT count(*)
FROM airport.crewhotel;
/*%%1c2%%static%%.G1c%%*/
insert into crewhotel (ID, Name, AddressID)
values (12, 'Hilton', 1234);

/*%%2a%%task%%4.G2a%%*/
/* Kommentar */
SELECT FlightNo, DepartureDateAndTimeUTC, ICAO_Code_Origin, ICAO_Code_Destination, PlaneID, FlightDurationInMinutes
FROM flightexecution
WHERE PlaneID = 2;

/*%%2b%%task%%5.G2b%%*/
/* Kommentar */
SELECT FlightNo
FROM flightexecution
WHERE PlaneID = 2
  AND FlightDurationInMinutes < 130
ORDER BY FlightDurationInMinutes;

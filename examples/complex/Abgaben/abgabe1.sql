/*%%%%head%%
{
"name": "test",
"type": "submission",
"authors": [["Yusuf Baran","yusufbaran66@yahoo.de","6364384"],["Angelos Ioannou","angelos.ioannou@gmx.de","6081379"]]
}
%%*/

/*%%1a%%*/
/* Kommentar zu Aufgabe 1a
ueber mehrere Zeilen */
insert into plane
values (5, 2, 'Cessna', 'Cessna 162 Skycatcher');

/*%%1b%%*/
/* Kommentar zu Aufgabe 1b */
CREATE TABLE `crewhotel`
(
    `ID`        int(11)     NOT NULL,
    `Name`      varchar(45) NOT NULL,
    `AddressID` int(11)     NOT NULL,
    PRIMARY KEY (ID)
);

/*%%1c%%*/
/* Kommentar */
insert into crewhotel
values (0, 'Hilton', 1234),
       (1, 'Steigenberger', 0001),
       (2, 'Bobs Burger', 1010101);


/*%%2a%%*/
/* Kommentar */
SELECT FlightNo, DepartureDateAndTimeUTC, ICAO_Code_Origin, ICAO_Code_Destination, PlaneID, FlightDurationInMinutes
FROM flightexecution
WHERE PlaneID = 2;

/*%%2b%%*/
/* Kommentar */
SELECT FlightNo
FROM flightexecution
WHERE PlaneID = 2
  AND FlightDurationInMinutes < 130
ORDER BY FlightDurationInMinutes;

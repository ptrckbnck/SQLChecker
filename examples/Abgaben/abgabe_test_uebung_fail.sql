/*%%%%head%%
{
"name": "test_uebung",
"type": "submission",
"authors": [["author3","yxc@yahoo.de","636334"],["author4","sdgs@gmx.de","34346"]]
}%%*/

/*%%1%%*/
# ungültige id
insert into plane
values ('five', 2, 'Cessna', 'Cessna 162 Skycatcher');

/*%%2%%*/
# name ist int
CREATE TABLE `crewhotel`
(
    `ID`        int(11) NOT NULL,
    `Name`      int(11) NOT NULL,
    `AddressID` int(11) NOT NULL,
    PRIMARY KEY (ID)
);

/*%%3%%*/
# falscher table
insert into crewhotello
values (0, 'Hilton', 1234),
       (1, 'Steigenberger', 0001),
       (2, 'Bobs Burger', 1010101);

/*%%4%%task%%*/
# falsche attribute
SELECT *
FROM flightexecution
WHERE PlaneID = 2;

/*%%5%%task%%*/
# falsche Ordnung
SELECT f.FlightNo,
       f.FlightDurationInMinutes
FROM flightexecution f
WHERE FlightDurationInMinutes < 130
ORDER BY PlaneID;
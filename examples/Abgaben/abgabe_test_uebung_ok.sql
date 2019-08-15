/*%%%%head%%
{
"name": "test_uebung",
"type": "submission",
"authors": [["author1","dfg@yahoo.de","6363484"],["author2","dfg@gmx.de","6083479"]]
}%%*/

/*%%1%%*/
insert into plane
values (5, 2, 'Cessna', 'Cessna 162 Skycatcher');

/*%%2%%*/
CREATE TABLE `crewhotel`
(
    `ID`        int(11)     NOT NULL,
    `Name`      varchar(45) NOT NULL,
    `AddressID` int(11)     NOT NULL,
    PRIMARY KEY (ID)
);

/*%%3%%*/
insert into crewhotel
values (0, 'Hilton', 1234),
       (1, 'Steigenberger', 0001),
       (2, 'Bobs Burger', 1010101);

/*%%4%%task%%*/
SELECT FlightNo,
       DepartureDateAndTimeUTC
FROM flightexecution
WHERE PlaneID = 2;

/*%%5%%task%%*/
SELECT f.FlightNo,
       f.FlightDurationInMinutes
FROM flightexecution f
WHERE FlightDurationInMinutes < 130
ORDER BY FlightNo;
DROP DATABASE IF EXISTS `airport`;

CREATE DATABASE `airport` DEFAULT CHARSET = utf8mb4;

use `airport`;


CREATE TABLE `airport`.`country`
(
    `CountryISO3166_2LetterCode` char(2)      NOT NULL,
    `CountryName`                varchar(100) NOT NULL,
    PRIMARY KEY (`CountryISO3166_2LetterCode`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO `country`
VALUES ('CA', 'Canada'),
       ('DE', 'Germany'),
       ('FR', 'France'),
       ('GB', 'United Kingdom'),
       ('GR', 'Greece'),
       ('IT', 'Italy'),
       ('NO', 'Norway');



CREATE TABLE `airport`.`address`
(
    `AddressID`                  int(11)      NOT NULL,
    `ZIPCode`                    varchar(10)  NOT NULL,
    `Town`                       varchar(100) NOT NULL,
    `Street`                     varchar(100) NOT NULL,
    `CountryISO3166_2LetterCode` char(2)      NOT NULL,
    PRIMARY KEY (`AddressID`),
    KEY `CountryISO3166_2LetterCode` (`CountryISO3166_2LetterCode`),
    CONSTRAINT `address_ibfk_1` FOREIGN KEY (`CountryISO3166_2LetterCode`) REFERENCES `country` (`CountryISO3166_2LetterCode`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Dumping data for table `address`
--
SET foreign_key_checks = 0;
INSERT INTO `address`
VALUES (1, '13405', 'Berlin', 'Flughafen Tegel', 'DE'),
       (2, '40474', 'Düsseldorf', 'Flughafenstraße 120', 'DE'),
       (3, '60547', 'Frankfurt am Main', 'Frankfurt Flughafen', 'DE'),
       (4, '85356', 'München', 'Nordallee 25', 'DE'),
       (5, '95700', 'Paris', 'Paris Charles de Gaulle Airport', 'FR'),
       (6, '19-004', 'Attiki Odos', 'Spata Artemida', 'GR'),
       (7, 'TW6', 'London', 'Greater London TW6', 'GB'),
       (8, '40', 'Ciampino Roma', 'Via Appia Nuova 1651', 'IT'),
       (9, '2061', 'Gardermoen', 'Edvard Munchs veg', 'NO'),
       (10, '6301', 'Mississauga', 'Silver Dart Dr', 'CA'),
       (11, '60512', 'Frankfurt am Main', 'Hauptstraße 1', 'DE'),
       (12, '60157', 'Frankfurt am Main', 'Fischerstraße 42', 'DE'),
       (13, '60134', 'Frankfurt am Main', 'Bankweg 135', 'DE'),
       (14, '65495', 'Frankfurt am Main', 'Windgasse 9', 'DE'),
       (15, '64358', 'Frankfurt am Main', 'Achenbachstraße 5', 'DE'),
       (16, '65260', 'Frankfurt am Main', 'Adalbertstraße 3', 'DE'),
       (17, '60154', 'Frankfurt am Main', 'Adam-Opel-Straße 4', 'DE'),
       (18, '10115', 'Berlin', 'Zimmerstraße 4', 'DE'),
       (19, '10179', 'Berlin', 'Rudi-Dutschke-Straße 134', 'DE'),
       (20, '10243', 'Berlin', 'Oranienstraße 9', 'DE'),
       (21, '0815', 'Nowhere', 'Somewhere', 'te');
SET foreign_key_checks = 1;


CREATE TABLE `airport`.`airport`
(
    `ICAO_Code`   varchar(4)   NOT NULL,
    `AirportName` varchar(100) NOT NULL,
    `AddressID`   int(11)      NOT NULL,
    PRIMARY KEY (`ICAO_Code`),
    KEY `AddressID` (`AddressID`),
    CONSTRAINT `airport_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Dumping data for table `airport`
--

INSERT INTO `airport`
VALUES ('CYYZ', 'Toronto Pearson International Airport', 10),
       ('EDDF', 'Frankfurt am Main', 3),
       ('EDDL', 'Düsseldorf International', 2),
       ('EDDM', 'München (Franz Josef Strauß)', 4),
       ('EDDT', 'Berlin-Tegel (Otto-Lilienthal)', 1),
       ('EGLC', 'London City Airport', 7),
       ('ENGM', 'Flughafen Oslo-Gardermoen', 9),
       ('LFPG', 'Paris-Charles de Gaulle', 5),
       ('LGAV', 'Athens International Airport', 6),
       ('LIRA', 'Flughafen Rom-Ciampino', 8);


--
-- Table structure for table `customer`
--

CREATE TABLE `airport`.`customer`
(
    `ID`        int(11)     NOT NULL,
    `LastName`  varchar(50) NOT NULL,
    `FirstName` varchar(50) NOT NULL,
    `AddressID` int(11)     NOT NULL,
    PRIMARY KEY (`ID`),
    KEY `AddressID` (`AddressID`),
    CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer`
VALUES (15943155, 'Müller', 'Elias', 13),
       (15946782, 'Richter', 'Thomas', 20),
       (16579435, 'Schmidt', 'Alexander', 11),
       (28943155, 'Weber', 'Sabrina', 16),
       (31764155, 'Koch', 'Nicole', 17),
       (35941358, 'Weber', 'Sabine', 18),
       (35943143, 'Schneider', 'Daniel', 14),
       (35943147, 'Schmidt', 'Stefanie', 12),
       (73941738, 'Fischer', 'David', 15),
       (99188655, 'Neumann', 'Emil', 19);

--
-- Table structure for table `employeetype`
--

DROP TABLE IF EXISTS `employeetype`;
CREATE TABLE `employeetype`
(
    `EmployeeTypeID` int(11) NOT NULL,
    `Name`           varchar(45) DEFAULT NULL,
    PRIMARY KEY (`EmployeeTypeID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `employeetype`
--

INSERT INTO `employeetype`
VALUES (1, 'Pilotin'),
       (2, 'Co-Pilotin'),
       (3, 'Purserin'),
       (4, 'Flugbegleiterin');


--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee`
(
    `EmployeeID`     int(11) NOT NULL,
    `Name`           varchar(45) DEFAULT NULL,
    `EmployeeTypeID` int(11)     DEFAULT NULL,
    PRIMARY KEY (`EmployeeID`),
    KEY `fk_employee_type_idx` (`EmployeeTypeID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `employee`
--

INSERT INTO `employee`
VALUES (2, 'Agnieszka', 2),
       (3, 'Alexandra', 3),
       (4, 'Alina', 4),
       (6, 'Amelie', 2),
       (7, 'Andrea', 3),
       (8, 'Anett', 4),
       (9, 'Anette', 1),
       (10, 'Angela', 2),
       (11, 'Angelica', 3),
       (12, 'Angelika', 4),
       (14, 'Anika', 2),
       (15, 'Anita', 3),
       (16, 'Anja', 4),
       (17, 'Anke', 1),
       (18, 'Ann', 2),
       (19, 'Anna', 3),
       (20, 'Anne', 4),
       (21, 'Anneliese', 1),
       (22, 'Annemarie', 2),
       (23, 'Annett', 3),
       (24, 'Annette', 4),
       (25, 'Anni', 1),
       (26, 'Annica', 2),
       (27, 'Agnes', 3),
       (28, 'Agnieszka', 4),
       (30, 'Alina', 2),
       (31, 'Alma', 3),
       (32, 'Amelie', 4),
       (34, 'Anett', 2),
       (35, 'Anette', 3),
       (36, 'Angela', 4),
       (37, 'Angelica', 1),
       (38, 'Angelika', 2),
       (39, 'Anica', 3),
       (40, 'Anika', 4),
       (41, 'Anita', 1),
       (42, NULL, 2),
       (43, 'Anke', 3),
       (44, 'Ann', 4),
       (45, 'Anna', 1),
       (46, 'Anne', 2),
       (47, 'Anneliese', 3),
       (48, 'Annemarie', 4),
       (49, 'Annett', 1),
       (50, 'Annette', 2),
       (51, 'Anni', 3),
       (52, 'Annica', 4);


--
-- Table structure for table `plane`
--

DROP TABLE IF EXISTS `plane`;
CREATE TABLE `plane`
(
    `PlaneID`   int(11)      NOT NULL,
    `NoOfSeats` int(11)      NOT NULL,
    `PlaneType` varchar(100) NOT NULL,
    `PlaneName` varchar(100) NOT NULL,
    `VIN`       bigint(18),
    `Checksum`  int(2),
    PRIMARY KEY (`PlaneID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `plane`
--


INSERT INTO `plane`
VALUES (1, 15, 'Airbus', 'Airbus A380', 123456789123456789, 7),
       (2, 5, 'Fokker 1', 'Fokker F100', 999999999999999999, 88),
       (3, 15, 'Boeing', 'Boeing 737', 987654321123456789, 89),
       (4, 10, 'Airbus', 'Airbus A340', 12, 1),
       (5, 5, 'Fokker 2', 'Fokker F100', null, null),
       (6, 5, 'Fokker 3', 'Fokker F100', null, null),
       (7, 5, 'Fokker 4', 'Fokker F100', null, null),
       (8, 5, 'Fokker 5', 'Fokker F100', null, null),
       (9, 5, 'Fokker 6', 'Fokker F100', null, null),
       (10, 5, 'Fokker 7', 'Fokker F100', null, null);


--
-- Table structure for table `flightexecution`
--

DROP TABLE IF EXISTS `flightexecution`;
CREATE TABLE `flightexecution`
(
    `FlightNo`                varchar(7) NOT NULL,
    `DepartureDateAndTimeUTC` timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `ICAO_Code_Origin`        varchar(4) NOT NULL,
    `ICAO_Code_Destination`   varchar(4) NOT NULL,
    `PlaneID`                 int(11)    NOT NULL,
    `FlightDurationInMinutes` int(11)    NOT NULL,
    PRIMARY KEY (`FlightNo`, `DepartureDateAndTimeUTC`),
    KEY `PlaneID` (`PlaneID`),
    KEY `ICAO_Code_Origin` (`ICAO_Code_Origin`),
    KEY `ICAO_Code_Destination` (`ICAO_Code_Destination`),
    CONSTRAINT `flightexecution_ibfk_1` FOREIGN KEY (`PlaneID`) REFERENCES `plane` (`PlaneID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `flightexecution_ibfk_2` FOREIGN KEY (`ICAO_Code_Origin`) REFERENCES `airport` (`ICAO_Code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `flightexecution_ibfk_3` FOREIGN KEY (`ICAO_Code_Destination`) REFERENCES `airport` (`ICAO_Code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `flightexecution`
--

INSERT INTO `flightexecution`
VALUES ('IBE1684', '2018-12-31 09:30:45', 'LIRA', 'ENGM', 2, 200),
       ('IBE1764', '2018-12-30 09:30:45', 'EGLC', 'EDDT', 1, 180),
       ('IBE1846', '2018-12-31 09:30:45', 'EDDF', 'EDDL', 3, 160),
       ('IBE3843', '2018-12-31 09:30:45', 'ENGM', 'EDDM', 2, 120),
       ('IBE4681', '2018-12-31 09:30:45', 'EDDF', 'LIRA', 4, 180),
       ('LH1167', '2018-12-31 09:30:45', 'LFPG', 'LGAV', 2, 180),
       ('LH1354', '2018-12-31 10:30:45', 'EDDT', 'LIRA', 1, 120),
       ('LH1761', '2018-12-31 09:30:45', 'EDDF', 'EDDT', 4, 240),
       ('LH1769', '2018-12-31 09:30:45', 'LIRA', 'EDDF', 1, 180),
       ('LH1943', '2018-12-31 09:30:45', 'EDDL', 'EGLC', 2, 180),
       ('LH1973', '2018-12-31 09:30:45', 'EDDF', 'LFPG', 3, 180),
       ('LH2301', '2018-07-26 09:50:00', 'EDDF', 'CYYZ', 2, 340),
       ('LH3333', '2018-06-05 08:30:45', 'EDDF', 'EDDM', 1, 155),
       ('LH3584', '2018-12-31 09:30:45', 'EDDM', 'EDDF', 1, 180),
       ('LH3842', '2018-12-31 09:30:45', 'EGLC', 'EDDL', 4, 120),
       ('LH5301', '2018-07-02 15:20:00', 'EDDF', 'CYYZ', 1, 740),

       ('IBE1684', '2019-02-20 05:30:45', 'LIRA', 'ENGM', 2, 200),
       ('IBE1764', '2019-02-20 06:30:45', 'EGLC', 'EDDT', 1, 180),
       ('IBE1846', '2019-02-20 07:30:45', 'EDDF', 'EDDL', 3, 160),
       ('IBE3843', '2019-02-20 08:00:45', 'ENGM', 'EDDM', 5, 120),
       ('IBE4681', '2019-02-20 09:30:45', 'EDDF', 'LIRA', 4, 180),
       ('LH1167', '2019-02-20 10:30:45', 'EDDM', 'LGAV', 5, 180),
       ('LH1354', '2019-02-20 11:30:45', 'EDDT', 'LIRA', 6, 120),
       ('LH1761', '2019-02-20 12:30:45', 'EDDF', 'EDDT', 7, 240),
       ('LH1769', '2019-02-20 13:30:45', 'LIRA', 'EDDF', 8, 180),
       ('LH1943', '2019-02-20 14:30:45', 'EDDT', 'EGLC', 9, 180),
       ('LH1973', '2019-02-20 15:30:45', 'EDDF', 'LFPG', 10, 180),
       ('LH2301', '2019-02-20 16:50:00', 'EDDT', 'CYYZ', 1, 340),
       ('LH3333', '2019-02-20 17:30:45', 'ENGM', 'EDDM', 2, 155),
       ('LH3584', '2019-02-20 18:30:45', 'EDDL', 'EDDF', 3, 180),
       ('LH3842', '2019-02-20 19:30:45', 'LIRA', 'EDDL', 4, 120),
       ('LH5301', '2019-02-20 20:20:00', 'LGAV', 'CYYZ', 5, 740),
       ('LH4761', '2019-02-21 07:30:45', 'LFPG', 'LIRA', 7, 240),

       ('LH7660', '2018-12-31 09:30:45', 'LGAV', 'EDDM', 3, 240);

--
-- Table structure for table `crew`
--

DROP TABLE IF EXISTS `crew`;
CREATE TABLE `crew`
(
    `EmployeeID`              int(11)    NOT NULL,
    `FlightNo`                varchar(7) NOT NULL,
    `DepartureDateAndTimeUTC` timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`EmployeeID`, `FlightNo`, `DepartureDateAndTimeUTC`),
    KEY `fk_crew_flight_idx` (`FlightNo`, `DepartureDateAndTimeUTC`),
    CONSTRAINT `fk_crew_employee` FOREIGN KEY (`EmployeeID`) REFERENCES `employee` (`EmployeeID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_crew_flight` FOREIGN KEY (`FlightNo`, `DepartureDateAndTimeUTC`) REFERENCES `flightexecution` (`FlightNo`, `DepartureDateAndTimeUTC`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `crew`
--
INSERT INTO `crew`
VALUES (19, 'LH1769', '2018-12-31 09:30:45'),
       (19, 'LH3333', '2018-06-05 08:30:45'),
       (20, 'LH3333', '2018-06-05 08:30:45'),
       (21, 'IBE1764', '2018-12-30 09:30:45'),
       (21, 'LH3333', '2018-06-05 08:30:45'),
       (22, 'IBE1764', '2018-12-30 09:30:45'),
       (22, 'LH3584', '2018-12-31 09:30:45'),
       (23, 'LH1354', '2018-12-31 10:30:45'),
       (23, 'LH3584', '2018-12-31 09:30:45'),
       (23, 'LH5301', '2018-07-02 15:20:00'),
       (42, 'LH1354', '2018-12-31 10:30:45'),
       (42, 'LH1769', '2018-12-31 09:30:45'),
       (42, 'LH5301', '2018-07-02 15:20:00'),
       (43, 'LH1769', '2018-12-31 09:30:45'),
       (44, 'LH3333', '2018-06-05 08:30:45'),
       (45, 'IBE1764', '2018-12-30 09:30:45'),
       (45, 'LH3333', '2018-06-05 08:30:45'),
       (46, 'IBE1764', '2018-12-30 09:30:45'),
       (46, 'LH3584', '2018-12-31 09:30:45'),
       (47, 'LH1354', '2018-12-31 10:30:45'),
       (47, 'LH3584', '2018-12-31 09:30:45');



--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;

CREATE TABLE `reservation`
(
    `ReservationID`           int(11)    NOT NULL AUTO_INCREMENT,
    `CustomerID`              int(11)    NOT NULL,
    `NoReservedSeats`         int(11)    NOT NULL,
    `Comment`                 varchar(1000)       DEFAULT NULL,
    `FlightNo`                varchar(7) NOT NULL,
    `DepartureDateAndTimeUTC` timestamp  NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`ReservationID`),
    KEY `CustomerID` (`CustomerID`),
    KEY `FlightNo` (`FlightNo`, `DepartureDateAndTimeUTC`),
    CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`FlightNo`, `DepartureDateAndTimeUTC`) REFERENCES `flightexecution` (`FlightNo`, `DepartureDateAndTimeUTC`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 73941739
  DEFAULT CHARSET = utf8mb4;


--
-- Dumping data for table `reservation`
--


INSERT INTO `reservation`
VALUES (1, 16579435, 1, '', 'LH1973', '2018-12-31 09:30:45'),
       (2, 35943147, 1, '', 'LH3584', '2018-12-31 09:30:45'),
       (3, 15943155, 2, 'Die Plätze sollten nebeneinander liegen', 'LH1167', '2018-12-31 09:30:45'),
       (4, 35943143, 1, '', 'LH7660', '2018-12-31 09:30:45'),
       (5, 73941738, 1, '', 'LH3842', '2018-12-31 09:30:45'),
       (6, 31764155, 3, NULL, 'IBE1846', '2018-12-31 09:30:45'),
       (7, 73941738, 2, NULL, 'IBE1846', '2018-12-31 09:30:45'),

       (8, 35943143, 5, '', 'IBE1764', '2019-02-20 06:30:45'),
       (9, 73941738, 5, '', 'IBE1846', '2019-02-20 07:30:45'),
       (10, 31764155, 5, NULL, 'IBE1764', '2019-02-20 06:30:45'),
       (11, 73941738, 6, NULL, 'IBE1846', '2019-02-20 07:30:45'),
       (12, 35943143, 5, '', 'LH1769', '2019-02-20 13:30:45'),
       (13, 73941738, 3, '', 'LH1769', '2019-02-20 13:30:45'),
       (14, 31764155, 5, NULL, 'LH1769', '2019-02-20 13:30:45'),
       (15, 73941738, 6, NULL, 'LH5301', '2019-02-20 20:20:00')
;

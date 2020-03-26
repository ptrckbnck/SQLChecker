DROP DATABASE IF EXISTS `airport`;

CREATE DATABASE `airport`;

use `airport`;

CREATE TABLE `airport`.`country`
(
    `CountryISO3166_2LetterCode` char(2)      NOT NULL,
    `CountryName`                varchar(100) NOT NULL,
    PRIMARY KEY (`CountryISO3166_2LetterCode`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


INSERT INTO `airport`.`country`
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
  DEFAULT CHARSET = utf8;


INSERT INTO `airport`.`address`
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
       (20, '10243', 'Berlin', 'Oranienstraße 9', 'DE');

CREATE TABLE `airport`.`airport`
(
    `ICAO_Code`   varchar(4)   NOT NULL,
    `AirportName` varchar(100) NOT NULL,
    `AddressID`   int(11)      NOT NULL,
    PRIMARY KEY (`ICAO_Code`),
    KEY `AddressID` (`AddressID`),
    CONSTRAINT `airport_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `address` (`AddressID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


INSERT INTO `airport`.`airport`
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
  DEFAULT CHARSET = utf8;


INSERT INTO `airport`.`customer`
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


CREATE TABLE `airport`.`plane`
(
    `PlaneID`   int(11)      NOT NULL,
    `NoOfSeats` int(11)      NOT NULL,
    `PlaneType` varchar(100) NOT NULL,
    `PlaneName` varchar(100) NOT NULL,
    PRIMARY KEY (`PlaneID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `airport`.`plane`
VALUES (1, 555, 'Airbus', 'Airbus A380'),
       (2, 85, 'Fokker', 'Fokker F100'),
       (3, 162, 'Boeing', 'Boeing 737'),
       (4, 380, 'Airbus', 'Airbus A340');


CREATE TABLE `airport`.`flightexecution`
(
    `FlightNo`                varchar(7) NOT NULL,
    `DepartureDateAndTimeUTC` timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  DEFAULT CHARSET = utf8;

INSERT INTO `flightexecution`
VALUES ('IBE1684', '2018-12-31 10:30:45', 'LIRA', 'ENGM', 2, 200),
       ('IBE1764', '2018-12-31 10:30:45', 'EGLC', 'EDDT', 1, 180),
       ('IBE1846', '2018-12-31 10:30:45', 'EDDF', 'EDDL', 3, 160),
       ('IBE3843', '2018-12-31 10:30:45', 'ENGM', 'EDDM', 2, 120),
       ('IBE4681', '2018-12-31 10:30:45', 'EDDF', 'LIRA', 4, 180),
       ('LH1167', '2018-12-31 10:30:45', 'LFPG', 'LGAV', 2, 180),
       ('LH1354', '2018-12-31 10:30:45', 'EDDT', 'LIRA', 1, 120),
       ('LH1761', '2018-12-31 10:30:45', 'EDDF', 'EDDT', 4, 240),
       ('LH1769', '2018-12-31 10:30:45', 'LIRA', 'EDDF', 1, 180),
       ('LH1943', '2018-12-31 10:30:45', 'EDDL', 'EGLC', 2, 180),
       ('LH1973', '2018-12-31 10:30:45', 'EDDF', 'LFPG', 3, 180),
       ('LH2301', '2018-07-26 10:50:00', 'EDDF', 'CYYZ', 2, 340),
       ('LH3333', '2018-06-05 09:30:45', 'EDDF', 'EDDM', 1, 155),
       ('LH3584', '2018-12-31 10:30:45', 'EDDM', 'EDDF', 1, 180),
       ('LH3842', '2018-12-31 10:30:45', 'EGLC', 'EDDL', 4, 120),
       ('LH5301', '2018-07-02 16:20:00', 'EDDF', 'CYYZ', 1, 740),
       ('LH7660', '2018-12-31 10:30:45', 'LGAV', 'EDDM', 3, 240);


CREATE TABLE `airport`.`reservation`
(
    `ReservationID`           int(11)    NOT NULL,
    `CustomerID`              int(11)    NOT NULL,
    `NoReservedSeats`         int(11)    NOT NULL,
    `Comment`                 varchar(1000)       DEFAULT NULL,
    `FlightNo`                varchar(7) NOT NULL,
    `DepartureDateAndTimeUTC` timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`ReservationID`),
    KEY `CustomerID` (`CustomerID`),
    KEY `FlightNo` (`FlightNo`, `DepartureDateAndTimeUTC`),
    CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`FlightNo`, `DepartureDateAndTimeUTC`) REFERENCES `flightexecution` (`FlightNo`, `DepartureDateAndTimeUTC`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


INSERT INTO `reservation`
VALUES (1, 16579435, 1, '', 'LH1973', '2018-12-31 10:30:45'),
       (2, 35943147, 1, '', 'LH3584', '2018-12-31 10:30:45'),
       (3, 15943155, 2, 'Die Plätze sollten nebeneinander liegen', 'LH1167', '2018-12-31 10:30:45'),
       (4, 35943143, 1, '', 'LH7660', '2018-12-31 10:30:45'),
       (5, 73941738, 1, '', 'LH3842', '2018-12-31 10:30:45');


	
	
	


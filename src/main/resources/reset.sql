/*static*/
DROP DATABASE `test`;

/*static*/
CREATE DATABASE test;

/*static*/
USE test;

/*static*/
DROP TABLE IF EXISTS color;
/*static*/
CREATE TABLE color (
  name varchar(10) NOT NULL,
  red int,
  green int,
  blue int,
  PRIMARY KEY (name)
);

/*static*/
LOCK TABLES color WRITE;
/*static*/
INSERT INTO color VALUES ('red',255,0,0),('green',0,255,0),('blue',0,0,255);
/*static*/
UNLOCK TABLES;

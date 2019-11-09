# Create the schema if necessary.
CREATE SCHEMA IF NOT EXISTS SkiDataAPI;
USE SkiDataAPI;

# Drop tables if necessary.
DROP TABLE IF EXISTS Stats;
DROP TABLE IF EXISTS Seasons;
DROP TABLE IF EXISTS Resorts;
DROP TABLE IF EXISTS LiftRides;

# Create tables if necessary.
CREATE TABLE Stats (
StatId	INT UNSIGNED NOT NULL AUTO_INCREMENT,
UniqueId VARCHAR(255) NOT NULL,
Count  INT NOT NULL,
Mean  INT NOT NULL,
Max  INT NOT NULL,
Server VARCHAR(255) NOT NULL,
Operation VARCHAR(255) NOT NULL,
  PRIMARY KEY (StatId)
);

CREATE TABLE Resorts (
ResortId	INT UNSIGNED NOT NULL AUTO_INCREMENT,
ResortName VARCHAR(255) NOT NULL,
CONSTRAINT pk_Resorts_ResortId 
  PRIMARY KEY (ResortId)
);

CREATE TABLE Seasons (
SeasonId INT UNSIGNED NOT NULL AUTO_INCREMENT,
Season INT NOT NULL,
ResortId INT UNSIGNED,
CONSTRAINT pk_Seasons_SeasonId
  PRIMARY KEY (SeasonId),
CONSTRAINT fk_Seasons_Resorts_ResortId
  FOREIGN KEY (ResortId)
  REFERENCES Resorts (ResortId)
  ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT unique_resort_season UNIQUE (ResortId, Season)
);

CREATE TABLE LiftRides (
ResortId	INT  NOT NULL,
SeasonId INT NOT NULL,
DayId INT NOT NULL,
SkierId INT NOT NULL,
StartTime INT NOT NULL,
LiftId INT NOT NULL,
Vertical INT NOT NULL,
PRIMARY KEY (ResortId, SeasonId, DayId, SkierId, StartTime)
);
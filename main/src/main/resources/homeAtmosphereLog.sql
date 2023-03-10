CREATE TABLE IF NOT EXISTS TEMPERATURE_LOG (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  measureTimeStamp TIMESTAMP(9) NOT NULL,
  sensorId VARCHAR NOT NULL,
  temperature FLOAT NOT NULL,
  heaterState VARCHAR(250) NOT NULL
);
CREATE TABLE IF NOT EXISTS HUMIDITY_LOG (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  measureTimeStamp TIMESTAMP(9) NOT NULL,
  sensorId VARCHAR NOT NULL,
  humidity FLOAT NOT NULL
);
CREATE TABLE IF NOT EXISTS CO2_LOG (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  measureTimeStamp TIMESTAMP(9) NOT NULL,
  sensorId VARCHAR NOT NULL,
  co2ppm INTEGER NOT NULL
);
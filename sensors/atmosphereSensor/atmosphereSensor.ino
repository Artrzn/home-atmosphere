#include <ESP8266WiFi.h>
#include <OneWire.h>
#include <ESP8266WebServer.h>
#include <DHT.h>
#include "MHZ19_uart.h"

const char* ssid = "*******";
const char* password = "*****";
OneWire temperatureSensorDS(2);
DHT dht(13, DHT11);
MHZ19_uart mhz19;
ESP8266WebServer server(80);
int fanPin = 14;
float temperatureFromDS = 0.0;
float temperatureFromDHT = 0.0;
float humidityFromDHT = 0.0;
int co2ppm;
unsigned long lastCicleStamp;
int cicleDelay = 60000;
unsigned long lastFanCicleStamp;
int fanCicleDelay = cicleDelay / 3;
int fanStage = 1;

void setup() {
  //Serial.begin(9600);
  mhz19.begin(4, 5);
  mhz19.setAutoCalibration(false);
  int status;
  status = mhz19.getStatus();
  delay(2000);
  status = mhz19.getStatus();
  delay(2000);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  WiFi.setAutoReconnect(true);
  WiFi.persistent(true);
  server.begin();
  server.on("/", sendSensorsData);
  pinMode(fanPin, OUTPUT);
  digitalWrite(fanPin, LOW);
}

void loop() {
  if (millis() - lastCicleStamp >= cicleDelay) {
    lastCicleStamp = millis();
    updateTemperature();
    updateDataFromDHT();
    co2ppm = mhz19.getPPM();
    Serial.println();
    Serial.println(temperatureFromDS);
    Serial.println(humidityFromDHT);
    Serial.println(temperatureFromDHT);
    Serial.println(co2ppm);
    fanStage = 0;
  }
  if (millis() - lastFanCicleStamp >= fanCicleDelay) {
    lastFanCicleStamp = millis();
    if(fanStage < 2) {
      digitalWrite(fanPin, HIGH);
      //Serial.println("enable fan");
      fanStage++;
    } else {
      digitalWrite(fanPin, LOW);
      //Serial.println("disable fan");
    }
  }
  server.handleClient();
}

void sendSensorsData() {
  server.send(200, "application/json", "{\"temperatureFromDS\":\"" + String(temperatureFromDS) + "\", \"temperatureFrimDHT\":\"" + String(temperatureFromDHT) + "\", \"humidityFromDHT\":\"" + String(humidityFromDHT) + "\", \"co2ppm\":\"" + String(co2ppm) +  "\"}");
}

void updateDataFromDHT() {
   humidityFromDHT= dht.readHumidity();
   temperatureFromDHT = dht.readTemperature();  
}

float updateTemperature() {
  byte sensorData[2];
  temperatureSensorDS.reset();
  temperatureSensorDS.write(0xCC);
  temperatureSensorDS.write(0x44);
  delay(1000);
  temperatureSensorDS.reset();
  temperatureSensorDS.write(0xCC);
  temperatureSensorDS.write(0xBE);
  sensorData[0] = temperatureSensorDS.read();
  sensorData[1] = temperatureSensorDS.read();
  temperatureFromDS =  ((sensorData[1] << 8) | sensorData[0]) * 0.0625;
}

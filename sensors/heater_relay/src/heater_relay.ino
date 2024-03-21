#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#define RELAY 0
#define WDT 2
const char* ssid = "***";
const char* password = "***";
unsigned long lastWdtCallTimestampt;
unsigned long actualWdtCallTimestampt;
const int wdtCallTimeuot = 10000;
byte previousState = 1;
byte actualState = 0;
String stateValues[2] = {"on", "off"};

ESP8266WebServer server(80);

void setup() {
  delay(4000);
  pinMode(RELAY, OUTPUT);
  pinMode(WDT, OUTPUT);
  digitalWrite(RELAY, 1);
  digitalWrite(WDT, 0);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  WiFi.setAutoReconnect(true);
  WiFi.persistent(true);
  server.begin();
  server.on("/", switchRelayState);
}

void switchRelayState() {
  if (server.method() != HTTP_POST) {
    server.send(405, "text/plain", "Method Not Allowed");
  } else {
    String relayState = server.arg("relay_state");
    if (relayState == "on") {
      previousState = actualState;
      actualState = 0;
      digitalWrite(RELAY, 0);
      server.send(200, "text/plain", "Relay on. Previous state " + stateValues[previousState]);
    } else if (relayState == "off") {
      previousState = actualState;
      actualState = 1;
      digitalWrite(RELAY, 1);
      server.send(200, "text/plain", "Relay off. Previous state " + stateValues[previousState]);
    } else {
      server.send(400, "text/plain", "Parameter relay_state not defined or not corrected. Support values: on, off. Accepted value: " + relayState);
    }
  }
}

void loop() {
  actualWdtCallTimestampt = millis();
  if (actualWdtCallTimestampt - lastWdtCallTimestampt > wdtCallTimeuot) {
    digitalWrite(WDT, 1);
    digitalWrite(WDT, 0);  
    lastWdtCallTimestampt = actualWdtCallTimestampt;
  }
  server.handleClient();
}
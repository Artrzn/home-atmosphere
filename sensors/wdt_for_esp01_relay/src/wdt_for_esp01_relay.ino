#define RELAY_ENABLE 2
#define RELAY 3
#define WDT 4

void setup() {
  pinMode(RELAY_ENABLE, OUTPUT);
  pinMode(RELAY, INPUT_PULLUP);
  digitalWrite(RELAY_ENABLE, 0);
  delay(5000);  //на релюхе стоит кондей, даем ему разрядиться
  digitalWrite(RELAY_ENABLE, 1);
  pinMode(WDT, OUTPUT);
  digitalWrite(WDT, 0);
  relayCollConsume();  //дергаем на всякий случай из за применяемых задержек
  attachInterrupt(1, relayCollConsume, RISING);
}

void relayCollConsume() {
  digitalWrite(WDT, 1);
  digitalWrite(WDT, 0);
}

void loop() {
}

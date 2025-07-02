#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

// Pin assignments
const int PIN_LED_STATUS  = 2;    // Built-in LED
const int PIN_DUST_LED    = 5;    // IR LED control
const int PIN_DUST_ANALOG = 34;   // ADC input
const int PIN_RELAY       = 12;   // Fan relay

/* UUID’ler */
#define SERVICE_UUID      "12345678-1234-1234-1234-1234567890AB"
#define CHAR_DUST_UUID    "12345678-1234-1234-1234-1234567890DC"  // notify
#define CHAR_FAN_UUID     "12345678-1234-1234-1234-1234567890CA"  // write

BLECharacteristic* dustChar;
bool deviceConnected = false;

/* —— BLE Callbacks —— */
class MyServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer* s) override {
    deviceConnected = true;
    digitalWrite(PIN_LED_STATUS, HIGH);
    Serial.println("[BLE] Client connected");
  }
  void onDisconnect(BLEServer* s) override {
    deviceConnected = false;
    Serial.println("[BLE] Client disconnected – advertising restart");
    s->getAdvertising()->start();
  }
};

class FanCmdCallbacks : public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic* c) override {
    String v = c->getValue();
    if (!v.isEmpty()) {
      char cmd = v.charAt(0);  
      if (cmd == '1') {
        digitalWrite(PIN_RELAY, LOW);      // Fan ON
        Serial.println("[CMD] Fan ON");
      } else if (cmd == '0') {
        digitalWrite(PIN_RELAY, HIGH);     // Fan OFF
        Serial.println("[CMD] Fan OFF");
      } else {
        Serial.printf("[CMD] Unknown (%c)\n", cmd);
      }
    }
  }
};

void setup() {
  /* GPIO init */
  pinMode(PIN_LED_STATUS, OUTPUT);
  pinMode(PIN_DUST_LED,   OUTPUT);
  pinMode(PIN_RELAY,      OUTPUT);
  digitalWrite(PIN_DUST_LED, HIGH);  // IR LED OFF (active-low)
  digitalWrite(PIN_RELAY,   HIGH);   // Fan OFF  (active-low)

  /* Serial */
  Serial.begin(115200);
  Serial.println("\n=== Dust Sensor ESP32 Boot ===");

  /* BLE init */
  BLEDevice::init("Dust Sensor");
  BLEServer* server = BLEDevice::createServer();
  server->setCallbacks(new MyServerCallbacks());

  BLEService* svc = server->createService(SERVICE_UUID);

  dustChar = svc->createCharacteristic(
      CHAR_DUST_UUID, BLECharacteristic::PROPERTY_NOTIFY);
  dustChar->addDescriptor(new BLE2902());

  BLECharacteristic* fanChar = svc->createCharacteristic(
      CHAR_FAN_UUID, BLECharacteristic::PROPERTY_WRITE);
  fanChar->setCallbacks(new FanCmdCallbacks());

  svc->start();
  server->getAdvertising()->start();
  Serial.println("[BLE] Advertising started");
}

void loop() {
  if (!deviceConnected) {                 // Advertise state
    digitalWrite(PIN_LED_STATUS, HIGH);
    delay(500);
    digitalWrite(PIN_LED_STATUS, LOW);
    delay(500);
    return;
  }

  /* —— Ölçüm Döngüsü —— */
  digitalWrite(PIN_DUST_LED, LOW);        // IR LED ON
  delayMicroseconds(280);
  uint16_t rawAdc = analogRead(PIN_DUST_ANALOG);
  delayMicroseconds(40);
  digitalWrite(PIN_DUST_LED, HIGH);       // IR LED OFF

  float voltage = rawAdc * (3.3f / 4095.0f);
  float ugm3    = 170.0f * voltage - 0.1f;   // basit kalibrasyon

  /* Debug çıkışları */
  Serial.printf("[DATA] ADC=%u  V=%.3f  Dust=%.2f µg/m³\n",
                rawAdc, voltage, ugm3);

  /* BLE Notify */
  char buf[12];
  snprintf(buf, sizeof(buf), "%.2f", ugm3);
  dustChar->setValue((uint8_t*)buf, strlen(buf));
  dustChar->notify();
  Serial.println("[BLE] Notified");

  delay(10000);    // 3 sn’de bir ölç
}

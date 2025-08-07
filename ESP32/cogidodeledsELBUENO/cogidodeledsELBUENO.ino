#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

// Pines para cada LED
const int LED_OJO_DER_PIN = 23;       // Comando "ojoder"
const int LED_OJO_IZQ_PIN = 22;       // Comando "ojoizq"
const int LED_OREJA_DER_PIN = 21;     // Comando "orejader"
const int LED_OREJA_IZQ_PIN = 19;     // Comando "orejaizq"
const int LED_NARIZ_PIN = 16;         // Comando "nariz"
const int LED_BOCA_PIN = 17;          // Comando "boca"
const int LED_CABELLO_PIN = 18;       // Comando "cabello"

// Callback para eventos Bluetooth
void btCallback(esp_spp_cb_event_t event, esp_spp_cb_param_t *param) {
  if (event == ESP_SPP_SRV_OPEN_EVT) {
    Serial.println("Dispositivo Bluetooth CONECTADO");
  } else if (event == ESP_SPP_CLOSE_EVT) {
    Serial.println("Dispositivo Bluetooth DESCONECTADO");
  }
}

void setup() {
  Serial.begin(115200);

  pinMode(LED_OJO_DER_PIN, OUTPUT);
  pinMode(LED_OJO_IZQ_PIN, OUTPUT);
  pinMode(LED_OREJA_DER_PIN, OUTPUT);
  pinMode(LED_OREJA_IZQ_PIN, OUTPUT);
  pinMode(LED_NARIZ_PIN, OUTPUT);
  pinMode(LED_BOCA_PIN, OUTPUT);
  pinMode(LED_CABELLO_PIN, OUTPUT);

  SerialBT.begin("Plimplim_ESP32Abi");
  SerialBT.register_callback(btCallback); // Registrar callback
  Serial.println("El dispositivo Bluetooth ha iniciado. Esperando conexión...");
}

void loop() {
  if (SerialBT.available()) {
    String command = SerialBT.readStringUntil('\n');
    command.trim();

    Serial.print("Comando recibido: ");
    Serial.println(command);

if (command.equals("All")) {
      Serial.println("ALL");
      digitalWrite(LED_OJO_DER_PIN, HIGH);
       digitalWrite(LED_OJO_IZQ_PIN, HIGH);
        digitalWrite(LED_OREJA_DER_PIN, HIGH);
         digitalWrite(LED_OREJA_IZQ_PIN, HIGH);
          digitalWrite(LED_BOCA_PIN, HIGH);
           digitalWrite(LED_NARIZ_PIN, HIGH);
            digitalWrite(LED_CABELLO_PIN, HIGH);
      delay(500);
       digitalWrite(LED_OJO_DER_PIN, LOW);
       digitalWrite(LED_OJO_IZQ_PIN, LOW);
        digitalWrite(LED_OREJA_DER_PIN, LOW);
         digitalWrite(LED_OREJA_IZQ_PIN, LOW);
          digitalWrite(LED_BOCA_PIN, LOW);
           digitalWrite(LED_NARIZ_PIN, LOW);
            digitalWrite(LED_CABELLO_PIN, LOW);
      }


    if (command.equals("ojoder")) {
      Serial.println("OJODER");
      digitalWrite(LED_OJO_DER_PIN, HIGH);
      delay(500);
      digitalWrite(LED_OJO_DER_PIN, LOW);
    } else if (command.equals("ojoizq")) {
      Serial.println("OJOIZQ");
      digitalWrite(LED_OJO_IZQ_PIN, HIGH);
      delay(500);
      digitalWrite(LED_OJO_IZQ_PIN, LOW);
    } else if (command.equals("orejader")) {
      Serial.println("OREJADER");
      digitalWrite(LED_OREJA_DER_PIN, HIGH);
      delay(500);
      digitalWrite(LED_OREJA_DER_PIN, LOW);
    } else if (command.equals("orejaizq")) {
      Serial.println("OREJAIZQ");
      digitalWrite(LED_OREJA_IZQ_PIN, HIGH);
      delay(500);
      digitalWrite(LED_OREJA_IZQ_PIN, LOW);
    } else if (command.equals("nariz")) {
      digitalWrite(LED_NARIZ_PIN, HIGH);
      delay(500);
      digitalWrite(LED_NARIZ_PIN, LOW);
    } else if (command.equals("boca")) {
      Serial.println("BOCA ON!");
      digitalWrite(LED_BOCA_PIN, HIGH);
      delay(500);
      digitalWrite(LED_BOCA_PIN, LOW);
    } else if (command.equals("cabello")) {
      digitalWrite(LED_CABELLO_PIN, HIGH);
      delay(500);
      digitalWrite(LED_CABELLO_PIN, LOW);
    }
  }

  delay(60);
}
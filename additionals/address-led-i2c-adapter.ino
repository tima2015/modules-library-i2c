// I2C bus control library
#include <Wire.h>
//LED Strip control library
#define TLED_CHIP LED_WS2812
#define TLED_ORDER ORDER_GRB
#include <tinyLED.h>

//**********
//Define cons
//I2C constants
const uint8_t deviceI2CAddress = 0x8;
const uint8_t patternLengthI2CAddress = 0x00;
const uint8_t ledCountI2CAddress = 0x01;
const uint8_t brightnessI2CAddress = 0x02;
const uint8_t patternI2CAddress = 0x03;
const uint8_t updateDelayI2CAddress = 0x04;

//Led constant s
const uint8_t ledPin = PB1;
//Other constants
const uint8_t maxPatternLenght = 64;
const uint8_t patternBytesArraySize = maxPatternLenght * 3;

//**********
//Variables
uint8_t selectedDataAddress = -1;
uint8_t patternLenght = 0;
uint16_t ledCount = 0;
uint8_t brightness = 255;
uint16_t updateDelay = 2500;
uint8_t pattern[patternBytesArraySize];
tinyLED<ledPin> strip;


void setup() {
  Wire.begin(deviceI2CAddress);
  Wire.onReceive(onReceive);
  Wire.onRequest(onRequest); 
}

/**
 * Основной цикл скетча
 * Раз в 1 + updateDelay выполняет обновление светодиодов.
 */
void loop() {
  delay(1 + updateDelay);
  if (patternLenght == 0) {
    return;
  }
  updateStrip();
}

/**
 * Обновить состояние светодиодов
 */
void updateStrip() {
  uint8_t scaledPatternLenght = patternLenght * 3;
  for (uint16_t i = 0, pattern_i = 0; i < ledCount; i++) {
    strip.sendRGB(pattern[pattern_i], pattern[pattern_i + 1], pattern[pattern_i + 2]);
    pattern_i += 3;
    if (pattern_i >= scaledPatternLenght) {
      pattern_i = 0;
    }
  }
  strip.setBrightness(brightness);
}

/**
 * Отправка запрошенных данных мастер-устройству по шине I2C
 */
void onRequest() {
  switch (selectedDataAddress) {
    case patternLenghtI2CAddress:
      Wire.write(patternLenght);
      break;
    case ledCountI2CAddress:
      writeWord(ledCount);
      break;
    case brightnessI2CAddress:
      Wire.write(brightness);
      break;
    case patternI2CAddress:
      Wire.write(pattern, patternBytesArraySize);
      break;
    case updateDelayI2CAddress:
      writeWord(updateDelay);
      break;
  }
}

void writeWord(uint16_t &word) {
  uint8_t *buffer = new uint8_t[2];
  buffer[0] = (uint8_t) word;
  buffer[1] = word >> 8;
  Wire.write(buffer, 2);
  delete[] buffer;
}

/**
 * Обработка принятых данных по шине I2C
 */
void onReceive(int nBytes) {
  if (!Wire.available()) {
    return;
  }

  selectedDataAddress = Wire.read();

  if (nBytes == 1) {
    return;
  }
  
  if (!Wire.available()) {
    return;
  }

  switch(selectedDataAddress) {
    case patternLenghtI2CAddress:
      setPatternLenghtFromI2C();
      break;
    case ledCountI2CAddress:
      setLedCountFromI2C();
      break;
    case brightnessI2CAddress:
      setBrightnessFromI2C();
      break;
    case patternI2CAddress:
      setPattern(nBytes);
      break;
    case updateDelayI2CAddress:
      setUpdateDelay();
      break;
  }
  clearBuffer();
}

void readWord(uint16_t &dst) {
  dst = Wire.read();
  if (Wire.available()) {
    dst = Wire.read() << 8 | dst;
  }
}

/**
 * Считываем и устанавливаем длину паттерна
 */
void setPatternLenghtFromI2C() {
  //Считываем байт размера паттерна
  patternLenght = Wire.read();

  //Если указан размер паттерна больше допустимого, округляем
  if (patternLenght > maxPatternLenght) {
    patternLenght = maxPatternLenght;
  }
  //Обнуление шаблона в случае уменьшения размера паттерна
  for (int i = patternLenght * 3; i < patternBytesArraySize; i++) {
    pattern[i] = 0;        
  }
}

/**
 * Считываем слово указывающее на количество светодиодов
 */
void setLedCountFromI2C() {
  readWord(ledCount);
}

/**
 * Считываем байт яркости
 */
void setBrightnessFromI2C() {
  brightness = Wire.read();  
}

/** 
 * Считываем байты цветов паттерна
 * 1 цвет == 3 байта
 */
void setPattern(int nBytes) {
  for (int pattern_i = 0, byte_i = 1; nBytes - byte_i >= 3 && pattern_i < patternLenght * 3; pattern_i += 3, byte_i += 3) {
    if (Wire.available()) pattern[pattern_i] = Wire.read();
    if (Wire.available()) pattern[pattern_i + 1] = Wire.read();
    if (Wire.available()) pattern[pattern_i + 2] = Wire.read();
  }
}

/**
 * Считываем слово содержащее время задержки в мс
 */
void setUpdateDelay() {
  readWord(updateDelay);
}

/**
 * Очистка буфера передающего лишние байты
 */
void clearBuffer() {
  while(Wire.available()) {
    Wire.read();
  }
}

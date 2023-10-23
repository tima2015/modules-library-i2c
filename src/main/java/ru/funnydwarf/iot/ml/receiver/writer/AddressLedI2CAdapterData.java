package ru.funnydwarf.iot.ml.receiver.writer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
@NoArgsConstructor
public class AddressLedI2CAdapterData {

    public enum WriteMode {
        FULL, NO, MAIN_DATA,
        PATTERN, ONLY_LED_COUNT, ONLY_BRIGHTNESS,
        ONLY_PATTERN_LENGHT, ONLY_UPDATE_DELAY, ONLY_PATTERN_COLORS
    }

    public AddressLedI2CAdapterData(int ledCount, byte brightness, byte patternLength, int updateDelay, WriteMode writeMode) {
        this.ledCount = ledCount;
        this.brightness = brightness;
        this.patternLength = patternLength;
        this.updateDelay = updateDelay;
        this.pattern = new Color[patternLength];
        for (int i = 0; i < this.pattern.length; i++) {
            this.pattern[i] = new Color(Color.LIGHT_GRAY.getRGB());
        }
        this.writeMode = writeMode;
    }

    /**
     * Количество светодиодов.
     * По умолчанию 0.
     * От 0 до 65535 (0xFFFF).
     */
    private int ledCount = 0;

    /**
     * Яркость ленты.
     * По умолчанию 255
     * От 0 до 255 (0xFF).
     */
    private byte brightness = (byte) 0xFF;

    /**
     * Размер паттерна.
     * По умолчанию 0.
     * От 0 до 64.
     */
    private byte patternLength;
    private Color[] pattern;
    /**
     * Время между синхронизацией контролера ленты и самой ленты.
     * Рекомендую устанавливать не слишком маленькое.
     * По умолчанию 2500.
     * От 0 до 65535 (0xFFFF).
     */
    private int updateDelay = 2500;

    private WriteMode writeMode = WriteMode.FULL;

    public void setPatternLength(byte patternLength) {
        this.patternLength = patternLength;
        Color[] pattern = new Color[patternLength];
        int i;
        //Копирование старых значений в новый массив
        for (i = 0; i < this.pattern.length && i < pattern.length; i++) {
            pattern[i] = this.pattern[i];
        }
        //Если ещё осталось место, заполняет его новыми объектами регистров
        for (; i < pattern.length; i++) {
            pattern[i] = new Color(Color.LIGHT_GRAY.getRGB());
        }
        this.pattern = pattern;
    }
}

package ru.funnydwarf.iot.ml.sensor.reader;

import lombok.extern.slf4j.Slf4j;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;

/**
 * Реализация читателя показаний датчика AHT10.
 * AHT10 - датчик температуры и влажности воздуха.
 * МОМЕНТ! На форумах вычитал что этот датчик плохо работает вместе с другими устройствами на шине
 * Так же данный датчик требует инициализации перед использованием
 */
@Slf4j
public class AHT10Reader implements Reader {
    private static final int commandRegister = 0xAC;
    private static final int dataRegister = 0x00;
    private static final int[] measurementTriggerCommand = {0x33, 0x00};
    private static final int dataBlockBytesCount = 6;
    @Override
    public double[] read(Object address, Object... args) {
        log.debug("read() called with: address = [{}]", address);
        I2CAddress i2cAddress = (I2CAddress) address;

        int[] bytes;
        try {
            I2CDriverWorker.writeBlock(i2cAddress, commandRegister, measurementTriggerCommand);
            Thread.sleep(1000);// FIXME: 24.03.2023 написать класс конфигурации регистров, проверять с его помощью состояние замера
            bytes = I2CDriverWorker.readBlock(i2cAddress, dataRegister, dataBlockBytesCount);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        int rowTemperature = (bytes[3] & 0x0F) << 8;
        rowTemperature |= bytes[4];
        rowTemperature <<= 8;
        rowTemperature |= bytes[5];
        double temperature = ((double)rowTemperature) * 200 / 1048576.0 - 50;

        int rowHumidity = bytes[1];
        rowHumidity <<= 8;
        rowHumidity |= bytes[2];
        rowHumidity <<= 4;
        rowHumidity |= bytes[3] >> 4;
        double humidity = ((double) rowHumidity) * 100 / 1048576.0;

        return new double[]{
                temperature,
                humidity
        };
    }
}

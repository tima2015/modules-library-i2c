package ru.funnydwarf.iot.ml.sensor.reader;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.sensor.CurrentMeasurementSession;
import ru.funnydwarf.iot.ml.sensor.Measurement;
import ru.funnydwarf.iot.ml.sensor.MeasurementDescription;
import ru.funnydwarf.iot.ml.sensor.MeasurementDescriptionRepository;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Реализация читателя показаний датчика AHT10.
 * AHT10 - датчик температуры и влажности воздуха.
 * МОМЕНТ! На форумах вычитал что этот датчик плохо работает вместе с другими устройствами на шине
 * Так же данный датчик требует инициализации перед использованием
 */
@Slf4j
public class AHT10Reader implements Reader {
    private static final String commandRegister = "0xAC";
    private static final String dataRegister = "0x00";
    private static final List<String> measurementTriggerCommand = List.of("0x33", "0x00");
    @Override
    public double[] read(Object address, Object... args) {
        log.debug("read() called with: address = [{}]", address);
        I2CAddress i2cAddress = (I2CAddress) address;

        String result = null;
        try {
            I2CDriverWorker.writeBlockData(i2cAddress, commandRegister, measurementTriggerCommand);
            result = I2CDriverWorker.readBlockData(i2cAddress, dataRegister);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        String[] bytesString = result.split(" ");
        int rowTemperature = (Byte.parseByte(bytesString[3],16) & 0x0F) << 16;
        rowTemperature |= Byte.parseByte(bytesString[4],16) << 8;
        rowTemperature |= Byte.parseByte(bytesString[5],16);
        double temperature = rowTemperature * 200 / 1048576.0 - 50;

        int rowHumidity = Byte.parseByte(bytesString[1],16) << 16;
        rowHumidity |= Byte.parseByte(bytesString[2],16) << 8;
        rowHumidity |= Byte.parseByte(bytesString[3],16);
        rowHumidity >>= 4;
        double humidity = rowHumidity * 100 / 1048576.0;

        return new double[]{
                temperature,
                humidity
        };
    }
}

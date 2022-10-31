package ru.funnydwarf.iot.ml.sensor.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.sensor.MeasurementData;

import java.io.IOException;
import java.util.Date;

/**
 * Реализация читателя показаний датчика AHT10.
 * AHT10 - датчик температуры и влажности воздуха.
 * МОМЕНТ! На форумах вычитал что этот датчик плохо работает вместе с другими устройствами на шине
 * Так же данный датчик требует инициализации перед использованием
 */
@Component
public class AHT10Reader implements Reader {

    private static final Logger log = LoggerFactory.getLogger(AHT10Reader.class);

    @Value("${Modules.Sensors.AHT10.temperatureUnitName:°C}")
    private String temperatureUnitName;

    @Value("${Modules.Sensors.DS18B20.temperatureMeasurementName:Temperature}")
    private String temperatureMeasurementName;

    @Value("${Modules.Sensors.AHT10.airHumidityUnitName:%}")
    private String airHumidityUnitName;

    @Value("${Modules.Sensors.DS18B20.airHumidityMeasurementName:Air Humidity}")
    private String airHumidityMeasurementName;

    @Override
    public MeasurementData[] read(Object address) {
        log.debug("read() called with: address = [{}]", address);
        I2CAddress i2CAddress = (I2CAddress) address;
        ProcessBuilder doMeasurementProcessBuilder = new ProcessBuilder("i2cset", "-y",
                String.valueOf(i2CAddress.bus()), i2CAddress.deviceAddress(), "0xAC", "0x33", "0x00", "i");
        ProcessBuilder getResultProcessBuilder = new ProcessBuilder("i2cget", "-y",
                String.valueOf(i2CAddress.bus()), i2CAddress.deviceAddress(), "0x00", "i");
        String result = null;

        try {
            doMeasurementProcessBuilder.start().waitFor();
            Process getResult = getResultProcessBuilder.start();
            getResult.waitFor();
            result = new String(getResult.getInputStream().readAllBytes());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        String[] bytesString = result.split(" ");
        int rowTemperature = (Byte.parseByte(bytesString[3]) & 0x0F) << 16;
        rowTemperature |= Byte.parseByte(bytesString[4]) << 8;
        rowTemperature |= Byte.parseByte(bytesString[5]);
        double temperature = rowTemperature * 200 / 1048576.0 - 50;

        int rowHumidity = Byte.parseByte(bytesString[1]) << 16;
        rowHumidity |= Byte.parseByte(bytesString[2]) << 8;
        rowHumidity |= Byte.parseByte(bytesString[3]);
        rowHumidity >>= 4;
        double humidity = rowHumidity * 100 / 1048576.0;

        return new MeasurementData[] {
                MeasurementData.createToCurrentDate(temperature, temperatureUnitName, temperatureMeasurementName),
                MeasurementData.createToCurrentDate(humidity, airHumidityUnitName, airHumidityMeasurementName)
        };
    }

    @Override
    public MeasurementData[] getTemplateRead() {
        Date incorrectDate = new Date(1);
        return new MeasurementData[]{
                new MeasurementData(Double.MIN_VALUE, temperatureUnitName, temperatureMeasurementName, incorrectDate),
                new MeasurementData(Double.MIN_VALUE, airHumidityUnitName, airHumidityMeasurementName, incorrectDate),
        };
    }
}

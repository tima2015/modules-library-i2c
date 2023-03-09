package ru.funnydwarf.iot.ml.sensor.register;

import ru.funnydwarf.iot.ml.I2CAddress;

import java.io.IOException;

public interface Readable {
    void readRegisterValueFromDevice(I2CAddress address) throws IOException, InterruptedException;
}

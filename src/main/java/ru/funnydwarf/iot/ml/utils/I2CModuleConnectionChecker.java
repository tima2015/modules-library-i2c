package ru.funnydwarf.iot.ml.utils;

import ru.funnydwarf.iot.ml.I2CAddress;

import java.io.IOException;

public class I2CModuleConnectionChecker {

    private I2CModuleConnectionChecker(){}

    public static boolean isModuleConnected(I2CAddress address) throws IOException, InterruptedException {
        ProcessBuilder checkModuleProcessBuilder =
                new ProcessBuilder("i2cdetect", "-y", String.valueOf(address.bus()));
        Process check = checkModuleProcessBuilder.start();
        check.waitFor();
        return new String(check.getInputStream().readAllBytes()).contains(address.deviceAddress());
    }
}

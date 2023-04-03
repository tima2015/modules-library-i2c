package ru.funnydwarf.iot.ml;


import lombok.Getter;
import ru.funnydwarf.iot.ml.sensor.Sensor;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class I2CModuleGroup extends ModuleGroup implements ModuleListReadable<I2CAddress> {

    private final String bus;

    public I2CModuleGroup(String bus) {
        super("I2C", "Inter-Integrated Circuit");
        this.bus = bus;
    }

    @Override
    protected InitializationState initialize() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("i2cdetect", "-l");
        Process process = builder.start();
        process.waitFor();
        if (new String(process.getInputStream().readAllBytes()).contains("i2c-" + bus)) {
            return InitializationState.OK;
        }
        return InitializationState.INITIALIZATION_ERROR;
    }

    @Override
    public List<I2CAddress> readModuleAdressesList() {
        List<I2CAddress> addresses = new ArrayList<>();
        String devicesTable = null;
        try {
            devicesTable = I2CDriverWorker.readDetectedDevices(bus);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        String[] rows = devicesTable.split("\n");
        for (int row_i = 1; row_i < rows.length; row_i++) {
            String addressRow = rows[row_i].substring(rows[row_i].indexOf(' ') + 1);
            for (int column_i = 0; column_i < addressRow.length(); column_i+= 3) {
                String address = addressRow.substring(column_i, column_i + 2);
                if (address.equals("--") || address.equals("  ")){
                    continue;
                }
                addresses.add(new I2CAddress(bus, "0x%h%h".formatted(row_i - 1, column_i / 3)));
            }
        }
        return addresses;
    }
}

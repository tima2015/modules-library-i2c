package ru.funnydwarf.iot.ml.sensor;

import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.I2CModuleGroup;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.sensor.dataio.DataInput;
import ru.funnydwarf.iot.ml.sensor.dataio.DataOutput;
import ru.funnydwarf.iot.ml.sensor.reader.AHT10Reader;
import ru.funnydwarf.iot.ml.utils.I2CModuleConnectionChecker;

public class AHT10Sensor extends Sensor {

    public AHT10Sensor(AHT10Reader reader, DataInput dataInput, DataOutput dataOutput, I2CModuleGroup group,
                       I2CAddress address, String name, String description) {
        super(reader, dataInput, dataOutput, group, address, name, description);
    }

    @Override
    protected InitializationState initialize() throws Exception {
        InitializationState state = super.initialize();
        I2CAddress address = (I2CAddress) getAddress();
        if (!I2CModuleConnectionChecker.isModuleConnected(address)) {
            return InitializationState.INITIALIZATION_ERROR;
        }

        ProcessBuilder initProcessBuilder = new ProcessBuilder("i2cset", String.valueOf(address.bus()),
                address.deviceAddress(), "0xE1", "0x08", "0x00", "i");
        Process process = initProcessBuilder.start();
        process.waitFor();
        return state;
    }
}

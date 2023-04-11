package ru.funnydwarf.iot.ml.sensor;

import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.I2CModuleGroup;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.sensor.reader.ADS1115Reader;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

@Deprecated
public class ADS1115Sensor extends Sensor<I2CModuleGroup, I2CAddress>{
    public ADS1115Sensor(MeasurementDescriptionRepository mdr, ADS1115Reader reader, I2CModuleGroup group, I2CAddress address, String name, String description) {
        super(reader, new MeasurementData[0]
                //mdr.findByUnitNameAndNameAndDescription("%", "value", "value from %s".formatted(name))
        , group, address, null, null);
    }

    @Override
    protected InitializationState initialize() throws Exception {
        if (!I2CDriverWorker.readDetectedDevices(getAddress().bus()).contains(getAddress().deviceAddress())) {
            return InitializationState.INITIALIZATION_ERROR;
        }
        return super.initialize();
    }
}

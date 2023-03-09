package ru.funnydwarf.iot.ml.sensor;

import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.I2CModuleGroup;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.sensor.reader.ADS1115Reader;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;

public class ADS1115Sensor extends Sensor{
    public ADS1115Sensor(MeasurementDescriptionRepository mdr, CurrentMeasurementSession cms, ADS1115Reader reader, I2CModuleGroup group, I2CAddress address, String name, String description) {
        super(reader, new MeasurementDescription[]{
                mdr.findByUnitNameAndNameAndDescription("%", "value", "value from %s".formatted(name))
        }, cms, group, address, name, description);
    }

    public ADS1115Sensor(MeasurementDescriptionRepository mdr, CurrentMeasurementSession cms, String measurementName, String measurementDescription, ADS1115Reader reader, I2CModuleGroup group, I2CAddress address, String name, String description) {
        super(reader, new MeasurementDescription[]{
                mdr.findByUnitNameAndNameAndDescription("%", measurementName, measurementDescription)
        }, cms, group, address, name, description);
    }

    @Override
    protected InitializationState initialize() throws Exception {
        I2CAddress address = (I2CAddress) getAddress();
        if (!I2CDriverWorker.readDetectedDevices(address.bus()).contains(address.deviceAddress())) {
            return InitializationState.INITIALIZATION_ERROR;
        }
        return super.initialize();
    }
}

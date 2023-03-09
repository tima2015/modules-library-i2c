package ru.funnydwarf.iot.ml.sensor;

import lombok.extern.slf4j.Slf4j;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.I2CModuleGroup;
import ru.funnydwarf.iot.ml.InitializationState;
import ru.funnydwarf.iot.ml.sensor.reader.AHT10Reader;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;
import ru.funnydwarf.iot.ml.utils.I2CModuleConnectionChecker;

import java.util.List;

@Slf4j
public class AHT10Sensor extends Sensor {

    public AHT10Sensor(MeasurementDescriptionRepository mdr, CurrentMeasurementSession cms, AHT10Reader reader, I2CModuleGroup group, I2CAddress address, String name, String description) {
        super(reader,
                new MeasurementDescription[]{
                        MeasurementDescriptionRepository.findOrCreate(mdr, "°C", "Temperature", "AHT10 temperature"),
                        MeasurementDescriptionRepository.findOrCreate(mdr, "%", "Air Humidity", "AHT10 air humidity")
                },
                cms,
                group,
                address,
                name,
                description);
    }

    @Override
    protected InitializationState initialize() throws Exception {
        I2CAddress address = (I2CAddress) getAddress();
        try {
            if (!I2CDriverWorker.readDetectedDevices(address.bus()).contains(address.deviceAddress())) {
                return InitializationState.INITIALIZATION_ERROR;
            }
            String init_register = "0xE1";
            List<String> initializeCommand = List.of("0x08", "0x00");
            I2CDriverWorker.writeBlockData(address, init_register, initializeCommand);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return InitializationState.INITIALIZATION_ERROR;
        }
        return super.initialize();
    }
}

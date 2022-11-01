package ru.funnydwarf.iot.ml.sensor;

import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.I2CModuleGroup;
import ru.funnydwarf.iot.ml.sensor.dataio.DataInput;
import ru.funnydwarf.iot.ml.sensor.dataio.DataOutput;
import ru.funnydwarf.iot.ml.sensor.reader.Reader;

public class ADS1115Sensor extends Sensor{
    public ADS1115Sensor(Reader reader, DataInput dataInput, DataOutput dataOutput, I2CModuleGroup group,
                         I2CAddress address, String name, String description) {
        super(reader, dataInput, dataOutput, group, address, name, description);
    }


}

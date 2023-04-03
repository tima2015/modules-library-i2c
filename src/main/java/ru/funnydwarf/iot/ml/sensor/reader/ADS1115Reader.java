package ru.funnydwarf.iot.ml.sensor.reader;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.sensor.Measurement;
import ru.funnydwarf.iot.ml.utils.I2CDriverWorker;
import ru.funnydwarf.iot.ml.utils.ads1115.*;

import java.io.IOException;
import java.util.HexFormat;
import java.util.List;

@Slf4j
@Deprecated // FIXME: 19.03.2023 нужно переделать конфиг
public class ADS1115Reader implements Reader<I2CAddress> {

    private final ADS1115Config config;

    public ADS1115Reader(ADS1115Config config) {
        this.config = config;
    }

    @Override
    public double[] read(I2CAddress address, Object ... args) {
        /*log.debug("read() called with: address = [{}]", address);
        I2CAddress i2cAddress = (I2CAddress) address;

        if (config.getMode() == ADS1115Mode.SINGLE && config.getState() != ADS1115State.READ_DONE_WRITE_START) {
            config.setState(ADS1115State.READ_DONE_WRITE_START);
        }

        String rowValue;
        try {
            I2CDriverWorker.writeBlockData(i2cAddress, ADS1115Registers.CONFIG_REGISTER, config.getConfigBytePair());
            boolean flag = true;
            while (flag){
                String rowResults = I2CDriverWorker.readBlockData(i2cAddress, ADS1115Registers.CONFIG_REGISTER);
                List<String> resultBytes = List.of(rowResults.split(" "));
                flag = !resultBytes.equals(config.getConfigBytePair());
            }
            rowValue = I2CDriverWorker.readBlockData(i2cAddress, ADS1115Registers.CONVERSION_REGISTER);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        byte[] resultBytes = HexFormat.of().withDelimiter(" ").withPrefix("0x").parseHex(rowValue);
        short resultShort = 0;
        resultShort |= ((resultBytes[0] << 8) | resultBytes[1]);
        double result = 100 * (resultShort < 0 ? resultShort / (double) Math.abs(Short.MIN_VALUE) : resultShort / (double) Short.MAX_VALUE);
        return new double[] { result };*/
        return new double[0];
    }
}

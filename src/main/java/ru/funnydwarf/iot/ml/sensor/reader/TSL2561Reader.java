package ru.funnydwarf.iot.ml.sensor.reader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.sensor.register.TSL2561Config;

import java.io.IOException;

@Slf4j
public class TSL2561Reader implements Reader {

    TSL2561Config config;

    @Setter
    @Getter
    private long manualTiming = 1000;
    @Setter
    @Getter
    private boolean useCSPackage = true; //Chip-scale package else T, FN and CL package

    public TSL2561Reader(TSL2561Config config) {
        this.config = config;
    }

    private short getTwoByteValue(TSL2561Config.DataRegister high, TSL2561Config.DataRegister low) {
        short value = high.getValue();
        value <<= 8;
        value |= low.getValue();
        return value;
    }

    private static final int CH_SCALE = 10;
    private static final int RATIO_SCALE = 9;
    private static final int LUX_SCALE = 14;
    private static final long NO_SCALE = 1 << CH_SCALE;
    private static final long SCALE_FOR_13_7_MS = 29975; // 322/11 * 2^CH_SCALE
    private static final long SCALE_FOR_101_MS = 4071; // 322/81 * 2^CH_SCALE
    private static final int K1T = 0x0040; // 0.125 * 2^RATIO_SCALE
    private static final int B1T = 0x01f2; // 0.0304 * 2^LUX_SCALE
    private static final int M1T = 0x01be; // 0.0272 * 2^LUX_SCALE
    private static final int K2T = 0x0080; // 0.250 * 2^RATIO_SCALE
    private static final int B2T = 0x0214; // 0.0325 * 2^LUX_SCALE
    private static final int M2T = 0x02d1; // 0.0440 * 2^LUX_SCALE
    private static final int K3T = 0x00c0; // 0.375 * 2^RATIO_SCALE
    private static final int B3T = 0x023f; // 0.0351 * 2^LUX_SCALE
    private static final int M3T = 0x037b; // 0.0544 * 2^LUX_SCALE
    private static final int K4T = 0x0100; // 0.50 * 2^RATIO_SCALE
    private static final int B4T = 0x0270; // 0.0381 * 2^LUX_SCALE
    private static final int M4T = 0x03fe; // 0.0624 * 2^LUX_SCALE
    private static final int K5T = 0x0138; // 0.61 * 2^RATIO_SCALE
    private static final int B5T = 0x016f; // 0.0224 * 2^LUX_SCALE
    private static final int M5T = 0x01fc; // 0.0310 * 2^LUX_SCALE
    private static final int K6T = 0x019a; // 0.80 * 2^RATIO_SCALE
    private static final int B6T = 0x00d2; // 0.0128 * 2^LUX_SCALE
    private static final int M6T = 0x00fb; // 0.0153 * 2^LUX_SCALE
    private static final int K7T = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int B7T = 0x0018; // 0.00146 * 2^LUX_SCALE
    private static final int M7T = 0x0012; // 0.00112 * 2^LUX_SCALE
    private static final int K8T = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int B8T = 0x0000; // 0.000 * 2^LUX_SCALE
    private static final int M8T = 0x0000; // 0.000 * 2^LUX_SCALE
    private static final int K1C = 0x0043; // 0.130 * 2^RATIO_SCALE
    private static final int B1C = 0x0204; // 0.0315 * 2^LUX_SCALE
    private static final int M1C = 0x01ad; // 0.0262 * 2^LUX_SCALE
    private static final int K2C = 0x0085; // 0.260 * 2^RATIO_SCALE
    private static final int B2C = 0x0228; // 0.0337 * 2^LUX_SCALE
    private static final int M2C = 0x02c1; // 0.0430 * 2^LUX_SCALE
    private static final int K3C = 0x00c8; // 0.390 * 2^RATIO_SCALE
    private static final int B3C = 0x0253; // 0.0363 * 2^LUX_SCALE
    private static final int M3C = 0x0363; // 0.0529 * 2^LUX_SCALE
    private static final int K4C = 0x010a; // 0.520 * 2^RATIO_SCALE
    private static final int B4C = 0x0282; // 0.0392 * 2^LUX_SCALE
    private static final int M4C = 0x03df; // 0.0605 * 2^LUX_SCALE
    private static final int K5C = 0x014d; // 0.65 * 2^RATIO_SCALE
    private static final int B5C = 0x0177; // 0.0229 * 2^LUX_SCALE
    private static final int M5C = 0x01dd; // 0.0291 * 2^LUX_SCALE
    private static final int K6C = 0x019a; // 0.80 * 2^RATIO_SCALE
    private static final int B6C = 0x0101; // 0.0157 * 2^LUX_SCALE
    private static final int M6C = 0x0127; // 0.0180 * 2^LUX_SCALE
    private static final int K7C = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int B7C = 0x0037; // 0.00338 * 2^LUX_SCALE
    private static final int M7C = 0x002b; // 0.00260 * 2^LUX_SCALE
    private static final int K8C = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int B8C = 0x0000; // 0.000 * 2^LUX_SCALE
    private static final int M8C = 0x0000; // 0.000 * 2^LUX_SCALE

    private long selectScale(TSL2561Config.TimingRegister.Integrate integrate, TSL2561Config.TimingRegister.Gain gain) {
        long scale;
        if (integrate.equals(TSL2561Config.TimingRegister.Integrate.CYCLE_101_MS)) {
            scale = SCALE_FOR_101_MS;
        } else if (integrate.equals(TSL2561Config.TimingRegister.Integrate.CYCLE_13_7_MS)) {
            scale = SCALE_FOR_13_7_MS;
        } else {
            scale = NO_SCALE;
        }
        if (gain.equals(TSL2561Config.TimingRegister.Gain.LOW)) {
            scale <<= 4;
        }
        return scale;
    }

    @Override
    public double[] read(Object address, Object... args) {
        log.debug("read() called with: address = [{}]", address);
        I2CAddress i2cAddress = (I2CAddress) address;
        TSL2561Config.TimingRegister timingRegister = config.getTimingRegister();
        try {
            timingRegister.readRegisterValueFromDevice(i2cAddress);
            if (timingRegister.getIntegrate().equals(TSL2561Config.TimingRegister.Integrate.NA)) {
                timingRegister.setManual(TSL2561Config.TimingRegister.Manual.START);
                Thread.sleep(manualTiming);
                timingRegister.setManual(TSL2561Config.TimingRegister.Manual.STOP);
            }
            config.getData0LowRegister().readRegisterValueFromDevice(i2cAddress);
            config.getData0HighRegister().readRegisterValueFromDevice(i2cAddress);
            config.getData1LowRegister().readRegisterValueFromDevice(i2cAddress);
            config.getData1HighRegister().readRegisterValueFromDevice(i2cAddress);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        short ch0 = getTwoByteValue(config.getData0HighRegister(), config.getData0LowRegister());
        short ch1 = getTwoByteValue(config.getData1HighRegister(), config.getData1LowRegister());
        long scale = selectScale(config.getTimingRegister().getIntegrate(), config.getTimingRegister().getGain());

        long channel0 = (scale * ch0) >> CH_SCALE;
        long channel1 = (scale * ch1) >> CH_SCALE;

        long ratio = 0;
        if (channel0 != 0) {
            ratio = (channel1 << (RATIO_SCALE + 1)) / channel0;
        }
        ratio = (ratio + 1) >> 1;

        int b, m;

        if (useCSPackage) {
            if ((ratio >= 0) && (ratio <= K1C)) {
                b = B1C;
                m = M1C;
            } else if (ratio <= K2C) {
                b = B2C;
                m = M2C;
            } else if (ratio <= K3C) {
                b = B3C;
                m = M3C;
            } else if (ratio <= K4C) {
                b = B4C;
                m = M4C;
            } else if (ratio <= K5C) {
                b = B5C;
                m = M5C;
            } else if (ratio <= K6C) {
                b = B6C;
                m = M6C;
            } else if (ratio <= K7C) {
                b = B7C;
                m = M7C;
            } else {
                b = B8C;
                m = M8C;
            }
        } else {
            if ((ratio >= 0) && (ratio <= K1T)) {
                b = B1T;
                m = M1T;
            } else if (ratio <= K2T) {
                b = B2T;
                m = M2T;
            } else if (ratio <= K3T) {
                b = B3T;
                m = M3T;
            } else if (ratio <= K4T) {
                b = B4T;
                m = M4T;
            } else if (ratio <= K5T) {
                b = B5T;
                m = M5T;
            } else if (ratio <= K6T) {
                b = B6T;
                m = M6T;
            } else if (ratio <= K7T) {
                b = B7T;
                m = M7T;
            } else {
                b = B8T;
                m = M8T;
            }
        }

        channel0 *= b;
        channel1 *= m;

        long temp = Math.max(channel0 - channel1, 0);
        temp += (1 << (LUX_SCALE - 1));
        long lux = temp >> LUX_SCALE;
        return new double[] {lux};
    }
}

package ru.funnydwarf.iot.ml.receiver.writer;

import lombok.RequiredArgsConstructor;
import ru.funnydwarf.iot.ml.I2CAddress;
import ru.funnydwarf.iot.ml.register.AddressLedI2CAdapterConfig;
import ru.funnydwarf.iot.ml.register.I2CBlock;

import java.awt.*;
import java.io.IOException;

@RequiredArgsConstructor
public class AddressLedI2CAdapterWriter implements Writer<I2CAddress, AddressLedI2CAdapterData> {

    private final AddressLedI2CAdapterConfig config;

    @Override
    public void write(I2CAddress address, AddressLedI2CAdapterData value) {
        try {
            switch (value.getWriteMode()) {
                case FULL -> {
                    config.getLedCountRegister().setValueAndWriteToDevice(address, value.getLedCount());
                    setMainData(address, value);
                }
                case MAIN_DATA -> setMainData(address, value);
                case PATTERN -> setPattern(address, value);
                case ONLY_LED_COUNT -> config.getLedCountRegister().setValueAndWriteToDevice(address, value.getLedCount());
                case ONLY_BRIGHTNESS -> config.getBrightnessRegister().setValueAndWriteToDevice(address, value.getBrightness());
                case ONLY_UPDATE_DELAY -> config.getUpdateDelayRegister().setValueAndWriteToDevice(address, value.getUpdateDelay());
                case ONLY_PATTERN_COLORS -> setPatternI2CBlock(address, value.getPattern());
                case ONLY_PATTERN_LENGHT -> config.getPatternLengthRegister().setValueAndWriteToDevice(address, value.getPatternLength());
                default -> {
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMainData(I2CAddress address, AddressLedI2CAdapterData value) throws IOException, InterruptedException {
        config.getBrightnessRegister().setValueAndWriteToDevice(address, value.getBrightness());
        config.getUpdateDelayRegister().setValueAndWriteToDevice(address, value.getUpdateDelay());
        setPattern(address, value);
    }

    private void setPattern(I2CAddress address, AddressLedI2CAdapterData value) throws IOException, InterruptedException {
        config.getPatternLengthRegister().setValueAndWriteToDevice(address, value.getPatternLength());
        setPatternI2CBlock(address, value.getPattern());
    }

    private void setPatternI2CBlock(I2CAddress address, Color[] pattern) throws IOException, InterruptedException {
        byte[] bytes = getRGBByteArrayFromColorArray(pattern);
        I2CBlock block = config.getPatternI2CBlock();
        block.setBlockSize(bytes.length);
        block.setRegistersValue(bytes);
        block.writeCurrentRegisterValueToDevice(address);
    }

    private byte[] getRGBByteArrayFromColorArray(Color[] colors) {
        byte[] rgbByteArray = new byte[colors.length * 3];
        for (int i = 0; i < colors.length; i++) {
            int rgb = colors[i].getRGB();
            rgbByteArray[i * 3] = (byte) ((rgb >> 16) & 0b1111_1111);
            rgbByteArray[i * 3 + 1] = (byte) ((rgb >> 8) & 0b1111_1111);
            rgbByteArray[i * 3 + 2] = (byte) (rgb & 0b1111_1111);
        }
        return rgbByteArray;
    }
}
